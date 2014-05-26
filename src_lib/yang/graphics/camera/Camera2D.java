package yang.graphics.camera;

import yang.graphics.camera.projection.OrthogonalProjection;

public class Camera2D extends YangCamera {

	public float mZoom;
	public float mRotation;
	public float mPreShiftX = 0,mPreShiftY = 0;
	public boolean mPreShiftEnabled = false;

	public Camera2D() {
		super();
		mPosition.mZ = 1;
		mZoom = 1;
		reset();
	}

	public void reset() {
		mPosition.mX = 0;
		mPosition.mY = 0;
		mPosition.mZ = 1;
		mZoom = 1;
		mRotation = 0;
		mNear = OrthogonalProjection.DEFAULT_NEAR;
		mFar = OrthogonalProjection.DEFAULT_FAR;
		mViewTransform.loadIdentity();
		refreshViewTransform();
		refreshProjectionTransform();
	}

	public void refreshViewTransform() {
//		if(mRotation!=0) {
//			MatrixOps.setRotationZ(mViewTransform.mValues,mRotation);
//			mViewTransform.postTranslate(-mPosition.mX, -mPosition.mY);
//		}else
//			mViewTransform.setTranslation(-mPosition.mX, -mPosition.mY);
//		mViewTransform.postScale(1/mZoom);

		mCameraTransform.setTranslation(mPosition.mX,mPosition.mY);

		if(mRotation!=0) {
			mCameraTransform.rotateZ(-mRotation);
		}
		if(mPreShiftEnabled) {
			mCameraTransform.translate(mPreShiftX,mPreShiftY);
		}
		mCameraTransform.scale(mZoom,mZoom,1);
	}

	protected void refreshProjectionTransform() {
		mProjectionUpdated = true;
		OrthogonalProjection.getTransform(mProjectionTransform,
				-1, 1,
				1, -1,
				mNear,mFar
				);
	}

	public void set(float x, float y, float zoom, float rotation) {
		mPosition.mX = x;
		mPosition.mY = y;
		mPosition.mZ = zoom;
		mZoom = zoom;
		mRotation = rotation;
		refreshViewTransform();
	}

	public void set(Camera2D prefaceCam) {
		mPosition.set(prefaceCam.mPosition);
		mZoom = prefaceCam.mZoom;
		mRotation = prefaceCam.mRotation;
		mPreShiftEnabled = prefaceCam.mPreShiftEnabled;
		mPreShiftX = prefaceCam.mPreShiftX;
		mPreShiftY = prefaceCam.mPreShiftY;
		refreshViewTransform();
	}

	public void setZBounds(float minZ,float maxZ) {
		mNear = -maxZ;
		mFar = -minZ;
		refreshProjectionTransform();
	}

}
