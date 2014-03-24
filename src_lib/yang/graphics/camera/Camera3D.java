package yang.graphics.camera;

import yang.graphics.camera.projection.OrthogonalProjection;
import yang.graphics.camera.projection.PerspectiveProjection;
import yang.math.MatrixOps;
import yang.math.objects.YangMatrix;

public class Camera3D extends DefaultCamera {

	public void setOrthogonalProjection(float width,float height,float near,float far) {
		OrthogonalProjection.getTransform(mProjectionTransform,-width*0.5f,width*0.5f,height*0.5f,-height*0.5f,near,far);
		mProjectionUpdated = true;
	}

	public void setOrthogonalProjection(float near,float far,float zoom) {
		setOrthogonalProjection(zoom*2,zoom*2,near,far);
	}

	public void setOrthogonalProjection(float near,float far) {
		setOrthogonalProjection(near,far,1);
	}

	public void setOrthogonalProjection() {
		setOrthogonalProjection(mNear,mFar);
	}

	public void setPerspectiveProjection(float fovy, float near, float far,float stretchX) {
		PerspectiveProjection.getTransformFovy(mProjectionTransform,fovy, stretchX,1, near, far);
		mProjectionUpdated = true;
	}

	public void setPerspectiveProjection(float fovy, float near, float far) {
		setPerspectiveProjection(fovy,near,far,1);
	}

	public void setPerspectiveProjection(float fovy) {
		setPerspectiveProjection(fovy,PerspectiveProjection.DEFAULT_NEAR,PerspectiveProjection.DEFAULT_FAR);
	}

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
