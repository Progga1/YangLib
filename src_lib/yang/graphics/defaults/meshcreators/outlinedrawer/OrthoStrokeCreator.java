package yang.graphics.defaults.meshcreators.outlinedrawer;

import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.defaults.meshcreators.MeshCreator;
import yang.graphics.model.FloatColor;
import yang.graphics.textures.TextureCoordinatesQuad;
import yang.math.MathFunc;

public class OrthoStrokeCreator extends MeshCreator<DefaultGraphics<?>> {

	public static float SNAP_TOLERANCE = 0.001f;
	public static float BIAS = 0.0001f;
	public OrthoStrokePatch[] mPatches;
	public OrthoStrokeLine[] mLines;
	public OrthoStrokeProperties mProperties;
	public int mPatchCount;
	public int mLineCount;
	public float mCurX,mCurY;
	public boolean mHandleLineIntersections = true;
	public FloatColor mColor = FloatColor.WHITE.clone();
	
	public OrthoStrokeCreator(DefaultGraphics<?> graphics,int maxPatchesAndLines,OrthoStrokeProperties properties) {
		super(graphics);
		mProperties = properties;
		mPatches = new OrthoStrokePatch[maxPatchesAndLines];
		mLines = new OrthoStrokeLine[maxPatchesAndLines];
		for(int i=0;i<maxPatchesAndLines;i++) {
			mPatches[i] = new OrthoStrokePatch();
			mLines[i] = new OrthoStrokeLine();
		}
		reset();
	}
	
	public void reset() {
		mPatchCount = 0;
		mLineCount = 0;
		mCurX = 0;
		mCurY = 0;
	}
	
	public void startStroke(float x,float y) {
		mCurX = x;
		mCurY = y;
	}
	
	public OrthoStrokePatch addPatch(float x,float y) {
		return mPatches[mPatchCount++].reset(x,y);
	}
	
	public OrthoStrokePatch pickPatch(float x,float y,float area) {
		float w = area*0.5f;
		for(int i=0;i<mPatchCount;i++) {
			OrthoStrokePatch patch = mPatches[i];
			if(x>=patch.mPosX-w && x<=patch.mPosX+w && y>=patch.mPosY-w && y<=patch.mPosY+w)
				return patch;
		}
		return null;
	}
	
	public OrthoStrokePatch pickOrAddPatch(float x,float y,float area) {
		OrthoStrokePatch patch = pickPatch(x,y,area);
		if(patch==null)
			return addPatch(x,y);
		else
			return patch;
	}
	
	protected void handlePatch(OrthoStrokePatch patch) {
		float w = mProperties.mWidth*0.4f;
		for(int i=0;i<mLineCount;i++) {
			OrthoStrokeLine line = mLines[i];
			if(!line.mDeleted) {
				if(line.mDeltaX!=0) {
					float prevEnd = line.getEndX();
					if(MathFunc.equals(line.mPosY,patch.mPosY,SNAP_TOLERANCE) && ((line.mPosX<patch.mPosX-w && prevEnd>patch.mPosX+w) || (line.mPosX>patch.mPosX+w && prevEnd<patch.mPosX-w))) {
						splitLine(line,patch.mPosX-line.mPosX);
						patch.mInterLines |= OrthoStrokeProperties.LEFT | OrthoStrokeProperties.RIGHT;
					}
				}
				if(line.mDeltaY!=0) {
					float prevEnd = line.getEndY();
					if(MathFunc.equals(line.mPosX,patch.mPosX,SNAP_TOLERANCE) && ((line.mPosY<patch.mPosY-w && prevEnd>patch.mPosY+w) || (line.mPosY>patch.mPosY+w && prevEnd<patch.mPosY-w))) {
						splitLine(line,patch.mPosY-line.mPosY);
						patch.mInterLines |= OrthoStrokeProperties.UP | OrthoStrokeProperties.DOWN;
					}
				}
			}
		}
	}
	
	protected void checkLineInclusion(OrthoStrokeLine line) {
		if(line.mDeleted)
			return;
		for(int i=0;i<mLineCount;i++) {
			OrthoStrokeLine otherLine = mLines[i];
			if(!otherLine.mDeleted && line!=otherLine) {
				if(line.mDeltaX!=0) {
					if(otherLine.mDeltaX!=0 && MathFunc.equals(line.mPosY,otherLine.mPosY,BIAS) && line.getLeft()>=otherLine.getLeft()-BIAS && line.getRight()<=otherLine.getRight()+BIAS) {
						line.mDeleted = true;
					}
				}
				if(line.mDeltaY!=0) {
					if(otherLine.mDeltaY!=0 && MathFunc.equals(line.mPosX,otherLine.mPosX,BIAS) && line.getBottom()>=otherLine.getBottom()-BIAS && line.getTop()<=otherLine.getTop()+BIAS) {
						line.mDeleted = true;
					}
				}
			}
		}
	}
	
	protected void splitLine(OrthoStrokeLine line, float newDelta) {
		if(line.mDeltaX!=0) {
			addLineX(line.mPosX+newDelta,line.mPosY,line.mDeltaX-newDelta,false);
			line.mDeltaX = newDelta;
		}
		if(line.mDeltaY!=0) {
			addLineY(line.mPosX,line.mPosY+newDelta,line.mDeltaY-newDelta,false);
			line.mDeltaY = newDelta;
		}
	}

	public void resolveIntersections() {
		for(int i=0;i<mPatchCount;i++) {
			handlePatch(mPatches[i]);
		}
		for(int i=0;i<mLineCount;i++) {
			checkLineInclusion(mLines[i]);
		}
	}
	
	private boolean handleLine(OrthoStrokeLine line,boolean updatePatches) {
	
		line.mDeleted = Math.abs(line.mDeltaX)+Math.abs(line.mDeltaY)<=mProperties.mWidth*1.0001f;

		if(!updatePatches)
			return !line.mDeleted;
	
		OrthoStrokePatch patch1 = pickOrAddPatch(line.mPosX,line.mPosY,SNAP_TOLERANCE);
		patch1.mInterLines |= line.getStartPointMask();
		OrthoStrokePatch patch2 = pickOrAddPatch(line.mPosX+line.mDeltaX,line.mPosY+line.mDeltaY,SNAP_TOLERANCE*0.01f);
		patch2.mInterLines |= line.getEndPointMask();
		
		if(mHandleLineIntersections) {
			//Intersections
			for(int i=0;i<mLineCount;i++) {
				OrthoStrokeLine oldLine = mLines[i];
				if(!oldLine.mDeleted && oldLine!=line) {
					float deltaX = line.mPosX-oldLine.mPosX;
					float deltaY = line.mPosY-oldLine.mPosY;
					if(line.mDeltaX!=0) {
						if(oldLine.mDeltaY!=0 && deltaY*oldLine.mDeltaY>=0 && Math.abs(deltaY)<Math.abs(oldLine.mDeltaY)) {
							if((line.mPosX<oldLine.mPosX && line.mPosX+line.mDeltaX>oldLine.mPosX) || (line.mPosX>oldLine.mPosX && line.mPosX+line.mDeltaX<oldLine.mPosX)) {
								pickOrAddPatch(line.mPosX-deltaX,line.mPosY,SNAP_TOLERANCE);
							}
						}
					}
					if(line.mDeltaY!=0) {
						if(oldLine.mDeltaX!=0 && deltaX*oldLine.mDeltaX>=0 && Math.abs(deltaX)<Math.abs(oldLine.mDeltaX)) {
							if((line.mPosY<oldLine.mPosY && line.mPosY+line.mDeltaY>oldLine.mPosY) || (line.mPosY>oldLine.mPosY && line.mPosY+line.mDeltaY<oldLine.mPosY)) {
								pickOrAddPatch(line.mPosX,line.mPosY-deltaY,SNAP_TOLERANCE);
							}
						}
					}
				}
			}
		}
		
		
		return !line.mDeleted;
	}
	
	private OrthoStrokeLine lineAdded(OrthoStrokeLine line,boolean updatePatches) {
		if(!handleLine(line,updatePatches)) {
			mLineCount--;
			return null;
		}else{
			return line;
		}
	}
	
	public OrthoStrokeLine addLineX(float startX,float startY,float distance,boolean updatePatches) {
		return lineAdded(mLines[mLineCount++].setX(startX,startY,distance),updatePatches);
	}
	
	public OrthoStrokeLine addLineY(float startX,float startY,float distance,boolean updatePatches) {
		return lineAdded(mLines[mLineCount++].setY(startX,startY,distance),updatePatches);
	}

	public OrthoStrokeLine marchX(float distance) {
		OrthoStrokeLine result = addLineX(mCurX,mCurY,distance,true);
		mCurX += distance;
		return result;
	}
	
	public OrthoStrokeLine marchY(float distance) {
		OrthoStrokeLine result = addLineY(mCurX,mCurY,distance,true);
		mCurY += distance;
		return result;
	}

	public void drawDebugOutput(float lineWidth) {
		for(int i=0;i<mPatchCount;i++) {
			OrthoStrokePatch patch = mPatches[i];
			mGraphics.drawRectCentered(patch.mPosX, patch.mPosY, mProperties.mWidth);
		}
		for(int i=0;i<mLineCount;i++) {
			OrthoStrokeLine line = mLines[i];
			mGraphics.drawLine(line.mPosX, line.mPosY, line.mPosX+line.mDeltaX, line.mPosY+line.mDeltaY, lineWidth);
		}
	}
	
	public int getVertexCount() {
		return mLineCount*4+mPatchCount*4;
	}
	
	public void putTexRect(float distance, TextureCoordinatesQuad texCoords) {
		
		final float BIAS_X = 0.001f;
		int fieldCount = (int)(Math.abs(distance)/mProperties.mWidth-0.5f);
		if(fieldCount>1)
			fieldCount *= mProperties.mStretch;
		float offset = mProperties.mOffsets[(fieldCount+mProperties.mOffsets.length-1)%mProperties.mOffsets.length]*mProperties.mFieldWidth;
		float x1 = texCoords.mLeft+offset+BIAS_X;
		float x2 = x1+(mProperties.mFieldWidth*fieldCount*texCoords.mWidth)-BIAS_X;
		float y2 = texCoords.mTop+texCoords.mHeight;
		mGraphics.mCurrentVertexBuffer.putVec8(DefaultGraphics.ID_TEXTURES,
				x1,texCoords.mTop+mProperties.mTexBias,
				x2,texCoords.mTop+mProperties.mTexBias,
				x1,y2-mProperties.mTexBias,
				x2,y2-mProperties.mTexBias
				);
//		mGraphics.mCurrentVertexBuffer.putVec8(DefaultGraphics.ID_TEXTURES,
//				texCoords.x1,texCoords.y1,
//				texCoords.x2,texCoords.y1,
//				texCoords.x1,texCoords.y2,
//				texCoords.x2,texCoords.y2
//				);
	}

	public void putPositions() {
		float w = mProperties.mWidth*0.5f;
		float lineW = w*mProperties.mStraightLineWidthFactor;
		IndexedVertexBuffer vertexBuffer = mGraphics.getCurrentVertexBuffer();
		for(int i=0;i<mLineCount;i++) {
			OrthoStrokeLine line = mLines[i];
			if(!line.mDeleted) {
				//straight line
				vertexBuffer.beginQuad(false);
				if(line.mDeltaX!=0) {
					if(line.mDeltaX>0) {
						mGraphics.putPosition(line.mPosX+w, line.mPosY-lineW);
						mGraphics.putPosition(line.mPosX+line.mDeltaX-w, line.mPosY-lineW);
						mGraphics.putPosition(line.mPosX+w, line.mPosY+lineW);
						mGraphics.putPosition(line.mPosX+line.mDeltaX-w, line.mPosY+lineW);
						putTexRect(line.mDeltaX, mProperties.mLineTexCoords[1]);
					}else{
						mGraphics.putPosition(line.mPosX-w, line.mPosY+lineW);
						mGraphics.putPosition(line.mPosX+line.mDeltaX+w, line.mPosY+lineW);
						mGraphics.putPosition(line.mPosX-w, line.mPosY-lineW);
						mGraphics.putPosition(line.mPosX+line.mDeltaX+w, line.mPosY-lineW);
						putTexRect(line.mDeltaX, mProperties.mLineTexCoords[3]);
					}
				}else {
					if(line.mDeltaY>0) {
						mGraphics.putPosition(line.mPosX+lineW, line.mPosY+w);
						mGraphics.putPosition(line.mPosX+lineW, line.mPosY+line.mDeltaY-w);
						mGraphics.putPosition(line.mPosX-lineW, line.mPosY+w);
						mGraphics.putPosition(line.mPosX-lineW, line.mPosY+line.mDeltaY-w);
						putTexRect(line.mDeltaY, mProperties.mLineTexCoords[0]);
					}else if(line.mDeltaY<0) {
						mGraphics.putPosition(line.mPosX-lineW, line.mPosY-w);
						mGraphics.putPosition(line.mPosX-lineW, line.mPosY+line.mDeltaY+w);
						mGraphics.putPosition(line.mPosX+lineW, line.mPosY-w);
						mGraphics.putPosition(line.mPosX+lineW, line.mPosY+line.mDeltaY+w);
						putTexRect(line.mDeltaY, mProperties.mLineTexCoords[2]);
					}
				}
			}
		}
		for(int i=0;i<mPatchCount;i++) {
			OrthoStrokePatch patch = mPatches[i];
			vertexBuffer.beginQuad(false);
			vertexBuffer.putVec12(DefaultGraphics.ID_POSITIONS,
					patch.mPosX-w, patch.mPosY-w, mGraphics.mCurrentZ,
					patch.mPosX+w, patch.mPosY-w, mGraphics.mCurrentZ,
					patch.mPosX-w, patch.mPosY+w, mGraphics.mCurrentZ,
					patch.mPosX+w, patch.mPosY+w, mGraphics.mCurrentZ
					);
			vertexBuffer.putArray(DefaultGraphics.ID_TEXTURES, mProperties.mTexCoordTable[patch.mInterLines].mAppliedCoordinates);
		}
	}
	
	public void putColors() {
		int vCount = getVertexCount();
		mGraphics.putColor(mColor.mValues, vCount);
		mGraphics.putSuppData(mGraphics.mCurSuppData, vCount);
	}
	
	public void drawCompletely() {
		putPositions();
		putColors();
	}
	
}
