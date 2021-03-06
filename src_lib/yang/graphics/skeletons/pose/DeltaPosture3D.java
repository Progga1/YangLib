package yang.graphics.skeletons.pose;

import yang.physics.massaggregation.MassAggregation;
import yang.physics.massaggregation.editing.JointEditData;
import yang.physics.massaggregation.editing.Skeleton3DEditing;
import yang.physics.massaggregation.elements.Joint;
import yang.util.Util;

public class DeltaPosture3D extends Posture<DeltaPosture3D,MassAggregation> {

	public Skeleton3DEditing mSkeletonData;

	public DeltaPosture3D(float[] data) {
		super(data);
	}

	public DeltaPosture3D(Skeleton3DEditing skeleton) {
		super(new float[skeleton.mSkeleton.calcAnimatedJointCount()*3]);
		mSkeletonData = skeleton;
		for(int i=0;i<mData.length;i++) {
			mData[i] = Float.MAX_VALUE;
		}
	}

	@Override
	public void applyPosture(MassAggregation skeleton, DeltaPosture3D interpolationPose, float weight) {

	}

	public void applyForceBased(MassAggregation skeleton, DeltaPosture3D interpolationPose, float weight) {
		int c = 0;
		int i = 0;
		//final float dWeight = 1-weight;
		for(final Joint joint:skeleton.mJoints) {
			if(joint.mAnimate) {
				final JointEditData jointData = mSkeletonData.mJointData[i++];
				if(!joint.mAnimDisabled && mData[c]!=Float.MAX_VALUE) {
					if(interpolationPose==null) {

					}else{
						final float fac = weight;
						if(interpolationPose.mData[c]!=Float.MAX_VALUE) {
							joint.addPositionForce(
									jointData.mPrevPos.mX + mData[c]*fac,
									jointData.mPrevPos.mY + mData[c+1]*fac,
									jointData.mPrevPos.mZ + mData[c+2]*fac,
									20);
						}
					}
				}
				c += 3;
			}
		}
	}

	@Override
	public void copyFromSkeleton(MassAggregation skeleton) {
		clear();
	}

	@Override
	public String toSourceCode() {
		return "new PositionPose3D(new float[]{"+Util.arrayToString(mData,",",0)+"})";
	}

	@Override
	public void clear() {
		for(int i=0;i<mData.length;i++) {
			mData[i] = Float.MAX_VALUE;
		}
	}

	@Override
	public void clear(int id) {
		mData[id*3] = Float.MAX_VALUE;
	}


}
