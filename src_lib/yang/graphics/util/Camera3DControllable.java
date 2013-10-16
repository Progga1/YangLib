package yang.graphics.util;

import yang.events.Keys;
import yang.events.eventtypes.SurfacePointerEvent;
import yang.math.MathConst;
import yang.math.objects.Vector3f;


public class Camera3DControllable extends Camera3DAlphaBeta {

	//State
	public boolean mShiftMode = false;
	protected int mCurPointerDownCount = 0;
	public float mTargetZoom = 1;

	//Settings
	public float mMinZoom = 0.3f;
	public float mMaxZoom = 10f;
	public int mMoveCameraButton = SurfacePointerEvent.BUTTON_MIDDLE;
	public int mMoveCameraAlternativeButton = SurfacePointerEvent.BUTTON_RIGHT;
	public int mShiftKey = Keys.SHIFT;
	
	//Temp
	private Vector3f mCamRight = new Vector3f();
	private Vector3f mCamUp = new Vector3f();
	
	public void pointerDown(SurfacePointerEvent event) {
		mCurPointerDownCount++;
		if(event.mId!=0)
			return;
	}
	
	public void step() {
		mZoom += (mTargetZoom-mZoom)*0.1f;
	}
	
	public void setZoom(float zoom) {
		mZoom = zoom;
		mTargetZoom = zoom;
	}
	
	public void pointerDragged(SurfacePointerEvent event) {
		if(event.mId==0) {
			if((mCurPointerDownCount==2 || mCurPointerDownCount==3) || event.mButton==mMoveCameraButton || event.mButton==mMoveCameraAlternativeButton) {
				if(mShiftMode || mCurPointerDownCount==2) {
					mCamera.getRightVector(mCamRight);
					mCamera.getUpVector(mCamUp);
					float fac = -mZoom;
					shiftFocus(fac*(mCamRight.mX*event.mDeltaX+mCamUp.mX*event.mDeltaY), fac*(mCamRight.mY*event.mDeltaX+mCamUp.mY*event.mDeltaY), fac*(mCamRight.mZ*event.mDeltaX+mCamUp.mZ*event.mDeltaY));
				}else{
					mViewAlpha -= event.mDeltaX*2;
					mViewBeta -= event.mDeltaY;
					final float MAX_BETA = MathConst.PI/2-0.01f;
					if(mViewBeta<-MAX_BETA)
						mViewBeta = -MAX_BETA;
					if(mViewBeta>MAX_BETA)
						mViewBeta = MAX_BETA;
				}
			}
		}
	}
	
	public void pointerUp(SurfacePointerEvent event) {
		mCurPointerDownCount--;
		if(event.mId>3)
			return;
	}

	public void zoom(float value) {
		mTargetZoom += value;
		if(mTargetZoom<mMinZoom)
			mTargetZoom = mMinZoom;
		if(mTargetZoom>mMaxZoom)
			mTargetZoom = mMaxZoom;
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
