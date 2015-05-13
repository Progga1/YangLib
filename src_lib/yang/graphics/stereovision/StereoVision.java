package yang.graphics.stereovision;

import yang.graphics.translator.AbstractGraphics;
import yang.math.objects.YangMatrix;

public class StereoVision {

	public static final int EYE_MONO = 0;
	public static final int EYE_LEFT = -1;
	public static final int EYE_RIGHT = 1;

	public static float DEFAULT_INTEROCULAR_DISTANCE = 0.064f;

	protected float mInterOcularDistance = DEFAULT_INTEROCULAR_DISTANCE;
	protected float mCameraShift = DEFAULT_INTEROCULAR_DISTANCE;

	protected YangMatrix mLeftResultTransform = new YangMatrix();
	protected YangMatrix mRightResultTransform = new YangMatrix();
	protected YangMatrix mLeftEyeTransform = new YangMatrix();
	protected YangMatrix mRightEyeTransform = new YangMatrix();
	public YangMatrix mPostTransform = null;

	public StereoVision() {
		setInterOcularDistance(DEFAULT_INTEROCULAR_DISTANCE);
	}

	public float getInterOcularDistance() {
		return mInterOcularDistance;
	}

	public void refreshTransforms() {
		mCameraShift = mInterOcularDistance*AbstractGraphics.METERS_PER_UNIT*0.5f;
		mLeftEyeTransform.setTranslation(-mCameraShift, 0);
		mRightEyeTransform.setTranslation(mCameraShift, 0);
		mLeftResultTransform.set(mLeftEyeTransform);
		if(mPostTransform!=null)
			mLeftResultTransform.multiplyLeft(mPostTransform);
		mRightResultTransform.set(mRightEyeTransform);
		if(mPostTransform!=null)
			mRightResultTransform.multiplyLeft(mPostTransform);
	}

	public void setInterOcularDistance(float distance) {
		mInterOcularDistance = distance;
		refreshTransforms();
	}

	public YangMatrix getLeftEyeTransform() {
		return mLeftResultTransform;
	}

	public YangMatrix getRightEyeTransform() {
		return mRightResultTransform;
	}

}
