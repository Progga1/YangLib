package yang.graphics.skeletons.pose;

import yang.graphics.skeletons.Skeleton2D;
import yang.physics.massaggregation.MassAggregation;
import yang.util.Util;

@SuppressWarnings("rawtypes")
public abstract class Pose<InterpolationPoseType extends Pose> {

	protected int mPoseId;
	
	public Pose() {
		mPoseId = 0;
	}
	
	public abstract void applyPose(MassAggregation skeleton,InterpolationPoseType interpolationPose, float weight);
	public abstract void copyFromSkeleton(Skeleton2D skeleton);
	public abstract String toSourceCode();
	
	public void applyPose(MassAggregation skeleton) {
		applyPose(skeleton,null,1);
	}

	public String getClassName() {
		return Util.getClassName(this);
	}
	
}
