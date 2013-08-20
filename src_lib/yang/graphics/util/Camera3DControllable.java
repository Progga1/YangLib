package yang.graphics.util;

import yang.events.Keys;
import yang.events.eventtypes.YangPointerEvent;
import yang.math.MathConst;
import yang.math.objects.Vector3f;


public class Camera3DControllable extends Camera3DAlphaBeta {

	//State
	public float mPntX,mPntY;
	public float mPntDeltaX,mPntDeltaY;
	public boolean mShiftMode = false;

	//Settings
	public float mMinZoom = 0.3f;
	public float mMaxZoom = 10f;
	public int mMoveCameraButton = YangPointerEvent.BUTTON_MIDDLE;
	public int mShiftKey = Keys.SHIFT;
	
	//Temp
	private Vector3f mCamRight = new Vector3f();
	private Vector3f mCamUp = new Vector3f();
	
	public void pointerDown(YangPointerEvent event) {
		mPntX = event.mX;
		mPntY = event.mY;
	}
	
	public void pointerDragged(YangPointerEvent event) {
		mPntDeltaX = event.mX-mPntX;
		mPntDeltaY = event.mY-mPntY;
		mPntX = event.mX;
		mPntY = event.mY;
		if(event.mButton == mMoveCameraButton) {
			if(mShiftMode) {
				mCamera.getRightVector(mCamRight);
				mCamera.getUpVector(mCamUp);
				float fac = -mZoom;
				shiftFocus(fac*(mCamRight.mX*mPntDeltaX+mCamUp.mX*mPntDeltaY), fac*(mCamRight.mY*mPntDeltaY+mCamUp.mY*mPntDeltaY), fac*(mCamRight.mZ*mPntDeltaX+mCamUp.mZ*mPntDeltaY));
			}else{
				mViewAlpha -= mPntDeltaX*2;
				mViewBeta -= mPntDeltaY;
				final float MAX_BETA = MathConst.PI/2-0.01f;
				if(mViewBeta<-MAX_BETA)
					mViewBeta = -MAX_BETA;
				if(mViewBeta>MAX_BETA)
					mViewBeta = MAX_BETA;
			}
		}
	}

	public void zoom(float value) {
		mZoom += value;
		if(mZoom<mMinZoom)
			mZoom = mMinZoom;
		if(mZoom>mMaxZoom)
			mZoom = mMaxZoom;
	}
	
	public void keyDown(int code) {
		if(code == mShiftKey) {
			mShiftMode = true;
		}
	}
	
	public void keyUp(int code) {
		if(code == mShiftKey) {
			mShiftMode = false;
		}
	}
	
}
