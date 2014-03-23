package yang.graphics.camera;

import yang.math.objects.Point3f;
import yang.math.objects.Vector3f;
import yang.math.objects.YangMatrix;

public class CameraProjection {

	protected YangMatrix mPostCameraTransform = new YangMatrix();
	protected YangMatrix mViewTransform = new YangMatrix(),mCameraTransform = new YangMatrix();
	protected YangMatrix mViewProjectTransform = new YangMatrix(),mUnprojectCameraTransform = new YangMatrix();
	protected Point3f mPosition = new Point3f();

//	public void update(YangCamera camera) {
//		camera.calcTransformations(this);
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

	public YangMatrix getViewProjReference() {
		return mViewProjectTransform;
	}

	public YangMatrix getUnprojCameraReference() {
		return mUnprojectCameraTransform;
	}

}
