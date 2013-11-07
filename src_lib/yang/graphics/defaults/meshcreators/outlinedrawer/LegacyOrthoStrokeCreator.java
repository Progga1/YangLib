package yang.graphics.defaults.meshcreators.outlinedrawer;

import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.defaults.meshcreators.MeshCreator;
import yang.graphics.model.FloatColor;
import yang.graphics.textures.TextureCoordinatesQuad;
import yang.math.MathFunc;

public class LegacyOrthoStrokeCreator extends MeshCreator<DefaultGraphics<?>> {

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

	public LegacyOrthoStrokeCreator(DefaultGraphics<?> graphics,int maxPatchesAndLines,OrthoStrokeProperties properties) {
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
		final float w = area*0.5f;
		for(int i=0;i<mPatchCount;i++) {
			final OrthoStrokePatch patch = mPatches[i];
			if(x>=patch.mX-w && x<=patch.mX+w && y>=patch.mY-w && y<=patch.mY+w)
				return patch;
		}
		return null;
	}

	public OrthoStrokePatch pickOrAddPatch(float x,float y,float area) {
		final OrthoStrokePatch patch = pickPatch(x,y,area);
		if(patch==null)
			return addPatch(x,y);
		else
			return patch;
	}

	protected void handlePatch(OrthoStrokePatch patch) {
		final float w = mProperties.mWidth*0.4f;

		for(int i=0;i<mLineCount;i++) {
			final OrthoStrokeLine line = mLines[i];
			if(!line.mDeleted) {
				if(line.mDeltaX!=0) {
					final float prevEnd = line.getEndX();
					if(MathFunc.equals(line.mY,patch.mY,SNAP_TOLERANCE) && ((line.mX<patch.mX-w && prevEnd>patch.mX+w) || (line.mX>patch.mX+w && prevEnd<patch.mX-w))) {
						splitLine(line,patch.mX-line.mX);
						if(line.mDeltaX>0)
							patch.mInterLines |= OrthoStrokeProperties.LEFT_IN | OrthoStrokeProperties.RIGHT_OUT;
						else
							patch.mInterLines |= OrthoStrokeProperties.LEFT_OUT | OrthoStrokeProperties.RIGHT_IN;
					}
				}
				if(line.mDeltaY!=0) {
					final float prevEnd = line.getEndY();
					if(MathFunc.equals(line.mX,patch.mX,SNAP_TOLERANCE) && ((line.mY<patch.mY-w && prevEnd>patch.mY+w) || (line.mY>patch.mY+w && prevEnd<patch.mY-w))) {
						splitLine(line,patch.mY-line.mY);
						if(line.mDeltaY>0)
							patch.mInterLines |= OrthoStrokeProperties.UP_IN | OrthoStrokeProperties.DOWN_OUT;
						else
							patch.mInterLines |= OrthoStrokeProperties.UP_OUT | OrthoStrokeProperties.DOWN_IN;
					}
				}
			}
		}
	}

	public void deleteLine(OrthoStrokeLine line) {
		if(line.mDeleted)
			return;
		line.mDeleted = true;
		mLineCount--;
		OrthoStrokePatch patch = pickPatch(line.mX, line.mY, SNAP_TOLERANCE);
		if(patch!=null)
			patch.mInterLines &= ~line.getStartPointMask();
		patch = pickPatch(line.getEndX(), line.getEndY(), SNAP_TOLERANCE);
		if(patch!=null)
			patch.mInterLines &= ~line.getEndPointMask();
	}

	protected void checkLineInclusion(OrthoStrokeLine line) {
		if(line.mDeleted)
			return;
		for(int i=0;i<mLineCount;i++) {
			final OrthoStrokeLine otherLine = mLines[i];
			if(!otherLine.mDeleted && line!=otherLine) {
				if(line.mDeltaX!=0) {
					if(otherLine.mDeltaX!=0 && MathFunc.equals(line.mY,otherLine.mY,BIAS) && line.getLeft()>=otherLine.getLeft()-BIAS && line.getRight()<=otherLine.getRight()+BIAS) {
						if(line.getLeft()<=otherLine.getLeft()+BIAS && line.getRight()>=otherLine.getRight()-BIAS) {
							deleteLine(line);
							deleteLine(otherLine);
						}
					}
				}
				if(line.mDeltaY!=0) {
					if(otherLine.mDeltaY!=0 && MathFunc.equals(line.mX,otherLine.mX,BIAS) && line.getBottom()>=otherLine.getBottom()-BIAS && line.getTop()<=otherLine.getTop()+BIAS) {
						deleteLine(line);
					}
				}
			}
		}
	}

	protected void splitLine(OrthoStrokeLine line, float newDelta) {
		if(line.mDeltaX!=0) {
			addLineX(line.mX+newDelta,line.mY,line.mDeltaX-newDelta,false);
			line.mDeltaX = newDelta;
		}
		if(line.mDeltaY!=0) {
			addLineY(line.mX,line.mY+newDelta,line.mDeltaY-newDelta,false);
			line.mDeltaY = newDelta;
		}
	}


	//TODO Complete resolve
	public void resolveIntersections() {
//		for(int i=0;i<mPatchCount;i++) {
//			handlePatch(mPatches[i]);
//		}
		for(int i=0;i<mLineCount;i++) {
			checkLineInclusion(mLines[i]);
		}
		for(int i=0;i<mPatchCount;i++) {
			handlePatch(mPatches[i]);
		}
	}

	private boolean handleLine(OrthoStrokeLine line,boolean updatePatches) {

		if(Math.abs(line.mDeltaX)+Math.abs(line.mDeltaY)<=mProperties.mWidth*1.0001f)
			deleteLine(line);

		if(!updatePatches)
			return !line.mDeleted;

		final OrthoStrokePatch patch1 = pickOrAddPatch(line.mX,line.mY,SNAP_TOLERANCE);
		patch1.mInterLines |= line.getStartPointMask();
		final OrthoStrokePatch patch2 = pickOrAddPatch(line.mX+line.mDeltaX,line.mY+line.mDeltaY,SNAP_TOLERANCE*0.01f);
		patch2.mInterLines |= line.getEndPointMask();

		if(mHandleLineIntersections) {
			//Intersections
			for(int i=0;i<mLineCount;i++) {
				final OrthoStrokeLine oldLine = mLines[i];
				if(!oldLine.mDeleted && oldLine!=line) {
					final float deltaX = line.mX-oldLine.mX;
					final float deltaY = line.mY-oldLine.mY;
					if(line.mDeltaX!=0) {
						if(oldLine.mDeltaY!=0 && deltaY*oldLine.mDeltaY>=0 && Math.abs(deltaY)<Math.abs(oldLine.mDeltaY)) {
							if((line.mX<oldLine.mX && line.mX+line.mDeltaX>oldLine.mX) || (line.mX>oldLine.mX && line.mX+line.mDeltaX<oldLine.mX)) {
								pickOrAddPatch(line.mX-deltaX,line.mY,SNAP_TOLERANCE);
							}
						}
					}
					if(line.mDeltaY!=0) {
						if(oldLine.mDeltaX!=0 && deltaX*oldLine.mDeltaX>=0 && Math.abs(deltaX)<Math.abs(oldLine.mDeltaX)) {
							if((line.mY<oldLine.mY && line.mY+line.mDeltaY>oldLine.mY) || (line.mY>oldLine.mY && line.mY+line.mDeltaY<oldLine.mY)) {
								pickOrAddPatch(line.mX,line.mY-deltaY,SNAP_TOLERANCE);
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
		final OrthoStrokeLine result = addLineX(mCurX,mCurY,distance,true);
		mCurX += distance;
		return result;
	}

	public OrthoStrokeLine marchY(float distance) {
		final OrthoStrokeLine result = addLineY(mCurX,mCurY,distance,true);
		mCurY += distance;
		return result;
	}

	public void drawDebugOutput(float lineWidth) {
		for(int i=0;i<mPatchCount;i++) {
			final OrthoStrokePatch patch = mPatches[i];
			mGraphics.drawRectCentered(patch.mX, patch.mY, mProperties.mWidth);
		}
		for(int i=0;i<mLineCount;i++) {
			final OrthoStrokeLine line = mLines[i];
			mGraphics.drawLine(line.mX, line.mY, line.mX+line.mDeltaX, line.mY+line.mDeltaY, lineWidth);
		}
	}

	public int getVertexCount() {
		return mLineCount*4+mPatchCount*4;
	}

	public void putTexRect(float distance, TextureCoordinatesQuad texCoords) {

		final float BIAS_X = 0.001f;
		int fieldCount = (int)(Math.abs(distance)/mProperties.mWidth-0.5f);
		final boolean alwaysStretch = mProperties.mOffsets==null;
		if(fieldCount>1)
			fieldCount *= mProperties.mStretch;
		float offset;
		if(alwaysStretch)
			offset = 0;
		else
			offset = mProperties.mOffsets[(fieldCount+mProperties.mOffsets.length-1)%mProperties.mOffsets.length]*mProperties.mLineTexFieldWidth;
		final float x1 = texCoords.mLeft+offset+BIAS_X;
		final float x2;
		if(alwaysStretch)
			x2 = texCoords.getBiasedRight();
		else
			x2 = x1+(mProperties.mLineTexFieldWidth*fieldCount*texCoords.mWidth)-BIAS_X;
		final float y2 = texCoords.mTop+texCoords.mHeight;
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
		final float w = mProperties.mWidth*0.5f;
		final float lineW = w*mProperties.mStraightLineWidthFactor;
		final IndexedVertexBuffer vertexBuffer = mGraphics.getCurrentVertexBuffer();
		for(int i=0;i<mLineCount;i++) {
			final OrthoStrokeLine line = mLines[i];
			if(!line.mDeleted) {
				//straight line
				vertexBuffer.beginQuad(false);
				if(line.mDeltaX!=0) {
					if(line.mDeltaX>0) {
						mGraphics.putPosition(line.mX+line.mDeltaX-w, line.mY+lineW);
						mGraphics.putPosition(line.mX+w, line.mY+lineW);
						mGraphics.putPosition(line.mX+line.mDeltaX-w, line.mY-lineW);
						mGraphics.putPosition(line.mX+w, line.mY-lineW);
						putTexRect(line.mDeltaX, mProperties.mLineTexCoords[OrthoStrokeProperties.ID_UP]);
					}else{
						mGraphics.putPosition(line.mX+line.mDeltaX+w, line.mY-lineW);
						mGraphics.putPosition(line.mX-w, line.mY-lineW);
						mGraphics.putPosition(line.mX+line.mDeltaX+w, line.mY+lineW);
						mGraphics.putPosition(line.mX-w, line.mY+lineW);
						putTexRect(line.mDeltaX, mProperties.mLineTexCoords[OrthoStrokeProperties.ID_DOWN]);
					}
				}else {
					if(line.mDeltaY>0) {
						mGraphics.putPosition(line.mX-lineW, line.mY+line.mDeltaY-w);
						mGraphics.putPosition(line.mX-lineW, line.mY+w);
						mGraphics.putPosition(line.mX+lineW, line.mY+line.mDeltaY-w);
						mGraphics.putPosition(line.mX+lineW, line.mY+w);
						putTexRect(line.mDeltaY, mProperties.mLineTexCoords[OrthoStrokeProperties.ID_RIGHT]);
					}else if(line.mDeltaY<0) {
						mGraphics.putPosition(line.mX+lineW, line.mY+line.mDeltaY+w);
						mGraphics.putPosition(line.mX+lineW, line.mY-w);
						mGraphics.putPosition(line.mX-lineW, line.mY+line.mDeltaY+w);
						mGraphics.putPosition(line.mX-lineW, line.mY-w);
						putTexRect(line.mDeltaY, mProperties.mLineTexCoords[OrthoStrokeProperties.ID_LEFT]);
					}
				}
			}
		}
		for(int i=0;i<mPatchCount;i++) {
			final OrthoStrokePatch patch = mPatches[i];
			vertexBuffer.beginQuad(false);
			vertexBuffer.putVec12(DefaultGraphics.ID_POSITIONS,
					patch.mX-w, patch.mY-w, mGraphics.mCurrentZ,
					patch.mX+w, patch.mY-w, mGraphics.mCurrentZ,
					patch.mX-w, patch.mY+w, mGraphics.mCurrentZ,
					patch.mX+w, patch.mY+w, mGraphics.mCurrentZ
					);
			vertexBuffer.putArray(DefaultGraphics.ID_TEXTURES, mProperties.mTexCoordTable[patch.mInterLines].mAppliedCoordinates);
		}
	}

	public void putColors() {
		final int vCount = getVertexCount();
		mGraphics.putColor(mColor.mValues, vCount);
		mGraphics.putSuppData(mGraphics.mCurSuppData, vCount);
	}

	public void drawCompletely() {
		putPositions();
		putColors();
	}

}
