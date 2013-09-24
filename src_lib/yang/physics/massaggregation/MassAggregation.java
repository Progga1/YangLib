package yang.physics.massaggregation;

import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.skeletons.CartoonBone;
import yang.graphics.skeletons.SkeletonCarrier;
import yang.graphics.skeletons.defaults.DefaultSkeletonCarrier;
import yang.graphics.skeletons.defaults.NeutralSkeletonCarrier;
import yang.graphics.skeletons.pose.Posture;
import yang.graphics.translator.GraphicsTranslator;
import yang.model.Rect;
import yang.physics.massaggregation.constraints.Constraint;
import yang.physics.massaggregation.constraints.DistanceConstraint;
import yang.physics.massaggregation.elements.Joint;
import yang.util.NonConcurrentList;

public class MassAggregation {

	public static int DEFAULT_ACCURACY = 16;
	protected static SkeletonCarrier NEUTRAL_CARRIER = new NeutralSkeletonCarrier();
	
	//Properties
	public float mFloorFriction = 0.98f;
	public float mConstantForceX;
	public float mConstantForceY;
	public float mConstantForceZ;
	public float mLowerLimit;
	public float mLimitForceInwards;
	public float mLimitForceOutwards;
	public int mAccuracy;
	public boolean m3D;
	public float mDefaultJointRadius = 0.1f;
	
	//Objects
	public SkeletonCarrier mCarrier;
	
	//Data
	public NonConcurrentList<Joint> mJoints;
	public NonConcurrentList<NonConcurrentList<CartoonBone>> mLayersList;
	public NonConcurrentList<CartoonBone> mBones;
	public NonConcurrentList<Constraint> mConstraints;

	//State
	public float mShiftX = 0;
	public float mShiftY = 0;
	public float mShiftZ = 0;
	public boolean mConstraintsActivated;
	public Posture mCurrentPose;
	public float mScale = 1;
	protected int mCurJointId = 0;
	
	
	public MassAggregation() {
		mJoints = new NonConcurrentList<Joint>();
		mBones = new NonConcurrentList<CartoonBone>();
		mLayersList = new NonConcurrentList<NonConcurrentList<CartoonBone>>();
		mConstraints = new NonConcurrentList<Constraint>();
		
		mCarrier = NEUTRAL_CARRIER;
		m3D = true;
		mConstraintsActivated = true;
		mAccuracy = DEFAULT_ACCURACY;
		mConstantForceX = 0;
		mConstantForceY = 0;
		mConstantForceZ = 0;
		mLimitForceInwards = 20f;
		mLimitForceOutwards = 10f;
		mLowerLimit = Float.MIN_VALUE;
	}
	
	protected void build() {
		
	}
	
	public void recalculateConstraints() {
		for(Joint joint:mJoints) {
			joint.recalculate();
		}
		for(Constraint constraint:mConstraints)
			constraint.recalculate();
	}
	
	public void setBonesVisible(boolean visible) {
		for(CartoonBone bone:mBones) {
			bone.mVisible = visible;
		}
	}
	
	public void get2DBoundaries(Rect target) {
		target.set(100000,-100000,-100000,100000);
		for(Joint joint:mJoints) {
			if(joint.mPosX<target.mLeft)
				target.mLeft = joint.mPosX;
			if(joint.mPosX>target.mRight)
				target.mRight = joint.mPosX;
			if(joint.mPosY>target.mTop)
				target.mTop = joint.mPosY;
			if(joint.mPosY<target.mBottom)
				target.mBottom = joint.mPosY;
		}
	}
	
	public Joint addJoint(Joint joint) {
		mJoints.add(joint);
		return joint;
	}
	
	public Joint addJoint(String name,Joint parent,float x,float y,float z) {
		Joint newJoint = new Joint(name, parent, x,y, mDefaultJointRadius, this);
		newJoint.mPosZ = z;
		return addJoint(newJoint);
	}
	
	public Joint addJoint(Joint parent,float x,float y,float z) {
		return addJoint("JOINT",parent,x,y,z);
	}
	
	public void addConstraint(Constraint constraint) {
		mConstraints.add(constraint);
	}
	
	public void addBone(CartoonBone bone,int layer,float constraintDistanceStrength) {
		while(layer>mLayersList.size()-1)
		{
			mLayersList.add(new NonConcurrentList<CartoonBone>());
		}
		mLayersList.get(layer).add(bone);
		mBones.add(bone);
		if(constraintDistanceStrength>0)
			addConstraint(new DistanceConstraint(bone,constraintDistanceStrength));
	}

	public void addBone(CartoonBone bone,int layer) {
		addBone(bone,layer,10);
	}

	public Joint getBoneByName(String name) {
		name = name.toUpperCase();
		for(Joint bone:mJoints) {
			if(bone.mName.equals(name))
				return bone;
		}
		return null;
	}
	
	public float getJointWorldX(Joint joint) {
		return mCarrier.getWorldX() + (mShiftX + joint.mPosX)*mCarrier.getScale()*mScale;
	}
	
	public float getJointWorldY(Joint joint) {
		return mCarrier.getWorldY() + (mShiftY + joint.mPosY)*mCarrier.getScale()*mScale;
	}
	
	public void setOffset(float x, float y) {
		mShiftX = x;
		mShiftY = y;
	}
	
	public float toJointX(float x) {
		return (x-mCarrier.getWorldX())*mCarrier.getScale();
	}
	
	public float toJointY(float y) {
		return (y-mCarrier.getWorldY())*mCarrier.getScale();
	}
	
	public void refreshVisualVars() {
		for(CartoonBone connection:mBones)
			connection.refreshVisualVars();
	}
	
	public void applyConstraints(float deltaTime) {
		
		float uDeltaTime = deltaTime/mAccuracy;
		float worldY = mCarrier.getWorldY();
		for(int i=0;i<mAccuracy;i++) {
			
			//Init force
			for(Joint joint:mJoints) {
				joint.mForceX = mConstantForceX*joint.mMass;
				joint.mForceY = mConstantForceY*joint.mMass;
				joint.mForceZ = mConstantForceZ*joint.mMass;
				
				if(joint.mPosY+worldY<mLowerLimit) {
					float uForce = (joint.mVelY<0)?mLimitForceInwards:mLimitForceOutwards;
					joint.mForceY += (mLowerLimit-joint.mPosY)*uForce;
					joint.mVelX *= mFloorFriction;
				}
			}
			
			//Apply constraints
			if(mConstraintsActivated)
				for(Constraint constraint:mConstraints) {
					constraint.apply();
				}
			
			if(mConstraintsActivated)
				for(Joint joint:mJoints) {
					joint.applyConstraint();
				}
			
			for(Joint joint:mJoints) {
				joint.physicalStep(uDeltaTime);
			}
		
		}
		
	}

	public void reApplyPose() {
		if(mCurrentPose!=null)
			mCurrentPose.applyPose(this);
	}
	
	@SuppressWarnings("unchecked")
	public <ConstraintType extends Constraint> ConstraintType getBoneConstraint(CartoonBone bone,Class<ConstraintType> type) {
		for(Constraint constraint:mConstraints) {
			if((constraint.getClass()==type) && (constraint.containsBone(bone)))
				return (ConstraintType)constraint;
		}
		return null;
	}
	
	public void reset() {
		for(Joint joint:mJoints) {
			joint.reset();
		}
	}

	public void setFriction(float friction) {
		for(Joint joint:mJoints) {
			joint.mFriction = friction;
		}
	}

	public int getNextJointId() {
		return mCurJointId++;
	}
	
	public void setCarrier(SkeletonCarrier carrier) {
		mCarrier = carrier;
	}
	
}
