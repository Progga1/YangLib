package yang.graphics.camera.intrinsics;

import yang.math.objects.YangMatrix;

public class CameraIntrinsics {

	public YangMatrix mIntrinsicsMatrix = new YangMatrix();
	public float mImageWidth = 1;
	public float mImageHeight = 1;

	public void setFocalLength(float focalLengthX,float focalLengthY) {
		mIntrinsicsMatrix.set(0,0, focalLengthX);
		mIntrinsicsMatrix.set(1,1, focalLengthY);
	}

	public void setFocalLength(float focalLength) {
		setFocalLength(focalLength,focalLength);
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

	public void setPrincipalPointNorm(float x,float y) {
		setPrincipalPoint(x*mImageWidth,y*mImageHeight);
	}

	public void setImageDimensions(float imageWidth,float imageHeight) {
		mImageWidth = imageWidth;
		mImageHeight = imageHeight;
	}

	public void setImageParameters(float imageWidth,float imageHeight,float principalPointX, float principalPointY) {
		setImageDimensions(imageWidth,imageHeight);
		setPrincipalPoint(principalPointX,principalPointY);
	}

	public float getImageWidth() {
		return mImageWidth;
	}

	public float getImageHeight() {
		return mImageHeight;
	}

}
