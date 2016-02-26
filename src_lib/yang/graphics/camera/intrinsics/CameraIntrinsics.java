package yang.graphics.camera.intrinsics;

import yang.math.objects.YangMatrix;

public class CameraIntrinsics {

	public YangMatrix mIntrinsicsMatrix = new YangMatrix();

	public void setFocalLength(float focalLengthX,float focalLengthY) {
		mIntrinsicsMatrix.set(0,0, focalLengthX);
		mIntrinsicsMatrix.set(1,1, focalLengthY);
	}

	public float getFocalLengthX() {
		return mIntrinsicsMatrix.get(0,0);
	}

	public float getFocalLengthY() {
		return mIntrinsicsMatrix.get(1,1);
	}

	public void setSkew(float skew) {
		mIntrinsicsMatrix.set(0,1,skew);
	}

	public float getSkew() {
		return mIntrinsicsMatrix.get(0,1);
	}

	public void setPrincipalPoint(float principalPointX, float principalPointY) {
		mIntrinsicsMatrix.set(0,2, principalPointX);
		mIntrinsicsMatrix.set(1,2, principalPointY);
	}

	public float getPrincipalPointX() {
		return mIntrinsicsMatrix.get(0,2);
	}

	public float getPrincipalPointY() {
		return mIntrinsicsMatrix.get(1,2);
	}

}
