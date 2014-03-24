package yang.graphics.camera;

import yang.graphics.camera.projection.PerspectiveProjection;

public class Camera2DPerspective extends DefaultCamera {

	private float mNearTop = 1;
	private float mRatio = 1;

	public void setPerspectiveProjection(float fovy, float near, float far,float stretchX) {
		mNearTop = PerspectiveProjection.getTransformFovy(mProjectionTransform,fovy, stretchX,1, near, far);
		mRatio = near/mNearTop;
		mProjectionUpdated = true;

	}

	public void setPerspectiveProjection(float fovy, float near, float far) {
		setPerspectiveProjection(fovy,near,far,1);
	}

	public void setPerspectiveProjection(float fovy) {
		setPerspectiveProjection(fovy,PerspectiveProjection.DEFAULT_NEAR,PerspectiveProjection.DEFAULT_FAR);
	}

	public void set(Camera2D camera2D) {
		//TODO invert
//		YangMatrix temp = new YangMatrix();
//		temp.translate(camera2D.getPositionReference());
//		temp.rot
		float zoom = camera2D.mPosition.mZ;
		mPosition.mX = camera2D.mPosition.mX;
		mPosition.mY = camera2D.mPosition.mY;
		mPosition.mZ = mRatio*zoom;

		mViewTransform.setTranslationNegative(mPosition);
	}

}
