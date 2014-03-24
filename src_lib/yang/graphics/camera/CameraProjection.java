package yang.graphics.camera;

import yang.math.objects.YangMatrix;


public class CameraProjection extends CameraTransformations {

	public YangMatrix mPostCameraTransform;

	public void copyFrom(YangCamera camera) {
		camera.calcTransformations();
		mViewTransform.set(camera.mViewTransform);
		mCameraTransform.set(camera.mCameraTransform);
		if(mPostCameraTransform!=null) {
			mCameraTransform.multiplyRight(mPostCameraTransform);
		}
		mViewProjectTransform.set(camera.mViewProjectTransform);
		mUnprojectCameraTransform.set(camera.mUnprojectCameraTransform);
//		if(camera.mPostUnprojection!=null) {
//			mUnprojectCameraTransform.multiplyRi(camera.mUnprojectCameraTransform);
//		}
		mPosition.set(camera.mPosition);
	}

}
