package yang.graphics.defaults;

import yang.graphics.camera.YangCamera;
import yang.graphics.stereovision.StereoVision;
import yang.math.objects.YangMatrix;

public class StereoCamera {

	//TODO unused, implement!

	public YangCamera mBaseCamera;

	private YangMatrix mTransform;

	public float mInterOcularDistance = StereoVision.DEFAULT_INTEROCULAR_DISTANCE;

	public YangMatrix mResultTransformLeft = new YangMatrix();
	public YangMatrix mResultTransformRight = new YangMatrix();
	public YangMatrix mBaseTransform = new YangMatrix();

	public StereoCamera(YangCamera baseCamera) {
		mBaseCamera = baseCamera;
	}

	public void refreshCameras() {

		mBaseCamera.calcResultTransform(mResultTransformLeft,null,mInterOcularDistance*0.5f);
		mBaseCamera.calcResultTransform(mResultTransformRight,null,mInterOcularDistance*0.5f);
	}

}
