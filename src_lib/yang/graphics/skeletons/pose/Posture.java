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

	public abstract void applyPose(SkeletonType skeleton,InterpolationPoseType interpolationPose, float weight);
	public abstract void copyFromSkeleton(SkeletonType skeleton);
	public abstract String toSourceCode();

	public void applyPose(SkeletonType skeleton) {
		applyPose(skeleton,null,1);
	}

	public String getClassName() {
		return Util.getClassName(this);
	}

	@Override
	public String toString() {
		return "{"+Util.arrayToString(mData,",",0)+"}";
	}

}
