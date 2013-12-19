package yang.graphics.skeletons.pose;

import yang.physics.massaggregation.MassAggregation;
import yang.physics.massaggregation.editing.JointEditData;
import yang.physics.massaggregation.editing.Skeleton3DEditing;
import yang.physics.massaggregation.elements.Joint;
import yang.util.Util;

public class CopyOfDeltaPosture3D extends Posture<CopyOfDeltaPosture3D,MassAggregation> {

	public Skeleton3DEditing mSkeletonData;

	public CopyOfDeltaPosture3D(float[] data) {
		super(data);
	}

	public CopyOfDeltaPosture3D(Skeleton3DEditing skeleton) {
		super(new float[skeleton.mSkeleton.calcAnimatedJointCount()*3]);
		mSkeletonData = skeleton;
		for(int i=0;i<mData.length;i++) {
			mData[i] = Float.MAX_VALUE;
		}
	}

	@Override
	public void applyPosture(MassAggregation skeleton, CopyOfDeltaPosture3D interpolationPose, float weight) {

	}

	@Override
	public void applyForceBased(MassAggregation skeleton, CopyOfDeltaPosture3D interpolationPose, float weight) {
		int c = 0;
		int i = 0;
		final float dWeight = 1-weight;
		for(final Joint joint:skeleton.mJoints) {
			if(joint.mAnimate) {
				final JointEditData jointData = mSkeletonData.mJointData[i++];
				if(!joint.mDragging && mData[c]!=Float.MAX_VALUE) {
					if(interpolationPose==null) {

					}else{
						final float fac = weight;
						//fac = 1;
						if(interpolationPose.mData[c]!=Float.MAX_VALUE) {
//							joint.setPos(
//									jointData.mPrevPosX + mData[c]*fac,
//									jointData.mPrevPosY + mData[c+1]*fac,
//									jointData.mPrevPosZ + mData[c+2]*fac
//									//1);
//									);

							joint.addPositionForce(
									jointData.mPrevPos.mX + mData[c]*fac,
									jointData.mPrevPos.mY + mData[c+1]*fac,
									jointData.mPrevPos.mZ + mData[c+2]*fac,
									1);

//							float x,y,z;
//							if(joint.mAngleParent==null) {
//								if(weight==0 || interpolationPose==null) {
//									x = mData[c];
//									y = mData[c+1];
//									z = mData[c+2];
//								}else{
//									x = mData[c]*weight + interpolationPose.mData[c]*dWeight;
//									y = mData[c+1]*weight + interpolationPose.mData[c+1]*dWeight;
//									z = mData[c+2]*weight + interpolationPose.mData[c+2]*dWeight;
//								}
//							}else{
//								if(weight==0 || interpolationPose==null) {
//									x = joint.mAngleParent.mPosX + mData[c];
//									y = joint.mAngleParent.mPosY + mData[c+1];
//									z = joint.mAngleParent.mPosZ + mData[c+2];
//								}else{
//									x = joint.mAngleParent.mPosX + mData[c]*weight + interpolationPose.mData[c]*dWeight;
//									y = joint.mAngleParent.mPosY + mData[c+1]*weight + interpolationPose.mData[c+1]*dWeight;
//									z = joint.mAngleParent.mPosZ + mData[c+2]*weight + interpolationPose.mData[c+2]*dWeight;
//								}
//							}
//
//							//joint.addPositionForce(x,y,z,1);
//							joint.setPos(x,y,z);
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
