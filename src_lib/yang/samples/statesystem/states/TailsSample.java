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
		mTail = new Tail(mGraphics2D,270,true);
		mTail.setColor(0.5f, 0.5f, 0.7f);
		mTailTexture = mGFXLoader.getAlphaMap("trans_sqrt",new TextureSettings(TextureWrap.REPEAT, TextureWrap.MIRROR,TextureFilter.LINEAR_MIP_LINEAR));
		mTail.setWidth(0.02f);
		mTail.createNodeEveryNthStep(1);
		mTail.mMinDist = 0.03f;
		mTail.mInterruptAtSmallDistances = false;
	}
	
	@Override
	public void step(float deltaTime) {
		if(mTail==null)
			return;
		if(mCurX<Float.MAX_VALUE) {
			mTail.mInterruptAtSmallDistances = false;
			mTail.refreshFront(mCurX, mCurY);
		}else{
			mTail.mInterruptAtSmallDistances = true;
			mTail.refreshFront();
		}
	}

	@Override
	public void draw() {
		mGraphics.clear(0, 0, 0.1f);
		
		mGraphics.bindTexture(mTailTexture);
		mTail.drawWholeTail();
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
