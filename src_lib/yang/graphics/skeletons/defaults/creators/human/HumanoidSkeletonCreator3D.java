package yang.graphics.skeletons.defaults.creators.human;

import yang.physics.massaggregation.MassAggregation;
import yang.physics.massaggregation.elements.Bone3D;
import yang.physics.massaggregation.elements.Joint;

public class HumanoidSkeletonCreator3D {

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

	public HumanoidSkeletonCreator3D() {

	}

	public MassAggregation create(HumanoidSkeletonProperties properties) {

		if(mSkeleton==null) {
			mSkeleton = new MassAggregation();
		}

		final float locScaleX = 1;
		final float locScaleY = 1;
		final float smallRad = 0.05f*locScaleX;
		final float medRad = 0.09f*locScaleX;
		final float neckY = properties.mBreastY*properties.mHeight;
		final float hipsY = properties.mHeight*properties.mHipsY;
		final float kneeY = hipsY*0.5f;
		final float legsX = properties.mHipWidth*0.5f;
		final float shoulderX = properties.mShoulderWidth*0.5f;
		final float shoulderY = neckY-properties.mShoulderOffsetY*properties.mHeight;
		final float armX = properties.mShoulderWidth*0.5f;
		final float elbowY = shoulderY-(properties.mArmLength*0.5f)*properties.mHeight;
		final float handY = shoulderY-properties.mArmLength*properties.mHeight;

		mSkeleton.mDefaultJointRadius = 0.1f*locScaleX;

		float breastZ = properties.mBreastZ;
		float hipsZ = properties.mHipsZ;
		mBreastJoint = mSkeleton.addJoint("Breast",null, 0,neckY,breastZ);
		mHeadJoint = mSkeleton.addJoint("Head",mBreastJoint, 0,properties.mHeight*locScaleY,properties.mHeadZ).setRadius(0.15f*locScaleX);
		mHipJoint = mSkeleton.addJoint("Hip",mBreastJoint, 0,hipsY,hipsZ);
		mLeftLegJoint = mSkeleton.addJoint("LeftLeg",mHipJoint, legsX,hipsY,hipsZ).setRadius(smallRad);
		mRightLegJoint = mSkeleton.addJoint("RightLeg",mHipJoint, -legsX,hipsY,hipsZ).setRadius(smallRad);
		mLeftKneeJoint = mSkeleton.addJoint("LeftKnee",mLeftLegJoint, legsX,kneeY,hipsZ).setRadius(medRad);
		mRightKneeJoint = mSkeleton.addJoint("RightKnee",mRightLegJoint, -legsX, kneeY,hipsZ).setRadius(medRad);
		mLeftFootJoint = mSkeleton.addJoint("LeftFoot",mLeftKneeJoint, legsX,0,hipsZ);
		mRightFootJoint = mSkeleton.addJoint("RightFoot",mRightKneeJoint, -legsX,0,hipsZ);
		mLeftShoulderJoint = mSkeleton.addJoint("LeftShoulder",mBreastJoint, shoulderX,shoulderY,breastZ).setRadius(smallRad);
		mRightShoulderJoint = mSkeleton.addJoint("RightShoulder",mBreastJoint, -shoulderX,shoulderY,breastZ).setRadius(smallRad);
		mLeftElbowJoint = mSkeleton.addJoint("LeftElbow",mLeftShoulderJoint, armX,elbowY,breastZ).setRadius(medRad);
		mRightElbowJoint = mSkeleton.addJoint("RightElbow",mRightShoulderJoint, -armX,elbowY,breastZ).setRadius(medRad);
		mLeftHandJoint = mSkeleton.addJoint("LeftHand",mLeftElbowJoint, armX, handY, breastZ);
		mRightHandJoint = mSkeleton.addJoint("RightHand",mRightElbowJoint, -armX, handY, breastZ);

		mSkeleton.mDefaultBoneSpring = 10;
		mBodyBone = (Bone3D)mSkeleton.addSpringBone(new Bone3D("Body",mBreastJoint,mHipJoint));
		mHeadBone = (Bone3D)mSkeleton.addSpringBone(new Bone3D("Head",mBreastJoint,mHeadJoint));
		mLeftUpperArmBone = (Bone3D)mSkeleton.addSpringBone(new Bone3D("LeftShoulder",mBreastJoint,mLeftShoulderJoint));
		mRightUpperArmBone = (Bone3D)mSkeleton.addSpringBone(new Bone3D("RightShoulder",mBreastJoint,mRightShoulderJoint));
		mLeftUpperArmBone = (Bone3D)mSkeleton.addSpringBone(new Bone3D("LeftUpperArm",mLeftShoulderJoint,mLeftElbowJoint));
		mRightUpperArmBone = (Bone3D)mSkeleton.addSpringBone(new Bone3D("RightUpperArm",mRightShoulderJoint,mRightElbowJoint));
		mLeftLowerArmBone = (Bone3D)mSkeleton.addSpringBone(new Bone3D("LeftLowerArm",mLeftElbowJoint,mLeftHandJoint));
		mRightLowerArmBone = (Bone3D)mSkeleton.addSpringBone(new Bone3D("RightLowerArm",mRightElbowJoint,mRightHandJoint));
		mLeftLegBone = (Bone3D)mSkeleton.addSpringBone(new Bone3D("LeftLeg",mHipJoint,mLeftLegJoint));
		mRightLegBone = (Bone3D)mSkeleton.addSpringBone(new Bone3D("RightLeg",mHipJoint,mRightLegJoint));
		mLeftUpperLegBone = (Bone3D)mSkeleton.addSpringBone(new Bone3D("LeftUpperLeg",mLeftLegJoint,mLeftKneeJoint));
		mRightUpperLegBone = (Bone3D)mSkeleton.addSpringBone(new Bone3D("RightUpperLeg",mRightLegJoint,mRightKneeJoint));
		mLeftLowerLegBone = (Bone3D)mSkeleton.addSpringBone(new Bone3D("LeftLowerLeg",mLeftKneeJoint,mLeftFootJoint));
		mRightLowerLegBone = (Bone3D)mSkeleton.addSpringBone(new Bone3D("RightLowerLeg",mRightKneeJoint,mRightFootJoint));

		mBreastBone = (Bone3D)mSkeleton.addSpringBone(new Bone3D("Breast",mLeftShoulderJoint,mRightShoulderJoint));
		mHipsBone = (Bone3D)mSkeleton.addSpringBone(new Bone3D("Hips",mLeftLegJoint,mRightLegJoint));

		return mSkeleton;
	}

	public MassAggregation create(MassAggregation target, HumanoidSkeletonProperties properties) {
		mSkeleton = target;
		create(properties);
		return mSkeleton;
	}

}
