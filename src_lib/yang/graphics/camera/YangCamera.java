package yang.graphics.camera;

import yang.math.objects.Point3f;
import yang.math.objects.Vector3f;
import yang.math.objects.matrix.YangMatrix;
import yang.math.objects.matrix.YangMatrixCameraOps;
import yang.model.SurfaceParameters;

public class YangCamera {

	protected YangMatrixCameraOps mProjectionTransform = new YangMatrixCameraOps();
	protected YangMatrixCameraOps mViewTransform = new YangMatrixCameraOps();

	public SurfaceParameters mSurface;
	protected Point3f mPosition = new Point3f();

	public boolean mAutoRefreshInverted = true;

	public YangCamera() {

	}

	public void calcResultTransform(YangMatrix target) {
		target.set(mViewTransform);
		target.multiplyRight(mProjectionTransform);
	}

	public void calcResultTransform(YangMatrix target,YangMatrix postViewTransform,float shiftX) {
		target.set(mViewTransform);
		target.mValues[12] += shiftX;
		if(postViewTransform!=null)
			target.multiplyLeft(postViewTransform);
		target.multiplyLeft(mProjectionTransform);
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

	public float getX() {
		return mPosition.mX;
	}

	public float getY() {
		return mPosition.mY;
	}

	public float getZ() {
		return mPosition.mZ;
	}

	public Point3f getPositionReference() {
		return mPosition;
	}

	public void getRightVector(Vector3f target) {
		final float[] mat = mViewTransform.mValues;
		target.mX = mat[0];
		target.mY = mat[4];
		target.mZ = mat[8];
	}

	public void getUpVector(Vector3f target) {
		final float[] mat = mViewTransform.mValues;
		target.mX = mat[1];
		target.mY = mat[5];
		target.mZ = mat[9];
	}

	public void getForwardVector(Vector3f target) {
		final float[] mat = mViewTransform.mValues;
		target.mX = mat[2];
		target.mY = mat[6];
		target.mZ = mat[10];
	}

	public YangMatrixCameraOps getProjectionTransformReference() {
		return mProjectionTransform;
	}

	public YangMatrix getViewTransformReference() {
		return mViewTransform;
	}

}
