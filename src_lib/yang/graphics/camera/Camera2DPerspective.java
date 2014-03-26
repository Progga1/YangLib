package yang.graphics.camera;

import yang.graphics.camera.projection.PerspectiveProjection;

public class Camera2DPerspective extends YangCamera {

	private float mRatio = 1;

	public void setPerspectiveProjection(float fovy, float near, float far,float stretchX) {
		mNear = near;
		mFar = far;
		mRatio = near/PerspectiveProjection.getTransformFovy(mProjectionTransform,fovy, stretchX,1, near, far);
	}

	public void setPerspectiveProjection(float fovy, float near, float far) {
		setPerspectiveProjection(fovy,near,far,1);
	}

	public void setPerspectiveProjection(float fovy) {
		setPerspectiveProjection(fovy,PerspectiveProjection.DEFAULT_NEAR,PerspectiveProjection.DEFAULT_FAR);
	}

	public void set(Camera2D camera2D) {
		float zoom = camera2D.mZoom;
		mPosition.mX = camera2D.mPosition.mX;
		mPosition.mY = camera2D.mPosition.mY;
		mPosition.mZ = mRatio*zoom;

		mCameraTransform.setTranslation(mPosition);
		if(camera2D.mRotation!=0) {
			mCameraTransform.rotateZ(camera2D.mRotation);
		}
	}

}
