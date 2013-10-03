package yang.graphics.skeletons.pose;

import yang.graphics.skeletons.CartoonSkeleton2D;
import yang.math.MathConst;
import yang.physics.massaggregation.MassAggregation;
import yang.util.Util;

@SuppressWarnings("rawtypes")
public abstract class Posture<InterpolationPoseType extends Posture,SkeletonType extends MassAggregation> {

	public static float PI = MathConst.PI;
	public static float PI_HALF = MathConst.PI_HALF;
	
	protected int mPoseId;
	
	public Posture() {
		mPoseId = 0;
	}
	
	public abstract void applyPose(SkeletonType skeleton,InterpolationPoseType interpolationPose, float weight);
	public abstract void copyFromSkeleton(CartoonSkeleton2D skeleton);
	public abstract String toSourceCode();
	
	public void applyPose(SkeletonType skeleton) {
		applyPose(skeleton,null,1);
	}

	public String getClassName() {
		return Util.getClassName(this);
	}
	
}
