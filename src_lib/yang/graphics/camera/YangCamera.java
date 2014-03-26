package yang.graphics.camera;

import yang.graphics.camera.projection.OrthogonalProjection;
import yang.math.objects.Point3f;
import yang.math.objects.Vector3f;
import yang.math.objects.YangMatrix;

public abstract class YangCamera extends CameraTransformations {

	protected YangMatrix mProjectionTransform = new YangMatrix();
	protected YangMatrix mInvProjectionTransform = new YangMatrix();
	protected boolean mProjectionUpdated = true;

	public boolean mAutoRefreshInverted = true;
	public float mNear = OrthogonalProjection.DEFAULT_NEAR;
	public float mFar = OrthogonalProjection.DEFAULT_FAR;

	public YangCamera() {

	}

	float[] mTempMat = new float[16];

	public YangMatrix getUnprojection() {
		if(mProjectionUpdated) {
			mProjectionTransform.asInverted(mInvProjectionTransform.mValues);
			mProjectionUpdated = false;
		}
		return mInvProjectionTransform;
	}

	public void calcTransformations() {

//		mViewProjectTransform.multiply(mProjectionTransform,mViewTransform);
//		mViewProjectTransform.asInverted(mUnprojectCameraTransform.mValues);
//		mViewTransform.asInverted(mCameraTransform.mValues);

		mCameraTransform.asInverted(mViewTransform.mValues);
		mViewProjectTransform.multiply(mProjectionTransform,mViewTransform);
		mUnprojectCameraTransform.multiply(mCameraTransform,getUnprojection());
		mCameraTransform.getTranslation(mPosition);
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

//	public Point3f normToWorld(float normX,float normY,float normZ) {
//		mInvResultTransform.apply3D(normX,normY,normZ, mTempPoint);
//		return mTempPoint;
//	}
//
//	public Point3f worldToNorm(float worldX,float worldY,float worldZ) {
//		mResultTransform.apply3D(worldX,worldY,worldZ, mTempPoint);
//		return mTempPoint;
//	}

	@Override
	public float getX() {
		return mPosition.mX;
	}

	@Override
	public float getY() {
		return mPosition.mY;
	}

	@Override
	public float getZ() {
		return mPosition.mZ;
	}

	@Override
	public Point3f getPositionReference() {
		return mPosition;
	}

}
