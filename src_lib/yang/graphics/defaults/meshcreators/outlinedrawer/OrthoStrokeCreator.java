package yang.graphics.defaults.meshcreators.outlinedrawer;

import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.defaults.meshcreators.MeshCreator;

public class OrthoStrokeCreator extends MeshCreator<DefaultGraphics<?>> {

	public static float SNAP_TOLERANCE = 0.001f;
	public OrthoStrokePatch[] mPatches;
	public OrthoStrokeLine[] mLines;
	public OrthoStrokeProperties mProperties;
	public int mPatchCount;
	public int mLineCount;
	private float mCurX,mCurY;
	
	public OrthoStrokeCreator(DefaultGraphics<?> graphics,int capacity,OrthoStrokeProperties properties) {
		super(graphics);
		mProperties = properties;
		mPatches = new OrthoStrokePatch[capacity];
		mLines = new OrthoStrokeLine[capacity];
		for(int i=0;i<capacity;i++) {
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
	
	private boolean handleLine(OrthoStrokeLine line) {
		float w = mProperties.mWidth*0.5f;
		OrthoStrokePatch patch = pickOrAddPatch(line.mPosX,line.mPosY,SNAP_TOLERANCE);
		patch.mInterLines |= line.getStartPointMask();
		patch = pickOrAddPatch(line.mPosX+line.mDeltaX,line.mPosY+line.mDeltaY,SNAP_TOLERANCE*0.01f);
		patch.mInterLines |= line.getEndPointMask();
		
		return true;
	}
	
	private OrthoStrokeLine lineAdded(OrthoStrokeLine line) {
		if(!handleLine(line)) {
			
			return null;
		}else{
			mLineCount++;
			return line;
		}
	}
	
	public OrthoStrokeLine addLineX(float startX,float startY,float distance) {
		return lineAdded(mLines[mLineCount].setX(startX,startY,distance));
	}
	
	public OrthoStrokeLine addLineY(float startX,float startY,float distance) {
		return lineAdded(mLines[mLineCount].setY(startX,startY,distance));
	}

	public OrthoStrokeLine marchX(float distance) {
		OrthoStrokeLine result = addLineX(mCurX,mCurY,distance);
		mCurX += distance;
		return result;
	}
	
	public OrthoStrokeLine marchY(float distance) {
		OrthoStrokeLine result = addLineY(mCurX,mCurY,distance);
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

	public void draw() {
		float w = mProperties.mWidth*0.5f;
		IndexedVertexBuffer vertexBuffer = mGraphics.getCurrentVertexBuffer();
		for(int i=0;i<mLineCount;i++) {
			OrthoStrokeLine line = mLines[i];
			vertexBuffer.beginQuad(false);
			if(line.mDeltaX!=0) {
				if(line.mDeltaX>0) {
					mGraphics.putPosition(line.mPosX+w, line.mPosY-w);
					mGraphics.putPosition(line.mPosX+line.mDeltaX-w, line.mPosY-w);
					mGraphics.putPosition(line.mPosX+w, line.mPosY+w);
					mGraphics.putPosition(line.mPosX+line.mDeltaX-w, line.mPosY+w);
				}else{
					mGraphics.putPosition(line.mPosX-w, line.mPosY+w);
					mGraphics.putPosition(line.mPosX+line.mDeltaX+w, line.mPosY+w);
					mGraphics.putPosition(line.mPosX-w, line.mPosY-w);
					mGraphics.putPosition(line.mPosX+line.mDeltaX+w, line.mPosY-w);
				}
			}else{
				if(line.mDeltaY>0) {
					mGraphics.putPosition(line.mPosX+w, line.mPosY+w);
					mGraphics.putPosition(line.mPosX+w, line.mPosY+line.mDeltaY-w);
					mGraphics.putPosition(line.mPosX-w, line.mPosY+w);
					mGraphics.putPosition(line.mPosX-w, line.mPosY+line.mDeltaY-w);
				}else{
					mGraphics.putPosition(line.mPosX-w, line.mPosY-w);
					mGraphics.putPosition(line.mPosX-w, line.mPosY+line.mDeltaY+w);
					mGraphics.putPosition(line.mPosX+w, line.mPosY-w);
					mGraphics.putPosition(line.mPosX+w, line.mPosY+line.mDeltaY+w);
				}
			}
		}
	}
	
}
