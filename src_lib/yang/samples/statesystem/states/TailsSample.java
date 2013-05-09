package yang.samples.statesystem.states;

import yang.events.eventtypes.YangPointerEvent;
import yang.graphics.tail.Tail;
import yang.graphics.textures.TextureSettings;
import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.textures.enums.TextureWrap;
import yang.graphics.translator.Texture;
import yang.samples.statesystem.SampleState;

public class TailsSample extends SampleState {

	private Tail mTail;
	private Texture mTailTexture;
	private float mCurX = Float.MAX_VALUE,mCurY;
	
	@Override
	protected void initGraphics() {
		mTail = new Tail(mGraphics2D,100,true);
		mTail.setColor(0.7f, 0.7f, 0.99f);
		mTailTexture = mGFXLoader.getAlphaMap("trans_invsqrt",new TextureSettings(TextureWrap.REPEAT, TextureWrap.MIRROR,TextureFilter.LINEAR_MIP_LINEAR));
		mTail.setWidth(0.075f);
		mTail.createNodeEveryNthStep(1);
		mTail.mMinDist = 0.0085f;
		mTail.mAutoInterruptSmallDistances = false;
	}
	
	@Override
	public void step(float deltaTime) {
		if(mTail==null)
			return;
		if(mCurX<Float.MAX_VALUE) {
			mTail.mAutoInterruptSmallDistances = true;
			mTail.refreshFront(mCurX, mCurY);
		}else{
			mTail.mAutoInterruptSmallDistances = true;
			mTail.refreshFront();
		}
	}

	@Override
	public void draw() {
		mGraphics.clear(0, 0, 0.1f);
		
		mGraphics.bindTexture(mTailTexture);
		mTail.drawWholeTail();
		
//		mGraphics2D.setWhite();
//		mGraphics.bindTexture(null);
//		for(int i=0;i<mTail.mCapacity;i++) {
//			mGraphics2D.drawRectCentered(mTail.mPosX[i], mTail.mPosY[i], 0.02f);
//		}
		
	}
	
	@Override
	public void pointerDown(float x,float y,YangPointerEvent event) {
		mTail.interruptTail();
	}
	
	@Override
	public void pointerDragged(float x,float y,YangPointerEvent event) {
		//mTail.refreshFront(x, y);
		mCurX = x;
		mCurY = y;
	}
	
	public void pointerUp(float x,float y,YangPointerEvent event) {
		mCurX = Float.MAX_VALUE;
	}

}
