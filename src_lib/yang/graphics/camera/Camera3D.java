package yang.graphics.camera;

import yang.math.objects.matrix.YangMatrix;
import yang.math.objects.matrix.YangMatrixCameraOps;

class Camera3D extends YangCamera {

	public final static int MODE_ORTHOGONAL = 0;
	public final static int MODE_PERSPECTIVE = 1;

	protected int mCurMode = MODE_ORTHOGONAL;
	public YangMatrixCameraOps mViewMatrix = new YangMatrixCameraOps();
	public YangMatrixCameraOps mProjectionMatrix = new YangMatrixCameraOps();
	public YangMatrix mInvProjection = new YangMatrix();

	private int mOrthoSize = 1;

	public Camera3D() {
		reset();
	}

	@Override
	public void refreshResultTransform() {
		mResultTransform.set(mProjectionMatrix);
		mResultTransform.multiplyRight(mViewMatrix);
		mResultTransform.asInverted(mInvResultTransform.mValues);
	}

	@Override
	public void reset() {
		mViewMatrix.loadIdentity();
		setOrthogonalProjection(-1,1, 1);

		refreshResultTransform();
	}

	public void setOrthogonalProjection(float near,float far,float width,float height) {
		mCurMode = MODE_ORTHOGONAL;
		mNear = near;
		mFar = far;
		mOrthoSize = 1;
		mProjectionMatrix.setOrthogonalProjection(-width*0.5f,width*0.5f,height*0.5f,-height*0.5f,near,far);
		mProjectionMatrix.asInverted(mInvProjection.mValues);
	}

	public void setOrthogonalProjection(float near,float far,float size) {
		setOrthogonalProjection(near,far,mScreen.getSurfaceRatioX()*size,mScreen.getSurfaceRatioY()*size);
	}

}
