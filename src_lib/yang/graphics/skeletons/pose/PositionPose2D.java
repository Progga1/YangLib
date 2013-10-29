package yang.graphics.skeletons.pose;

import yang.graphics.skeletons.CartoonSkeleton2D;
import yang.physics.massaggregation.elements.Joint;

public class PositionPose2D extends Posture<PositionPose2D,CartoonSkeleton2D>{

	protected float[] mPositionsX;
	protected float[] mPositionsY;

	@Override
	public void applyPose(CartoonSkeleton2D skeleton, PositionPose2D pose, float weight) {
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
	public void copyFromSkeleton(CartoonSkeleton2D skeleton) {
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
