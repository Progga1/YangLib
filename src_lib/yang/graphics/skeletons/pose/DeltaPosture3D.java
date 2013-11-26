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
	public void applyPose(MassAggregation skeleton, DeltaPosture3D interpolationPose, float weight) {

	}

	@Override
	public void applyForceBased(MassAggregation skeleton, DeltaPosture3D interpolationPose, float weight) {
		int c = 0;
		int i = 0;
		final float dWeight = 1-weight;
		for(final Joint joint:skeleton.mJoints) {
			if(joint.mAnimate) {
				final JointEditData jointData = mSkeletonData.mJointData[i++];
				if(!joint.mDragging && !joint.mNoAnimationForce && mData[c]!=Float.MAX_VALUE) {
					if(interpolationPose==null) {

					}else{
						final float fac = weight;
						if(interpolationPose.mData[c]!=Float.MAX_VALUE) {
							joint.addPositionForce(
									jointData.mPrevPosX + mData[c]*fac,
									jointData.mPrevPosY + mData[c+1]*fac,
									jointData.mPrevPosZ + mData[c+2]*fac,
									8);
						}
					}
				}
				c += 3;
			}
		}
	}

	@Override
	public void copyFromSkeleton(MassAggregation skeleton) {
		for(int i=0;i<mData.length;i++) {
			mData[i] = Float.MAX_VALUE;
		}
	}

	@Override
	public String toSourceCode() {
		return "new PositionPose3D(new float[]{"+Util.arrayToString(mData,",",0)+"})";
	}




}
