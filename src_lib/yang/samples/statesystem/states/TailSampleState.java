package yang.samples.statesystem.states;

import yang.events.eventtypes.YangPointerEvent;
import yang.graphics.tail.Tail;
import yang.graphics.textures.TextureSettings;
import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.textures.enums.TextureWrap;
import yang.graphics.translator.Texture;
import yang.samples.statesystem.SampleState;

public class TailSampleState extends SampleState {

	private Tail mTail;
	private Texture mTailTexture;
	private float mCurX = Float.MAX_VALUE,mCurY;
	private boolean mShowNodes;
	
	@Override
	protected void initGraphics() {
		
	}
	
	@Override
	public void step(float deltaTime) {
		if(mTail==null)
			return;
		if(mCurX<Float.MAX_VALUE) {
			mTail.mAutoInterruptSmallDistances = false;
			mTail.refreshFront(mCurX, mCurY);
		}else{
			mTail.mAutoInterruptSmallDistances = true;
			mTail.refreshFront();
		}
	}

	@Override
	public void draw() {
		mGraphics.clear(0, 0, 0.1f);
		
		if(mTail==null)
			return;
		mGraphics.bindTexture(mTailTexture);
		mTail.drawWholeTail();
		
		if(mShowNodes) {
			mGraphics.bindTexture(mStateSystem.mCircleTexture);
			mGraphics2D.setWhite();
			for(int i=0;i<mTail.mCapacity;i++) {
				mGraphics2D.drawRectCentered(mTail.mPosX[i], mTail.mPosY[i], 0.018f);
			}
			final float DIR_SCALE = mTail.mWidth*0.54f;
			mGraphics2D.setColor(0.6f);
			for(int i=0;i<mTail.mCapacity;i++) {
				mGraphics2D.drawRectCentered(mTail.mPosX[i]+mTail.mDirX[i]*DIR_SCALE, mTail.mPosY[i]+mTail.mDirY[i]*DIR_SCALE, 0.008f);
				mGraphics2D.drawRectCentered(mTail.mPosX[i]-mTail.mDirX[i]*DIR_SCALE, mTail.mPosY[i]-mTail.mDirY[i]*DIR_SCALE, 0.008f);
			}
		}
	}
	
	@Override
	public void pointerDown(float x,float y,YangPointerEvent event) {
		if(mTail==null) {
			mTail = new Tail(mGraphics2D,100,true);
			mTail.setColor(0.7f, 0.7f, 0.99f);
			mTailTexture = mGFXLoader.getAlphaMap("trans_invsqrt",new TextureSettings(TextureWrap.REPEAT, TextureWrap.MIRROR,TextureFilter.LINEAR_MIP_LINEAR));
			mTail.setWidth(0.095f);
			mTail.createNodeEveryNthStep(1);
			mTail.mMinDist = 0.075f;
			mTail.mAutoInterruptSmallDistances = false;
		}
		
		//mTail.interruptTail();
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
	
	@Override
	public void keyUp(int code) {
		super.keyUp(code);
		if(code == 'n')
			mShowNodes ^= true;
		if(code == 'd')
			mTail.debugOut();
	}

}
