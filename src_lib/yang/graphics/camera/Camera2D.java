package yang.graphics.camera;

import yang.graphics.camera.projection.OrthogonalProjection;
import yang.math.MatrixOps;

public class Camera2D extends YangCamera {

	public float mZoom;
	public float mRotation;
	public float mRatioX = 1,mRatioY = 1;

	public static void set(YangCamera target,float x,float y,float zoom, float rotation) {
		if(rotation!=0) {
			MatrixOps.setRotationZ(target.mViewTransform.mValues,rotation);
			target.mViewTransform.postTranslate(-x, -y);
		}else
			target.mViewTransform.setTranslation(-x, -y);
		target.mViewTransform.postScale(1/zoom);
		target.mPosition.set(x,y,zoom);
		zoom = 1;
		OrthogonalProjection.getTransform(target.mProjectionTransform,
				-zoom, zoom,
				zoom, -zoom,
				target.mNear,target.mFar
				);
	}

	public Camera2D() {
		super();
		mPosition.mZ = 1;
		mZoom = 1;
		refreshProjectionTransform();
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
		refreshProjectionTransform();
	}

	public void refreshViewTransform() {
		if(mRotation!=0) {
			MatrixOps.setRotationZ(mViewTransform.mValues,mRotation);
			mViewTransform.postTranslate(-mPosition.mX, -mPosition.mY);
		}else
			mViewTransform.setTranslation(-mPosition.mX, -mPosition.mY);

	}

	protected void refreshProjectionTransform() {
		OrthogonalProjection.getTransform(mProjectionTransform,
				-mRatioX * mZoom, mRatioX * mZoom,
				mRatioY * mZoom, -mRatioY * mZoom,
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

	public void setZBounds(float minZ,float maxZ) {
		mNear = -maxZ;
		mFar = -minZ;
		refreshProjectionTransform();
	}

}
