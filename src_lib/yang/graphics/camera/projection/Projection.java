package yang.graphics.camera.projection;

import yang.math.objects.matrix.YangMatrix;
import yang.math.objects.matrix.YangMatrixCameraOps;

public class Projection {

	public YangMatrix mTransform;
	protected YangMatrix mInvTransform;
	public float mRatioX = 1,mRatioY = 1;
	protected float mNear = YangMatrixCameraOps.DEFAULT_NEAR;
	protected float mFar = YangMatrixCameraOps.DEFAULT_FAR;

	public void refresh() {

	}

	public Projection() {
		reset();
	}

//	public void setRatio(ScreenInfo ratio) {
//		if(mRatioX!=ratio.getSurfaceRatioX() || mRatioY!=ratio.getSurfaceRatioY()) {
//			mRatioX = ratio.getSurfaceRatioX();
//			mRatioY = ratio.getSurfaceRatioY();
//			refresh();
//		}
//	}

	public void setRatio(float ratioX,float ratioY) {
		mRatioX = ratioX;
		mRatioY = ratioY;
		refresh();
	}

	public void setRatio(float ratioX) {
		setRatio(ratioX,1);
	}

	public void reset() {
		mNear = YangMatrixCameraOps.DEFAULT_NEAR;
		mFar = YangMatrixCameraOps.DEFAULT_FAR;
		if(mTransform!=null)
			mTransform.loadIdentity();
	}

	public void refreshInverted() {
		mTransform.asInverted(mInvTransform.mValues);
	}

}
