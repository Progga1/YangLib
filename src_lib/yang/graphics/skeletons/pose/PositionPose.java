package yang.graphics.skeletons.pose;

import yang.graphics.skeletons.Skeleton2D;
import yang.graphics.skeletons.elements.Joint;
import yang.physics.massaggregation.MassAggregation;

public class PositionPose extends Pose<PositionPose>{

	protected float[] mPositionsX;
	protected float[] mPositionsY;

	@Override
	public void applyPose(MassAggregation skeleton, PositionPose pose, float weight) {
		skeleton.mCurrentPose = this;
		int c = 0;
		for(Joint joint:skeleton.mJoints) {
			if(c>=mPositionsX.length)
				break;
			joint.mPosX = mPositionsX[c];
			joint.mPosY = mPositionsY[c];
			c++;
		}
	}

	@Override
	public void copyFromSkeleton(Skeleton2D skeleton) {
		int c = 0;
		for(Joint joint:skeleton.mJoints) {
			if(c>=mPositionsX.length)
				break;
			mPositionsX[c] = joint.mPosX;
			mPositionsY[c] = joint.mPosY;
			c++;
		}
	}

	@Override
	public String toSourceCode() {
		return null;
	}
	
}
