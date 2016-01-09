package yang.graphics.camera;

import yang.graphics.camera.projection.OrthogonalProjection;
import yang.graphics.camera.projection.PerspectiveProjection;
import yang.graphics.model.TransformationData;
import yang.math.MathConst;
import yang.math.MatrixOps;
import yang.math.objects.EulerAngles;
import yang.math.objects.Point3f;

public class Camera3D extends YangCamera {

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
		setOrthogonalProjection(OrthogonalProjection.DEFAULT_NEAR,OrthogonalProjection.DEFAULT_FAR);
	}

	public void setPerspectiveProjection(float fovy, float near, float far,float stretchX) {
		PerspectiveProjection.getTransformFovy(mProjectionTransform,fovy, stretchX,1, near,far);
		mProjectionUpdated = true;
	}

	public void setPerspectiveProjection(float fovy, float near, float far,float stretchX,float shiftX,float shiftY) {
		PerspectiveProjection.getTransformFovy(mProjectionTransform, fovy, stretchX,1, near,far, shiftX,shiftY);
		mProjectionUpdated = true;
	}

	public void setPerspectiveProjection(float fovy, float near, float far) {
		setPerspectiveProjection(fovy,near,far,1);
	}

	public void setPerspectiveProjection(float fovy, float range) {
		setPerspectiveProjection(fovy,PerspectiveProjection.DEFAULT_NEAR,PerspectiveProjection.DEFAULT_NEAR+range);
	}

	public void setPerspectiveProjection(float fovy) {
		setPerspectiveProjection(fovy,PerspectiveProjection.DEFAULT_NEAR,PerspectiveProjection.DEFAULT_FAR);
	}

	public void setLookAt(float eyeX,float eyeY,float eyeZ, float lookAtX,float lookAtY,float lookAtZ, float upX,float upY,float upZ) {
		mPosition.set(eyeX,eyeY,eyeZ);
		MatrixOps.setLookAt(mCameraTransform.mValues,eyeX,eyeY,eyeZ, lookAtX,lookAtY,lookAtZ, upX,upY,upZ);
	}

	public void setLookAt(float eyeX,float eyeY,float eyeZ, float lookAtX,float lookAtY,float lookAtZ) {
		setLookAt(eyeX,eyeY,eyeZ, lookAtX,lookAtY,lookAtZ, 0,1,0);
	}

	public void setLookAt(Point3f eye,Point3f lookAt) {
		setLookAt(eye.mX,eye.mY,eye.mZ, lookAt.mX,lookAt.mY,lookAt.mZ);
	}

	public void setLookAt(float eyeX,float eyeY,float eyeZ, float lookAtX,float lookAtY,float lookAtZ, float roll) {
		mPosition.set(eyeX,eyeY,eyeZ);
		MatrixOps.setLookAt(mCameraTransform.mValues,eyeX,eyeY,eyeZ, lookAtX,lookAtY,lookAtZ, 0,1,0);
		if(roll!=0)
			mCameraTransform.rotateZ(roll);
	}

	public void setLookAt(Point3f eye,Point3f lookAt, float roll) {
		setLookAt(eye.mX,eye.mY,eye.mZ, lookAt.mX,lookAt.mY,lookAt.mZ, roll);
	}

	public void setLookAtAlphaBeta(float alpha, float beta, float distance, float lookAtX, float lookAtY, float lookAtZ) {
		MatrixOps.setLookAtAlphaBeta(mCameraTransform.mValues, lookAtX,lookAtY,lookAtZ,alpha,beta,distance, mPosition);
	}

	public void setLookAtAlphaBeta(float alpha, float beta, float distance, Point3f lookAtPoint) {
		MatrixOps.setLookAtAlphaBeta(mCameraTransform.mValues, lookAtPoint.mX,lookAtPoint.mY,lookAtPoint.mZ,alpha,beta,distance, mPosition);
	}

	public void setLookAtAlphaBeta(float alpha, float beta, float distance) {
		setLookAtAlphaBeta(alpha,beta, distance, 0,0,0);
	}

	public void setLookAtAlphaBeta(float alpha, float beta, float roll, float distance, Point3f lookAtPoint) {
		MatrixOps.setLookAtAlphaBeta(mCameraTransform.mValues, lookAtPoint.mX,lookAtPoint.mY,lookAtPoint.mZ,alpha,beta,distance, mPosition);
		if(roll!=0)
			mCameraTransform.rotateZ(roll);
	}

	public void setLookAtAlphaBeta(EulerAngles angles, float distance, Point3f focus) {
		setLookAtAlphaBeta(angles.mYaw,angles.mPitch,angles.mRoll, distance,focus);
	}

	public void setLookOutwardsAlphaBeta(float alpha, float beta, float roll, float distance, float pivotX,float pivotY,float pivotZ) {
		alpha += MathConst.PI;
		beta *= -1;
		setLookAt(pivotX,pivotY,pivotZ,
				pivotX+(float)(Math.sin(alpha)*Math.cos(beta))*distance,
				pivotY+(float)Math.sin(beta)*distance,
				pivotZ+(float)(Math.cos(alpha)*Math.cos(beta))*distance,
				0,1,0);
		if(roll!=0)
			mCameraTransform.rotateZ(roll);
	}

	public void setLookOutwardsAlphaBeta(float alpha, float beta, float distance, Point3f pivot) {
		setLookOutwardsAlphaBeta(alpha,beta,0, distance, pivot.mX,pivot.mY,pivot.mZ);
	}

	public void setLookOutwardsAlphaBeta(EulerAngles angles,float distance, Point3f pivot) {
		setLookOutwardsAlphaBeta(angles.mYaw,angles.mPitch,angles.mRoll, distance, pivot.mX,pivot.mY,pivot.mZ);
	}

	public void setByTransform(TransformationData transform) {
		mCameraTransform.loadIdentity();
		mCameraTransform.translate(transform.mPosition);
		mCameraTransform.multiplyQuaternionRight(transform.mOrientation);
	}

}
