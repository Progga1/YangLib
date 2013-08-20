package yang.graphics.util;

import yang.events.eventtypes.YangPointerEvent;
import yang.math.MathConst;

public class Camera3DControllable extends Camera3DAlphaBeta {

	public float mPntX,mPntY;
	public float mPntDeltaX,mPntDeltaY;
	
	public void pointerDown(YangPointerEvent event) {
		mPntX = event.mX;
		mPntY = event.mY;
	}
	
	public void pointerDragged(YangPointerEvent event) {
		mPntDeltaX = event.mX-mPntX;
		mPntDeltaY = event.mY-mPntY;
		mPntX = event.mX;
		mPntY = event.mY;
		if(event.mButton == YangPointerEvent.BUTTON_RIGHT) {
			mViewAlpha -= mPntDeltaX*2;
			mViewBeta -= mPntDeltaY;
			final float MAX_BETA = MathConst.PI/2-0.01f;
			if(mViewBeta<-MAX_BETA)
				mViewBeta = -MAX_BETA;
			if(mViewBeta>MAX_BETA)
				mViewBeta = MAX_BETA;
		}		
	}

	public void zoom(float value) {
		mZoom += value;
		if(mZoom<0.3f)
			mZoom = 0.3f;
		if(mZoom>10f)
			mZoom = 10f;
	}
	
}
