package yang.graphics.skeletons.defaults.human;

import yang.physics.massaggregation.MassAggregation;
import yang.physics.massaggregation.elements.Bone3D;
import yang.physics.massaggregation.elements.Joint;

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

	public Bone3D mBodyBone;
	public Bone3D mHeadBone;
	public Bone3D mLeftUpperArmBone;
	public Bone3D mRightUpperArmBone;
	public Bone3D mLeftLowerArmBone;
	public Bone3D mRightLowerArmBone;
	public Bone3D mLeftUpperLegBone;
	public Bone3D mRightUpperLegBone;
	public Bone3D mLeftLowerLegBone;
	public Bone3D mRightLowerLegBone;

	public Bone3D mLeftShoulderBone;
	public Bone3D mRightShoulderBone;
	public Bone3D mLeftLegBone;
	public Bone3D mRightLegBone;

	public Bone3D mHipsBone;
	public Bone3D mBreastBone;

	public MassAggregation mSkeleton;

	public HumanSkeletonCreator3D() {

	}

	public MassAggregation create(HumanSkeletonProperties properties) {

		if(mSkeleton==null) {
			mSkeleton = new MassAggregation();
		}

		final float locScaleX = 1;
		final float locScaleY = 1;
		final float smallRad = 0.05f*locScaleX;
		final float neckY = (1-properties.mHeadRatio)*properties.mHeight;
		final float hipsY = properties.mHeight*properties.mHipRatio;
		final float kneeY = hipsY*0.5f;
		final float legsX = properties.mHipWidth*0.5f;
		final float shoulderX = properties.mShoulderWidth*0.5f;
		final float shoulderY = neckY-properties.mShoulderOffsetY*properties.mHeight;
		final float armX = properties.mShoulderWidth*0.5f;
		final float elbowY = shoulderY-(properties.mArmLength*0.5f)*properties.mHeight;
		final float handY = shoulderY-properties.mArmLength*properties.mHeight;

		mSkeleton.mDefaultJointRadius = 0.1f*locScaleX;

		mBreastJoint = mSkeleton.addJoint("Breast",null, 0,neckY,0);
		mHeadJoint = mSkeleton.addJoint("Head",mBreastJoint, 0,2*locScaleY,0).setRadius(0.15f*locScaleX);
		mHipJoint = mSkeleton.addJoint("Hip",mBreastJoint, 0,hipsY,0);
		mLeftLegJoint = mSkeleton.addJoint("LeftLeg",mHipJoint, legsX, hipsY,0).setRadius(smallRad);
		mRightLegJoint = mSkeleton.addJoint("RightLeg",mHipJoint, -legsX, hipsY,0).setRadius(smallRad);
		mLeftKneeJoint = mSkeleton.addJoint("LeftKnee",mLeftLegJoint, legsX, kneeY,0);
		mRightKneeJoint = mSkeleton.addJoint("RightKnee",mRightLegJoint, -legsX, kneeY,0);
		mLeftFootJoint = mSkeleton.addJoint("LeftFoot",mLeftKneeJoint, legsX,0,0);
		mRightFootJoint = mSkeleton.addJoint("RightFoot",mRightKneeJoint, -legsX,0,0);
		mLeftShoulderJoint = mSkeleton.addJoint("LeftShoulder",mBreastJoint, shoulderX,shoulderY,0).setRadius(smallRad);
		mRightShoulderJoint = mSkeleton.addJoint("RightShoulder",mBreastJoint, -shoulderX,shoulderY,0).setRadius(smallRad);
		mLeftElbowJoint = mSkeleton.addJoint("LeftElbow",mLeftShoulderJoint, armX,elbowY,0);
		mRightElbowJoint = mSkeleton.addJoint("RightElbow",mRightShoulderJoint, -armX,elbowY,0);
		mLeftHandJoint = mSkeleton.addJoint("LeftHand",mLeftElbowJoint, armX, handY, 0);
		mRightHandJoint = mSkeleton.addJoint("RightHand",mRightElbowJoint, -armX, handY, 0);

		mSkeleton.mDefaultBoneSpring = 10;
		mBodyBone = mSkeleton.addSpringBone(new Bone3D("Body",mBreastJoint,mHipJoint));
		mBodyBone = mSkeleton.addSpringBone(new Bone3D("Head",mBreastJoint,mHeadJoint));
		mLeftUpperArmBone = mSkeleton.addSpringBone(new Bone3D("LeftShoulder",mBreastJoint,mLeftShoulderJoint));
		mRightUpperArmBone = mSkeleton.addSpringBone(new Bone3D("RightShoulder",mBreastJoint,mRightShoulderJoint));
		mLeftUpperArmBone = mSkeleton.addSpringBone(new Bone3D("LeftUpperArm",mLeftShoulderJoint,mLeftElbowJoint));
		mRightUpperArmBone = mSkeleton.addSpringBone(new Bone3D("RightUpperArm",mRightShoulderJoint,mRightElbowJoint));
		mLeftLowerArmBone = mSkeleton.addSpringBone(new Bone3D("LeftLowerArm",mLeftElbowJoint,mLeftHandJoint));
		mRightLowerArmBone = mSkeleton.addSpringBone(new Bone3D("RightLowerArm",mRightElbowJoint,mRightHandJoint));
		mLeftLegBone = mSkeleton.addSpringBone(new Bone3D("LeftLeg",mHipJoint,mLeftLegJoint));
		mRightLegBone = mSkeleton.addSpringBone(new Bone3D("RightLeg",mHipJoint,mRightLegJoint));
		mLeftUpperLegBone = mSkeleton.addSpringBone(new Bone3D("LeftUpperLeg",mLeftLegJoint,mLeftKneeJoint));
		mRightUpperLegBone = mSkeleton.addSpringBone(new Bone3D("RightUpperLeg",mRightLegJoint,mRightKneeJoint));
		mLeftLowerLegBone = mSkeleton.addSpringBone(new Bone3D("LeftLowerLeg",mLeftKneeJoint,mLeftFootJoint));
		mRightLowerLegBone = mSkeleton.addSpringBone(new Bone3D("RightLowerLeg",mRightKneeJoint,mRightFootJoint));

		mBreastBone = mSkeleton.addSpringBone(new Bone3D("Breast",mLeftShoulderJoint,mRightShoulderJoint));
		mHipsBone = mSkeleton.addSpringBone(new Bone3D("Hips",mLeftLegJoint,mRightLegJoint));

		return mSkeleton;
	}

}
