package yang.graphics.camera;

import yang.math.MatrixOps;
import yang.math.objects.YangMatrix;

public class Camera3D extends DefaultCamera {

	public void setLookAt(float eyeX,float eyeY,float eyeZ, float lookAtX,float lookAtY,float lookAtZ, float upX,float upY,float upZ) {
		mPosition.set(eyeX,eyeY,eyeZ);
		MatrixOps.setLookAtInverse(mViewTransform.mValues,eyeX,eyeY,eyeZ, lookAtX,lookAtY,lookAtZ, upX,upY,upZ);
	}

	public void setLookAt(float eyeX,float eyeY,float eyeZ, float lookAtX,float lookAtY,float lookAtZ) {
		mPosition.set(eyeX,eyeY,eyeZ);
		MatrixOps.setLookAtInverse(mViewTransform.mValues,eyeX,eyeY,eyeZ, lookAtX,lookAtY,lookAtZ, 0,1,0);
	}

	public void setLookAtAlphaBeta(float lookAtX, float lookAtY, float lookAtZ, float alpha, float beta, float distance) {
		MatrixOps.setLookAtAlphaBetaInverse(mViewTransform.mValues, lookAtX,lookAtY,lookAtZ,alpha,beta,distance, mPosition);
	}

	public void setViewByTransform(YangMatrix cameraTransform) {
		cameraTransform.getTranslation(mPosition);
		cameraTransform.asInverted(mViewTransform.mValues);
	}

}
