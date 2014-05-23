package yang.graphics.util.cameracontrol;

import yang.events.eventtypes.SurfacePointerEvent;
import yang.graphics.camera.Camera2D;
import yang.graphics.camera.YangCamera;
import yang.surface.YangSurface;

public class Camera2DControl extends CameraControl {

	public float mPosX,mPosY;
	public float mMinX,mMinY,mMaxX,mMaxY;
	public Camera2D mCamera;

	public Camera2DControl(YangSurface surface) {
		super(surface);
		this.setBounds(-Float.MAX_VALUE,-Float.MAX_VALUE,Float.MAX_VALUE,Float.MAX_VALUE);
	}

	public void setBounds(float minX,float minY,float maxX,float maxY) {
		mMinX = minX;
		mMaxX = maxX;
		mMinY = minY;
		mMaxY = maxY;
	}

	private void clamp() {
		if(mPosX<mMinX)
			mPosX = mMinX;
		else if(mPosX>mMaxX)
			mPosX = mMaxX;
		if(mPosY<mMinY)
			mPosY = mMinY;
		else if(mPosY>mMaxY)
			mPosY = mMaxY;
	}

	public void setPosition(float x,float y) {
		mPosX = x;
		mPosY = y;
		clamp();
	}

	@Override
	protected void onShift(float deltaX, float deltaY) {
		mPosX += deltaX;
		mPosY += deltaY;
		clamp();
	}

	@Override
	protected void onDrag(SurfacePointerEvent event) {
		mPosX += event.mDeltaX;
		mPosY += event.mDeltaY;
		clamp();
	}

	@Override
	public YangCamera getUpdatedCameraInstance() {
		mCamera.set(mPosX,mPosY, mZoom,0);
		return mCamera;
	}

	@Override
	public YangCamera getCameraInstance() {
		return mCamera;
	}

}
