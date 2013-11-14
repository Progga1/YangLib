package yang.graphics.skeletons.pose;

import yang.physics.massaggregation.MassAggregation;
import yang.physics.massaggregation.elements.Joint;
import yang.util.Util;

public class PositionPosture3D extends Posture<PositionPosture3D,MassAggregation> {

	public PositionPosture3D(float[] data) {
		super(data);
	}

	public PositionPosture3D(MassAggregation skeleton) {
		super(skeleton);
	}

	@Override
	public void applyPose(MassAggregation skeleton, PositionPosture3D interpolationPose, float weight) {
		int c = 0;
		final float dWeight = 1-weight;
		for(final Joint joint:skeleton.mJoints) {
			if(joint.mAnimate) {
				joint.mPosX = (mData[c]*dWeight + interpolationPose.mData[c++]*weight);
				joint.mPosY = (mData[c]*dWeight + interpolationPose.mData[c++]*weight);
				joint.mPosZ = (mData[c]*dWeight + interpolationPose.mData[c++]*weight);
			}
		}
	}

	@Override
	public void copyFromSkeleton(MassAggregation skeleton) {
		int c=0;
		for(final Joint joint:skeleton.mJoints) {
			if(joint.mAnimate) {
				mData[c++] = joint.mPosX;
				mData[c++] = joint.mPosY;
				mData[c++] = joint.mPosZ;
			}
		}
	}

	@Override
	public String toSourceCode() {
		return "new PositionPose3D(new float[]{"+Util.arrayToString(mData,",",0)+"})";
	}




}
