package yang.graphics.defaults;

import yang.graphics.camera.YangCamera;
import yang.graphics.stereovision.StereoVision;
import yang.math.objects.matrix.YangMatrix;
import yang.math.objects.matrix.YangMatrixCameraOps;

public class StereoCamera {

	//TODO unused, implement!

	public YangCamera mBaseCamera;

	private YangMatrix mTransform;

	public float mInterOcularDistance = StereoVision.DEFAULT_INTEROCULAR_DISTANCE;

	public YangMatrixCameraOps mResultTransformLeft = new YangMatrixCameraOps();
	public YangMatrixCameraOps mResultTransformRight = new YangMatrixCameraOps();
	public YangMatrixCameraOps mBaseTransform = new YangMatrixCameraOps();

	public StereoCamera(YangCamera baseCamera) {
		mBaseCamera = baseCamera;
	}

	public void refreshCameras() {

		mBaseCamera.calcResultTransform(mResultTransformLeft,null,mInterOcularDistance*0.5f);
		mBaseCamera.calcResultTransform(mResultTransformRight,null,mInterOcularDistance*0.5f);
	}

}
