package yang.graphics.camera.projection;

import yang.math.objects.YangMatrix;

public class Projection {

	public YangMatrix mTransform;
	protected YangMatrix mInvTransform;
	public float mRatioX = 1,mRatioY = 1;
	protected float mNear = -1;
	protected float mFar = 1;

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
		mNear = -1;
		mFar = 1;
		if(mTransform!=null)
			mTransform.loadIdentity();
	}

	public void refreshInverted() {
		mTransform.asInverted(mInvTransform.mValues);
	}

}
