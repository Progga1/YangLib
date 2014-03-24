package yang.graphics.stereovision;

import yang.graphics.translator.AbstractGraphics;
import yang.math.objects.YangMatrix;

public class StereoVision {

	public static float DEFAULT_INTEROCULAR_DISTANCE = 0.064f;

	protected float mInterOcularDistance = DEFAULT_INTEROCULAR_DISTANCE;
	protected float mCameraShift = DEFAULT_INTEROCULAR_DISTANCE;

	protected YangMatrix mLeftTransform = new YangMatrix();
	protected YangMatrix mRightTransform = new YangMatrix();

	public StereoVision() {
		setInterOcularDistance(DEFAULT_INTEROCULAR_DISTANCE);
	}

	public float getInterOcularDistance() {
		return mInterOcularDistance;
	}

	protected void refreshTransforms() {
		mCameraShift = mInterOcularDistance*AbstractGraphics.METERS_PER_UNIT*0.5f;
		mLeftTransform.setTranslation(-mCameraShift, 0);
		mRightTransform.setTranslation(mCameraShift, 0);
	}

	public void setInterOcularDistance(float distance) {
		mInterOcularDistance = distance;
		refreshTransforms();
	}

	public YangMatrix getLeftEyeTransform() {
		return mLeftTransform;
	}

	public YangMatrix getRightEyeTransform() {
		return mRightTransform;
	}

}
