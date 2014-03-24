package yang.graphics.camera;


public class CameraProjection extends CameraTransformations {

	public void copyFrom(YangCamera camera) {
		camera.calcTransformations();
		mViewTransform.set(camera.mViewTransform);
		mCameraTransform.set(camera.mCameraTransform);
		mViewProjectTransform.set(camera.mViewProjectTransform);
		mUnprojectCameraTransform.set(camera.mUnprojectCameraTransform);
		mPosition.set(camera.mPosition);
	}

}
