package yang.graphics.util.cameracontrol;

import yang.events.Keys;
import yang.events.eventtypes.SurfacePointerEvent;
import yang.events.eventtypes.YangEvent;
import yang.events.eventtypes.YangSensorEvent;
import yang.events.listeners.YangEventListener;
import yang.graphics.camera.YangCamera;
import yang.surface.YangSurface;

public abstract class CameraControl implements YangEventListener {

	//Settings
	public float mMinZoom = 0.3f;
	public float mMaxZoom = 15f;
	public int mShiftKey = Keys.SHIFT;
	public float mZoomDelay = 0.1f;
	public int mMoveCameraButton = SurfacePointerEvent.BUTTON_MIDDLE;
	public int mMoveCameraAlternativeButton = SurfacePointerEvent.BUTTON_RIGHT;

	//Objects
	protected YangSurface mSurface = null;

	//State
	public float mZoom = 1;
	public float mTargetZoom = 1;
	public boolean mShiftMode = false;
	protected int mCurPointerDownCount = 0;
	private float mLstX = Float.MAX_VALUE,mLstY;

	protected abstract void onShift(float deltaX,float deltaY);
	protected abstract void onDrag(SurfacePointerEvent event);
	public abstract YangCamera getUpdatedCameraInstance();
	public abstract YangCamera getCameraInstance();

	public CameraControl() {
		mTargetZoom = 1;
		mZoom = 1;
	}

	public CameraControl(YangSurface surface) {
		this();
		setSurface(surface);
	}

	public CameraControl setSurface(YangSurface surface) {
		mSurface = surface;
		return this;
	}

	public void step(float deltaTime) {
		mZoom += (mTargetZoom-mZoom)*mZoomDelay;
	}

	public void setZoom(float zoom) {
		mTargetZoom = zoom;
		mZoom = zoom;
	}

	public void snap() {
		mZoom = mTargetZoom;
	}

	protected void checkSnap() {
		if(mSurface!=null && (mSurface.mPlaySpeed==0 || mSurface.mPaused)) {
			snap();
		}
	}


	@Override
	public void pointerDown(float x, float y, SurfacePointerEvent event) {
		mCurPointerDownCount++;
	}

	@Override
	public void pointerDragged(float x, float y, SurfacePointerEvent event) {
		if(event.mId==0) {
			if((mCurPointerDownCount==2 || mCurPointerDownCount==3) || event.mButton==mMoveCameraButton || event.mButton==mMoveCameraAlternativeButton) {
				if(mShiftMode || mCurPointerDownCount==2) {
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
					onShift(deltaX*fac,deltaY*fac);
				}else{
					onDrag(event);
				}
			}
		}
		checkSnap();
	}

	@Override
	public void pointerMoved(float x, float y, SurfacePointerEvent event) {

	}

	@Override
	public void pointerUp(float x, float y, SurfacePointerEvent event) {
		mLstX = Float.MAX_VALUE;
		mCurPointerDownCount--;
		checkSnap();
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
	public void sensorChanged(YangSensorEvent event) {

	}

}
