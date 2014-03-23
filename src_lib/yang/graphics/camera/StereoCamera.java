package yang.graphics.camera;

import yang.graphics.stereovision.StereoVision;
import yang.math.objects.YangMatrix;

public class StereoCamera {

	//TODO unused, implement!

	public YangCamera mBaseCamera;

	private YangMatrix mTransform;

	public float mInterOcularDistance = StereoVision.DEFAULT_INTEROCULAR_DISTANCE;

	private CameraProjection mBaseCameraProjection = new CameraProjection();
	private CameraProjection mLeftCameraProjection = new CameraProjection();
	private CameraProjection mRightCameraProjection = new CameraProjection();
	public YangMatrix mBaseTransform = new YangMatrix();

	public StereoCamera(YangCamera baseCamera) {
		mBaseCamera = baseCamera;
		refreshTransforms();
	}

	public void refreshTransforms() {
		mLeftCameraProjection.mPostCameraTransform.setTranslation(-mInterOcularDistance*0.5f,0);
		mRightCameraProjection.mPostCameraTransform.setTranslation(mInterOcularDistance*0.5f,0);
	}

	public void refreshCameras() {
		mLeftCameraProjection.copyFrom(mBaseCamera);
		mRightCameraProjection.copyFrom(mBaseCamera);
	}

}
