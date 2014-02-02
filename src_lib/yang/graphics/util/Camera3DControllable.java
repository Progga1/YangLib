package yang.graphics.util;

import yang.events.Keys;
import yang.events.eventtypes.SurfacePointerEvent;
import yang.events.eventtypes.YangEvent;
import yang.events.eventtypes.YangSensorEvent;
import yang.events.listeners.YangEventListener;
import yang.math.MathConst;
import yang.math.objects.Vector3f;
import yang.surface.YangSurface;


public class Camera3DControllable extends Camera3DAlphaBeta implements YangEventListener {

	final float MAX_BETA = MathConst.PI/2-0.01f;

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

	//Objects
	private final YangSurface mSurface;

	//Temp
	private final Vector3f mCamRight = new Vector3f();
	private final Vector3f mCamUp = new Vector3f();

	public Camera3DControllable(YangSurface surface) {
		mSurface = surface;
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
		checkSnap();
	}

	public void setViewAngle(float alpha,float beta) {
		mViewAlpha = alpha;
		mTargetViewAlpha = alpha;
		mViewBeta = beta;
		mTargetViewBeta = beta;
	}

	@Override
	public void pointerDown(float x, float y, SurfacePointerEvent event) {

	}

	private void checkSnap() {
		if(mSurface.mPlaySpeed==0 || mSurface.mPaused) {
			mZoom = mTargetZoom;
			mViewAlpha = mTargetViewAlpha;
			mViewBeta = mTargetViewBeta;
		}
	}

	private float mLstX = Float.MAX_VALUE,mLstY;

	@Override
	public void pointerDragged(float x, float y, SurfacePointerEvent event) {
		if(event.mId==0) {
			if((mCurPointerDownCount==2 || mCurPointerDownCount==3) || event.mButton==mMoveCameraButton || event.mButton==mMoveCameraAlternativeButton) {
				if(mShiftMode || mCurPointerDownCount==2) {
					mCamera.getRightVector(mCamRight);
					mCamera.getUpVector(mCamUp);
					final float fac = -mZoom;
					float deltaX = event.mDeltaX;
					float deltaY = event.mDeltaY;
					if(mCurPointerDownCount==2) {
						final float ux = (event.mInputState.mPointerTrackers[0].mX + event.mInputState.mPointerTrackers[1].mX)*0.5f;
						final float uy = (event.mInputState.mPointerTrackers[0].mY + event.mInputState.mPointerTrackers[1].mY)*0.5f;
						if(mLstX!=Float.MAX_VALUE) {
							deltaX = ux-mLstX;
							deltaY = uy-mLstY;
						}
						mLstX = ux;
						mLstY = uy;
					}
					shiftFocus(fac*(mCamRight.mX*deltaX+mCamUp.mX*deltaY), fac*(mCamRight.mY*deltaX+mCamUp.mY*deltaY), fac*(mCamRight.mZ*deltaX+mCamUp.mZ*deltaY));
				}else{
					mTargetViewAlpha -= event.mDeltaX*2;
					mTargetViewBeta -= event.mDeltaY;
					if(mTargetViewBeta<-MAX_BETA)
						mTargetViewBeta = -MAX_BETA;
					if(mTargetViewBeta>MAX_BETA)
						mTargetViewBeta = MAX_BETA;
				}
			}
		}
		checkSnap();
	}

	@Override
	public void pointerUp(float x, float y, SurfacePointerEvent event) {
		mLstX = Float.MAX_VALUE;
		mCurPointerDownCount--;
		checkSnap();
		if(event.mId>3)
			return;
	}

	@Override
	public void zoom(float value) {
		mTargetZoom += value;
		if(mTargetZoom<mMinZoom)
			mTargetZoom = mMinZoom;
		if(mTargetZoom>mMaxZoom)
			mTargetZoom = mMaxZoom;
		checkSnap();
	}

	@Override
	public void keyDown(int code) {
		if(code == mShiftKey) {
			mShiftMode = true;
		}
		checkSnap();
	}

	@Override
	public void keyUp(int code) {
		if(code == mShiftKey) {
			mShiftMode = false;
		}
		checkSnap();
	}

	@Override
	public boolean rawEvent(YangEvent event) {
		return false;
	}

	@Override
	public void pointerMoved(float x, float y, SurfacePointerEvent event) {

	}

	@Override
	public void sensorChanged(YangSensorEvent event) {

	}

}
