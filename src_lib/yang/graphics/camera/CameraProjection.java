package yang.graphics.camera;

import yang.math.objects.YangMatrix;


public class CameraProjection extends CameraTransformations {

	private YangMatrix mTempMat = new YangMatrix();

	public void copyFrom(YangCamera camera,YangMatrix postTransform) {

		if(postTransform!=null) {
			//Recalculate with post transform
			mCameraTransform.multiply(camera.mCameraTransform,postTransform);
			mCameraTransform.asInverted(mViewTransform.mValues);
			mViewProjectTransform.multiply(camera.mProjectionTransform,mViewTransform);
			mUnprojectCameraTransform.multiply(mCameraTransform,camera.getUnprojection());
			mCameraTransform.getTranslation(mPosition);
		}else{
			//Pure copy
			camera.updateTransformations(true,true);
			mCameraTransform.set(camera.mCameraTransform);
			mViewTransform.set(camera.mViewTransform);
			mViewProjectTransform.set(camera.mViewProjectTransform);
			mUnprojectCameraTransform.set(camera.mUnprojectCameraTransform);
			mPosition.set(camera.mPosition);
		}
		mProjectionTransform.set(camera.mProjectionTransform.mValues);
		mInvProjectionTransform.set(camera.mInvProjectionTransform.mValues);

	}

//	public void updatePostCameraTransform(YangMatrix postTransform,YangMatrix invPostTransform) {
//		mViewProjectTransform.multiply(mInvProjectionTransform,postTransform);
//		mViewProjectTransform.multiplyRight(mViewTransform);
//		mUnprojectCameraTransform.multiply(mCameraTransform,invPostTransform);
//		mUnprojectCameraTransform.getTranslation(mPosition);
//		mUnprojectCameraTransform.multiplyRight(mProjectionTransform);
//	}
//
//	public void updatePostCameraTransform(YangMatrix postTransform) {
//		postTransform.asInverted(mTempMat.mValues);
//		updatePostCameraTransform(postTransform,mTempMat);
//	}

}
