package yang.graphics.camera.projection;

import yang.graphics.camera.intrinsics.CameraIntrinsicsFOV;
import yang.math.objects.YangMatrix;

public class PerspectiveProjection extends Projection {

	public static float DEFAULT_NEAR = 0.02f;
	public static float DEFAULT_FAR = 12f;
	public static float DEFAULT_FOVY = 0.6f;

	public static void getTransform(YangMatrix target, float nearRight,float nearTop, float near,float far) {
		target.setRow(0, near/nearRight, 0, 0, 0);
		target.setRow(1, 0, near/nearTop, 0, 0);
		target.setRow(2, 0,0,-(far+near)/(far-near),-2*far*near/(far-near));
		target.setRow(3, 0,0,-1,0);
	}

	public static void getTransform(YangMatrix target, float nearRight,float nearTop, float near,float far, float shiftX,float shiftY) {
		float nearWidth = (nearRight*2);
		float nearHeight = (nearTop*2);
		shiftX *= nearHeight;
		shiftY *= nearHeight;
		float nearLeft = -nearRight+shiftX;
		nearRight += shiftX;
		float nearBottom = -nearTop+shiftY;
		nearTop += shiftY;
		target.setRow(0, 2*near/(nearRight-nearLeft), 0, (nearRight+nearLeft)/nearWidth, 0);
		target.setRow(1, 0, 2*near/(nearTop-nearBottom), (nearTop+nearBottom)/nearHeight, 0);
		target.setRow(2, 0,0,-(far+near)/(far-near),-2*far*near/(far-near));
		target.setRow(3, 0,0,-1,0);
	}

	public static void getTransformByFactor(YangMatrix target,float factorX,float factorY,float near,float far) {
		getTransform(target,factorX*near,factorY*near,near,far);
	}

	public static void getTransformFov(YangMatrix target,float fovx,float fovy,float near,float far) {
		getTransform(target,(float)Math.tan(fovx)*near,(float)Math.tan(fovy)*near,near,far);
	}

	public static float getTransformFovy(YangMatrix target, float fovy, float ratioX,float ratioY, float near,float far) {
		float res = (float)Math.tan(fovy)*near;
		getTransform(target,res*ratioX,res*ratioY,near,far);
		return res;
	}

	public static float getTransformFovy(YangMatrix target, float fovy, float ratioX,float ratioY, float near,float far, float shiftX,float shiftY) {
		final float tan = (float)Math.tan(fovy);
		float res = tan*near;
		getTransform(target,res*ratioX,res*ratioY,near,far,shiftX,shiftY);
		return res;
	}

	public static void getTransformFovy(YangMatrix target,float fovy,float ratioX,float near, float far) {
		getTransformFovy(target,fovy,ratioX,1,near,far);
	}

	public static void getTransform(YangMatrix target,float near, float far) {
		getTransformFovy(target,DEFAULT_FOVY,1,1,near,far);
	}

	public static void getTransform(YangMatrix target) {
		getTransformFovy(target,DEFAULT_FOVY,1,1,DEFAULT_NEAR,DEFAULT_FAR);
	}

	public static void getTransform(YangMatrix target,CameraIntrinsicsFOV intrinsics) {
		 getTransform(target,intrinsics.mNear*intrinsics.getProjFacX(),intrinsics.mNear*intrinsics.getProjFacY(), intrinsics.mNear,intrinsics.mFar, intrinsics.getFOVShiftX(), intrinsics.getFOVShiftY());
	}

}
