package yang.graphics.defaults.meshcreators;

import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.translator.GraphicsTranslator;

public class StripCreator {
	
	public DefaultGraphics<?> mGraphics;
	public GraphicsTranslator mTranslator;
	public IndexedVertexBuffer mVertexBuffer;
	
	protected boolean mFstStrip = false;
	protected boolean mStripSinglePoint = false;
	
	public StripCreator(DefaultGraphics<?> graphics) {
		setGraphics(graphics);
	}
	
	public StripCreator() {
		
	}
	
	public void setGraphics(DefaultGraphics<?> graphics) {
		mGraphics = graphics;
		mTranslator = graphics.mTranslator;
	}

	public void startStrip() {
		mFstStrip = true;
		mVertexBuffer = mGraphics.mCurrentVertexBuffer;
	}

	public void startStrip(float x1, float y1, float x2, float y2) {
		mStripSinglePoint = false;
		mVertexBuffer = mGraphics.mCurrentVertexBuffer;
		mGraphics.putPosition(x1, y1);
		mGraphics.putPosition(x2, y2);
	}
	
	public void continueStripSinglePoint() {
		mStripSinglePoint = true;
		if (mFstStrip) {
			mFstStrip = false;
			return;
		}
		mVertexBuffer.putRelativeIndex(-2);
		mVertexBuffer.putRelativeIndex(-1);
		mVertexBuffer.putRelativeIndex(0);
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
			mVertexBuffer.putRelativeIndex(-2);
			mVertexBuffer.putRelativeIndex(-1);
			mVertexBuffer.putRelativeIndex(0);
		}else
			mStripSinglePoint = false;
		mVertexBuffer.putRelativeIndex(1);
		mVertexBuffer.putRelativeIndex(0);
		mVertexBuffer.putRelativeIndex(-1);
	}

	public void continueStrip(float x1, float y1, float x2, float y2) {
		continueStrip();
		mGraphics.putPosition(x1, y1);
		mGraphics.putPosition(x2, y2);
	}
	
}
