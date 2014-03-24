package yang.graphics.camera;

import yang.graphics.stereovision.StereoVision;
import yang.math.objects.YangMatrix;

public class StereoCamera extends StereoVision {

	//TODO unused, implement!

	public YangCamera mBaseCamera;

	private CameraProjection mBaseCameraProjection = new CameraProjection();
	private CameraProjection mLeftCameraProjection = new CameraProjection();
	private CameraProjection mRightCameraProjection = new CameraProjection();
	public YangMatrix mBaseTransform = new YangMatrix();

	public StereoCamera(YangCamera baseCamera) {
		super();
		mBaseCamera = baseCamera;
		refreshTransforms();
	}

	@Override
	public void refreshTransforms() {

	}

	public void refreshCameras() {
		mLeftCameraProjection.mPostCameraTransform = mLeftTransform;
		mLeftCameraProjection.mPostCameraTransform = mRightTransform;
		mLeftCameraProjection.copyFrom(mBaseCamera);
		mRightCameraProjection.copyFrom(mBaseCamera);
	}

}
