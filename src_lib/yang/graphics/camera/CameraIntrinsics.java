package yang.graphics.camera;

import yang.math.objects.YangMatrix;

public class CameraIntrinsics {

	public YangMatrix mMatrix = new YangMatrix();

	public void setFocalLength(float focalLengthX,float focalLengthY) {
		mMatrix.set(0,0, focalLengthX);
		mMatrix.set(1,1, focalLengthY);
	}

	public float getFocalLengthX() {
		return mMatrix.get(0,0);
	}

	public float getFocalLengthY() {
		return mMatrix.get(1,1);
	}

	public void setSkew(float skew) {
		mMatrix.set(0,1,skew);
	}

	public float getSkew() {
		return mMatrix.get(0,1);
	}

	public void setPrincipalPoint(float principalPointX, float principalPointY) {
		mMatrix.set(0,2, principalPointX);
		mMatrix.set(1,2, principalPointY);
	}



}
