package yang.graphics.defaults.meshcreators;

import yang.graphics.defaults.DefaultGraphics;

public class DoubleStripCreator extends StripCreator {
	
	public DoubleStripCreator(DefaultGraphics<?> graphics) {
		setGraphics(graphics);
	}
	
	public void startStrip(float x1, float y1, float x2, float y2,float middleX,float middleY) {
		mStripSinglePoint = false;
		mVertexBuffer = mGraphics.mCurrentVertexBuffer;
		mGraphics.putPosition(x1, y1);
		mGraphics.putPosition(x2, y2);
		mGraphics.putPosition(middleX,middleY);
	}
	
	@Override
	public void startStrip(float x1, float y1, float x2, float y2) {
		startStrip(x1,y1,x2,y2,(x1+x2)*0.5f,(y1+y2)*0.5f);
	}
	
	public void continueStripSinglePoint() {
		mStripSinglePoint = true;
		if (mFstStrip) {
			mFstStrip = false;
			return;
		}
		mVertexBuffer.putRelativeIndex(-1);
		mVertexBuffer.putRelativeIndex(0);
		mVertexBuffer.putRelativeIndex(-2);
		mVertexBuffer.putRelativeIndex(-3);
		mVertexBuffer.putRelativeIndex(0);
		mVertexBuffer.putRelativeIndex(-1);
	}
	
	public void continueStripSinglePoint(float x,float y) {
		continueStripSinglePoint();
		mGraphics.putPosition(x,y);
	}
	
	public void continueStrip() {
		if (mFstStrip) {
			mFstStrip = false;
			return;
		}
		if(!mStripSinglePoint) {
			mVertexBuffer.putRelativeIndex(-3);
			mVertexBuffer.putRelativeIndex(0);
			mVertexBuffer.putRelativeIndex(-1);
			mVertexBuffer.putRelativeIndex(1);
			mVertexBuffer.putRelativeIndex(-2);
			mVertexBuffer.putRelativeIndex(-1);
		}else
			mStripSinglePoint = false;
		mVertexBuffer.putRelativeIndex(2);
		mVertexBuffer.putRelativeIndex(-1);
		mVertexBuffer.putRelativeIndex(0);
		mVertexBuffer.putRelativeIndex(-1);
		mVertexBuffer.putRelativeIndex(2);
		mVertexBuffer.putRelativeIndex(1);
	}
	
	public void continueStrip(float x1, float y1, float x2, float y2,float middleX,float middleY) {
		continueStrip();
		mGraphics.putPosition(x1, y1);
		mGraphics.putPosition(x2, y2);
		mGraphics.putPosition(middleX,middleY);
	}
	
	@Override
	public void continueStrip(float x1, float y1, float x2, float y2) {
		continueStrip(x1,y1,x2,y2,(x1+x2)*0.5f,(y1+y2)*0.5f);
	}
	
}
