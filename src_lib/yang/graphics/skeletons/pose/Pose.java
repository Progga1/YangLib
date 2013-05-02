package yang.graphics.skeletons.pose;

import yang.graphics.skeletons.Skeleton;
import yang.util.Util;

@SuppressWarnings("rawtypes")
public abstract class Pose<InterpolationPoseType extends Pose> {

	protected int mPoseId;
	
	public Pose() {
		mPoseId = 0;
	}
	
	public abstract void applyPose(Skeleton skeleton,InterpolationPoseType interpolationPose, float weight);
	public abstract void copyFromSkeleton(Skeleton skeleton);
	public abstract String toSourceCode();
	
	public void applyPose(Skeleton skeleton) {
		applyPose(skeleton,null,1);
	}

	public String getClassName() {
		return Util.getClassName(this);
	}
	
}
