package yang.graphics.camera;

import yang.math.objects.matrix.YangMatrix;

public class Camera3D extends YangCamera {

	public static void setLookAt(YangCamera target,float eyeX,float eyeY,float eyeZ, float lookAtX,float lookAtY,float lookAtZ, float upX,float upY,float upZ) {
		target.mPosition.set(eyeX,eyeY,eyeZ);
		target.mViewTransform.setLookAt(eyeX,eyeY,eyeZ, lookAtX,lookAtY,lookAtZ, upX,upY,upZ);
	}

	public static void setLookAtAlphaBeta(YangCamera target, float lookAtX, float lookAtY, float lookAtZ, float alpha, float beta, float distance) {
		target.mViewTransform.setLookAtAlphaBeta(lookAtX,lookAtY,lookAtZ,alpha,beta,distance, target.mPosition);
	}

	public void setLookAt(float eyeX,float eyeY,float eyeZ, float lookAtX,float lookAtY,float lookAtZ, float upX,float upY,float upZ) {
		mPosition.set(eyeX,eyeY,eyeZ);
		mViewTransform.setLookAt(eyeX,eyeY,eyeZ, lookAtX,lookAtY,lookAtZ, upX,upY,upZ);
	}

	public void setLookAt(float eyeX,float eyeY,float eyeZ, float lookAtX,float lookAtY,float lookAtZ) {
		mPosition.set(eyeX,eyeY,eyeZ);
		mViewTransform.setLookAt(eyeX,eyeY,eyeZ, lookAtX,lookAtY,lookAtZ, 0,1,0);
	}

	public void setViewByTransform(YangMatrix cameraTransform) {
		cameraTransform.getTranslation(mPosition);
		cameraTransform.asInverted(mViewTransform.mValues);
	}

	public static void setByTransform(YangCamera target, YangMatrix cameraTransform) {
		cameraTransform.getTranslation(target.mPosition);
		cameraTransform.asInverted(target.mViewTransform.mValues);
	}



}
