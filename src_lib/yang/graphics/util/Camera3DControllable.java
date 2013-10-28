package yang.graphics.util;

import yang.events.Keys;
import yang.events.eventtypes.SurfacePointerEvent;
import yang.math.MathConst;
import yang.math.objects.Vector3f;


public class Camera3DControllable extends Camera3DAlphaBeta {

	//State
	public boolean mShiftMode = false;
	protected int mCurPointerDownCount = 0;
	public float mTargetZoom = 1,mTargetViewAlpha,mTargetViewBeta;

	//Settings
	public float mMinZoom = 0.3f;
	public float mMaxZoom = 15f;
	public float mZoomDelay = 0.1f,mViewDelay = 0.3f;
	public int mMoveCameraButton = SurfacePointerEvent.BUTTON_MIDDLE;
	public int mMoveCameraAlternativeButton = SurfacePointerEvent.BUTTON_RIGHT;
	public int mShiftKey = Keys.SHIFT;

	//Temp
	private final Vector3f mCamRight = new Vector3f();
	private final Vector3f mCamUp = new Vector3f();

	public Camera3DControllable() {
		setZoom(1);
	}

	public void pointerDown(SurfacePointerEvent event) {
		mCurPointerDownCount++;
		if(event.mId!=0)
			return;
	}

	public void step() {
		mZoom += (mTargetZoom-mZoom)*mZoomDelay;
		mViewAlpha += (mTargetViewAlpha-mViewAlpha)*mViewDelay;
		mViewBeta += (mTargetViewBeta-mViewBeta)*mViewDelay;
	}

	public void setZoom(float zoom) {
		if(mInvertView)
			return;
		mZoom = zoom;
		mTargetZoom = zoom;
	}

	public void setViewAngle(float alpha,float beta) {
		mViewAlpha = alpha;
		mTargetViewAlpha = alpha;
		mViewBeta = beta;
		mTargetViewBeta = beta;
	}

	private float mLstX = Float.MAX_VALUE,mLstY;

	public void pointerDragged(SurfacePointerEvent event) {
		if(event.mId==0) {
			if((mCurPointerDownCount==2 || mCurPointerDownCount==3) || event.mButton==mMoveCameraButton || event.mButton==mMoveCameraAlternativeButton) {
				if(mShiftMode || mCurPointerDownCount==2) {
					mCamera.getRightVector(mCamRight);
					mCamera.getUpVector(mCamUp);
					final float fac = -mZoom;
					float deltaX = event.mDeltaX;
					float deltaY = event.mDeltaY;
					if(mCurPointerDownCount==2) {
						final float x = (event.mEventQueue.mPointerTrackers[0].mX + event.mEventQueue.mPointerTrackers[1].mX)*0.5f;
						final float y = (event.mEventQueue.mPointerTrackers[0].mY + event.mEventQueue.mPointerTrackers[2].mY)*0.5f;
						if(mLstX!=Float.MAX_VALUE) {
							deltaX = x-mLstX;
							deltaY = y-mLstY;
						}
						mLstX = x;
						mLstY = y;
					}
					shiftFocus(fac*(mCamRight.mX*deltaX+mCamUp.mX*deltaY), fac*(mCamRight.mY*deltaX+mCamUp.mY*deltaY), fac*(mCamRight.mZ*deltaX+mCamUp.mZ*deltaY));
				}else{
					mTargetViewAlpha -= event.mDeltaX*2;
					mTargetViewBeta -= event.mDeltaY;
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
		mLstX = Float.MAX_VALUE;
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
