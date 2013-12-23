package yang.graphics.skeletons.pose;

import yang.math.MathConst;
import yang.physics.massaggregation.MassAggregation;
import yang.util.Util;

@SuppressWarnings("rawtypes")
public abstract class Posture<InterpolationPoseType extends Posture,SkeletonType extends MassAggregation> {

	public static float PI = MathConst.PI;
	public static float PI_HALF = MathConst.PI_HALF;

	public float[] mData;

	protected int mPostureId;

	public Posture() {
		mPostureId = 0;
	}

	public Posture(float[] data) {
		this();
		mData = data;
	}

	public abstract void applyPosture(SkeletonType skeleton,InterpolationPoseType interpolationPose, float weight);
	public abstract void applyForceBased(SkeletonType skeleton,InterpolationPoseType interpolationPose, float weight);
	public abstract void copyFromSkeleton(SkeletonType skeleton);
	public abstract String toSourceCode();

	public void applyPosture(SkeletonType skeleton) {
		applyPosture(skeleton,null,1);
	}

	public void applyForceBased(SkeletonType skeleton) {
		applyForceBased(skeleton,null,1);
	}

	public String getClassName() {
		return Util.getClassName(this);
	}

	@Override
	public String toString() {
		return "{"+Util.arrayToString(mData,",",0)+"}";
	}

	public void copyFromPosture(Posture posture) {
		System.arraycopy(posture.mData, 0, mData, 0, mData.length);
	}

	public void clear() {

	}

	public void clear(int mId) {

	}

}
