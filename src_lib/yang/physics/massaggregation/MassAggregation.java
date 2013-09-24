package yang.physics.massaggregation;

import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.skeletons.SkeletonCarrier;
import yang.graphics.skeletons.defaults.DefaultSkeletonCarrier;
import yang.graphics.skeletons.elements.Bone;
import yang.graphics.skeletons.elements.Joint;
import yang.graphics.skeletons.pose.Posture;
import yang.graphics.translator.GraphicsTranslator;
import yang.model.Rect;
import yang.physics.massaggregation.constraints.Constraint;
import yang.physics.massaggregation.constraints.DistanceConstraint;
import yang.util.NonConcurrentList;

public class MassAggregation {

	public static int DEFAULT_ACCURACY = 16;
	
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
	
	//Objects
	public DefaultGraphics<?> mGraphics;
	public GraphicsTranslator mTranslator;
	public SkeletonCarrier mCarrier;
	
	//Data
	public NonConcurrentList<Joint> mJoints;
	public NonConcurrentList<NonConcurrentList<Bone>> mLayersList;
	public NonConcurrentList<Bone> mBones;
	public NonConcurrentList<Constraint> mConstraints;
	public Rect mBoundariesRect;

	//State
	public float mShiftX = 0;
	public float mShiftY = 0;
	public float mShiftZ = 0;
	public boolean mConstraintsActivated;
	public Posture mCurrentPose;
	private boolean mInitialized;
	public float mScale = 1;
	private int mCurJointId = 0;
	
	
	public MassAggregation() {
		mJoints = new NonConcurrentList<Joint>();
		mBones = new NonConcurrentList<Bone>();
		mLayersList = new NonConcurrentList<NonConcurrentList<Bone>>();
		mConstraints = new NonConcurrentList<Constraint>();
		
		m3D = true;
		mConstraintsActivated = true;
		mInitialized = false;
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
		for(Bone bone:mBones) {
			bone.mVisible = visible;
		}
	}
	
	public void init(DefaultGraphics<?> graphics,SkeletonCarrier carrier) {
		mCarrier = carrier;

		mGraphics = graphics;
		mTranslator = mGraphics.mTranslator;
		
		mCurJointId = 0;
		build();
		
		mBoundariesRect = new Rect();
		refreshBoundariesRect();
		mInitialized = true;
		
		finish();
	}
	
	protected void finish() {
		
	}
	
	public void refreshBoundariesRect() {
		mBoundariesRect.set(100000,-100000,-100000,100000);
		for(Joint joint:mJoints) {
			if(joint.mPosX<mBoundariesRect.mLeft)
				mBoundariesRect.mLeft = joint.mPosX;
			if(joint.mPosX>mBoundariesRect.mRight)
				mBoundariesRect.mRight = joint.mPosX;
			if(joint.mPosY>mBoundariesRect.mTop)
				mBoundariesRect.mTop = joint.mPosY;
			if(joint.mPosY<mBoundariesRect.mBottom)
				mBoundariesRect.mBottom = joint.mPosY;
		}
	}
	
	public void init(DefaultGraphics<?> graphics) {
		init(graphics,new DefaultSkeletonCarrier(this));
	}
	
	public void addJoint(Joint joint) {
		mJoints.add(joint);
	}
	
	public void addConstraint(Constraint constraint) {
		mConstraints.add(constraint);
	}
	
	public void addBone(Bone bone,int layer,float constraintDistanceStrength) {
		while(layer>mLayersList.size()-1)
		{
			mLayersList.add(new NonConcurrentList<Bone>());
		}
		mLayersList.get(layer).add(bone);
		mBones.add(bone);
		if(constraintDistanceStrength>0)
			addConstraint(new DistanceConstraint(bone,constraintDistanceStrength));
	}

	public void addBone(Bone bone,int layer) {
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
		return mCarrier.getWorldX() + (mShiftX + joint.mPosX*mCarrier.getLookDirection())*mCarrier.getScale()*mScale;
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
		for(Bone connection:mBones)
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

	public boolean isInitialized() {
		return mInitialized;
	}
	
	@SuppressWarnings("unchecked")
	public <ConstraintType extends Constraint> ConstraintType getBoneConstraint(Bone bone,Class<ConstraintType> type) {
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
	
}
