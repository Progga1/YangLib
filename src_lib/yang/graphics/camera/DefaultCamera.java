package yang.graphics.camera;

import yang.math.objects.Vector3f;
import yang.math.objects.YangMatrix;

public class DefaultCamera extends YangCamera {

	float[] mTempMat = new float[16];

	@Override
	public void calcTransformations() {

//		mViewProjectTransform.multiply(mProjectionTransform,mViewTransform);
//		mViewProjectTransform.asInverted(mUnprojectCameraTransform.mValues);
//		mViewTransform.asInverted(mCameraTransform.mValues);

		mCameraTransform.asInverted(mViewTransform.mValues);
		mViewProjectTransform.multiply(mProjectionTransform,mViewTransform);
		mUnprojectCameraTransform.multiply(mCameraTransform,getUnprojection());
	}

	@Override
	public void getRightVector(Vector3f target) {
		final float[] mat = mViewTransform.mValues;
		target.mX = mat[0];
		target.mY = mat[4];
		target.mZ = mat[8];
	}

	@Override
	public void getUpVector(Vector3f target) {
		final float[] mat = mViewTransform.mValues;
		target.mX = mat[1];
		target.mY = mat[5];
		target.mZ = mat[9];
	}

	@Override
	public void getForwardVector(Vector3f target) {
		final float[] mat = mViewTransform.mValues;
		target.mX = mat[2];
		target.mY = mat[6];
		target.mZ = mat[10];
	}

	public void setProjectionUpdated() {
		mProjectionUpdated = true;
	}

	public YangMatrix getProjectionTransformReference() {
		return mProjectionTransform;
	}

	@Override
	public YangMatrix getViewTransformReference() {
		return mViewTransform;
	}

}
