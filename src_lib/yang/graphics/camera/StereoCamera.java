package yang.graphics.camera;

import yang.graphics.stereovision.StereoVision;

public class StereoCamera extends StereoVision {

	public YangCamera mBaseCamera;

	private CameraProjection mBaseCameraProjection = new CameraProjection();
	private CameraProjection mLeftCameraProjection = new CameraProjection();
	private CameraProjection mRightCameraProjection = new CameraProjection();

	public StereoCamera(YangCamera baseCamera) {
		super();
		mBaseCamera = baseCamera;
	}

	public void refreshCameras() {
		mLeftCameraProjection.copyFrom(mBaseCamera,mLeftResultTransform);
		mRightCameraProjection.copyFrom(mBaseCamera,mRightResultTransform);
	}

}
