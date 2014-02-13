package yang.graphics.skeletons.pose;

import yang.physics.massaggregation.MassAggregation;
import yang.physics.massaggregation.elements.Joint;
import yang.util.Util;

public class PositionPosture3D extends Posture<PositionPosture3D,MassAggregation> {

	public PositionPosture3D(float[] data) {
		super(data);
	}

	public PositionPosture3D(MassAggregation skeleton) {
		super(new float[skeleton.calcAnimatedJointCount()*3]);
		for(int i=0;i<mData.length;i++) {
			mData[i] = Float.MAX_VALUE;
		}
	}

	@Override
	public void applyPosture(MassAggregation skeleton, PositionPosture3D interpolationPose, float weight) {
		int c = 0;
		final float dWeight = 1-weight;
		for(final Joint joint:skeleton.mJoints) {
			if(joint.mAnimate) {
				if(mData[c]!=Float.MAX_VALUE && !joint.mDragging) {
					if(weight==0 || interpolationPose==null) {
						joint.mX = mData[c++];
						joint.mY = mData[c++];
						joint.mZ = mData[c++];
					}else{
						joint.mX = mData[c]*weight + interpolationPose.mData[c]*dWeight;
						joint.mY = mData[c+1]*weight + interpolationPose.mData[c+1]*dWeight;
						joint.mZ = mData[c+2]*weight + interpolationPose.mData[c+2]*dWeight;
						c += 3;
					}
				}else
					c += 3;
			}
		}
	}

	@Override
	public void applyForceBased(MassAggregation skeleton, PositionPosture3D interpolationPose, float weight) {
//		applyPosture(skeleton,interpolationPose,weight);if(true)return;
		int c = 0;
		final float dWeight = 1-weight;
		for(final Joint joint:skeleton.mJoints) {
			if(joint.mAnimate) {
				if(mData[c]!=Float.MAX_VALUE && !joint.mDragging) {
					if(weight==0 || interpolationPose==null) {
						joint.addPositionForce(mData[c],mData[c+1],mData[c+2],1);
					}else{
						if(interpolationPose.mData[c]!=Float.MAX_VALUE)
							joint.addPositionForce(
									mData[c]*weight + interpolationPose.mData[c]*dWeight,
									mData[c+1]*weight + interpolationPose.mData[c+1]*dWeight,
									mData[c+2]*weight + interpolationPose.mData[c+2]*dWeight,
									4);
					}
				}
				c += 3;
			}
		}
	}

	@Override
	public void copyFromSkeleton(MassAggregation skeleton) {
		int c=0;
		for(final Joint joint:skeleton.mJoints) {
			if(joint.mAnimate) {
				mData[c++] = joint.mX;
				mData[c++] = joint.mY;
				mData[c++] = joint.mZ;
			}
		}
	}

	@Override
	public String toSourceCode() {
		return "new PositionPose3D(new float[]{"+Util.arrayToString(mData,",",0)+"})";
	}

	@Override
	public void clear() {
		for(int c=0;c<mData.length;c++)
			mData[c] = Float.MAX_VALUE;
	}

	@Override
	public void clear(int id) {
		mData[id*3] = Float.MAX_VALUE;
	}

}
