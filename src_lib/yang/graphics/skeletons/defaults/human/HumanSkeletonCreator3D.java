package yang.graphics.skeletons.defaults.human;

import yang.graphics.skeletons.elements.Bone;
import yang.graphics.skeletons.elements.Joint;
import yang.physics.massaggregation.MassAggregation;

public class HumanSkeletonCreator3D {

	public Joint mBreastJoint;
	public Joint mHeadJoint;
	public Joint mHipJoint;
	public Joint mLeftKneeJoint;
	public Joint mRightKneeJoint;
	public Joint mLeftFootJoint;
	public Joint mRightFootJoint;
	public Joint mLeftElbowJoint;
	public Joint mRightElbowJoint;
	public Joint mLeftHandJoint;
	public Joint mRightHandJoint;
	public Joint mLeftToesJoint;
	public Joint mRightToesJoint;
	
	public Joint mLeftShoulderJoint;
	public Joint mRightShoulderJoint;
	public Joint mLeftLegJoint;
	public Joint mRightLegJoint;
	
	public Bone mBodyBone;
	public Bone mHeadBone;
	public Bone mLeftUpperArmBone;
	public Bone mRightUpperArmBone;
	public Bone mLeftLowerArmBone;
	public Bone mRightLowerArmBone;
	public Bone mLeftUpperLegBone;
	public Bone mRightUpperLegBone;
	public Bone mLeftLowerLegBone;
	public Bone mRightLowerLegBone;
	
	public MassAggregation mSkeleton;
	
	public HumanSkeletonCreator3D() {
		
	}
	
	public MassAggregation create(HumanSkeletonProperties properties) {
		
		if(mSkeleton==null) {
			mSkeleton = new MassAggregation();
			//mSkeleton.init(graphics);
		}
			
		float locScaleX = 1;
		float locScaleY = 1;
		float neckY = (1-properties.mHeadRatio)*properties.mHeight;
		float hipsY = properties.mHeight*properties.mHipRatio;
		float kneeY = hipsY*0.5f;
		float legsX = properties.mHipWidth*0.5f;
		float shoulderX = properties.mShoulderWidth*0.5f;
		float shoulderY = neckY-properties.mShoulderOffsetY*properties.mHeight;
		float armX = properties.mShoulderWidth*0.5f;
		float elbowY = shoulderY-(properties.mArmLength*0.5f)*properties.mHeight;
		float handY = shoulderY-properties.mArmLength*properties.mHeight;
	
		mSkeleton.mDefaultJointRadius = 0.1f*locScaleX;
		
		mBreastJoint = mSkeleton.addJoint("Breast",null, 0,neckY,0);
		mHeadJoint = mSkeleton.addJoint("Head",mBreastJoint, 0,2*locScaleY,0).setRadius(0.15f*locScaleX);
		mHipJoint = mSkeleton.addJoint("Hip",mBreastJoint, 0,hipsY,0);
		mLeftKneeJoint = mSkeleton.addJoint("LeftKnee",null, legsX, kneeY,0);
		mRightKneeJoint = mSkeleton.addJoint("RightKnee",null, -legsX, kneeY,0);
		mLeftFootJoint = mSkeleton.addJoint("LeftFoot",mLeftKneeJoint, legsX,0,0);
		mRightFootJoint = mSkeleton.addJoint("RightFoot",mRightKneeJoint, -legsX,0,0);
		mLeftElbowJoint = mSkeleton.addJoint("LeftElbow",null, armX,elbowY,0);
		mRightElbowJoint = mSkeleton.addJoint("RightElbow",null, -armX,elbowY,0);
		mLeftHandJoint = mSkeleton.addJoint("LeftHand",mLeftElbowJoint, armX, handY, 0);
		mRightHandJoint = mSkeleton.addJoint("RightHand",mRightElbowJoint, -armX, handY, 0);
		
		return mSkeleton;
	}
	
}
