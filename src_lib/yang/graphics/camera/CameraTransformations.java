package yang.graphics.camera;

import yang.math.MatrixOps;
import yang.math.objects.Point3f;
import yang.math.objects.Vector3f;
import yang.math.objects.YangMatrix;

public abstract class CameraTransformations {

	protected YangMatrix mViewTransform = new YangMatrix(),mCameraTransform = new YangMatrix();
	protected YangMatrix mViewProjectTransform = new YangMatrix(),mUnprojectCameraTransform = new YangMatrix();
	protected YangMatrix mProjectionTransform = new YangMatrix(),mInvProjectionTransform = new YangMatrix();
	protected Point3f mPosition = new Point3f();

	public YangMatrix getProjectionTransformReference() {
		return mProjectionTransform;
	}

	public float getX() {
		return mPosition.mX;
	}

	public float getY() {
		return mPosition.mY;
	}

	public float getZ() {
		return mPosition.mZ;
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



	public Point3f getPositionReference() {
		return mPosition;
	}

	public YangMatrix getViewTransformReference() {
		return mViewTransform;
	}

	public YangMatrix getCameraTransformReference() {
		return mCameraTransform;
	}

	public YangMatrix getViewProjectReference() {
		return mViewProjectTransform;
	}

	public YangMatrix getResultTransformReference() {
		return mViewProjectTransform;
	}

	public YangMatrix getUnprojectCameraReference() {
		return mUnprojectCameraTransform;
	}

	public float worldToNorm2DX(float worldX,float worldY) {
		return MatrixOps.applyFloatMatrixX2D(mViewProjectTransform.mValues, worldX, worldY);
	}

	public float worldToNorm2DY(float worldX,float worldY) {
		return MatrixOps.applyFloatMatrixY2D(mViewProjectTransform.mValues, worldX, worldY);
	}

	public float normToWorld2DX(float normX,float normY) {
		return MatrixOps.applyFloatMatrixX2D(mUnprojectCameraTransform.mValues, normX, normY);
	}

	public float normToWorld2DY(float normX,float normY) {
		return MatrixOps.applyFloatMatrixY2D(mUnprojectCameraTransform.mValues, normX, normY);
	}

	public void worldToNorm(float x,float y,float z,Point3f target) {
		mViewProjectTransform.apply3DNormalized(x, y, z, target);
	}

	public void normToWorld(float x,float y,float z,Point3f target) {
		mUnprojectCameraTransform.apply3DNormalized(x, y, z, target);
	}
}
