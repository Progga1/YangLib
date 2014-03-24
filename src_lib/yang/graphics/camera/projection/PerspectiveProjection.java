package yang.graphics.camera.projection;

import yang.math.objects.YangMatrix;

public class PerspectiveProjection extends Projection {

	public static float DEFAULT_NEAR = 0.02f;
	public static float DEFAULT_FAR = 12f;
	public static float DEFAULT_FOVY = 0.6f;

	public static void getTransform(YangMatrix target,float nearRight,float nearTop,float near, float far) {
		target.setRow(0, near/nearRight, 0, 0, 0);
		target.setRow(1, 0, near/nearTop, 0, 0);
		target.setRow(2, 0,0,-(far+near)/(far-near),-2*far*near/(far-near));
		target.setRow(3, 0,0,-1,0);
	}

	public static float getTransformFovy(YangMatrix target,float fovy,float ratioX,float ratioY,float near, float far) {
		final float tan = (float)Math.tan(fovy);
		float res = tan*near;
		getTransform(target,res*ratioX,res*ratioY,near,far);
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

}
