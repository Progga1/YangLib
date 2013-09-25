package yang.graphics.skeletons.defaults;

import yang.graphics.skeletons.CartoonBone;
import yang.physics.massaggregation.MassAggregation;
import yang.physics.massaggregation.Skeleton2D;
import yang.physics.massaggregation.constraints.AngleConstraint;
import yang.physics.massaggregation.elements.Joint;

public class RopeSkeletonCreator {
	
	private int mJointCount = 1;
	private float mRopeLength = 2;
	private float mWidth = 1;

	public float mJointMass = 1.5f;
	public MassAggregation mSkeleton; 
	
	public RopeSkeletonCreator(int jointCount,float length,float width) {
		super();
		mJointCount = jointCount;
		mRopeLength = length;
		mWidth = width;
	}
	
	public MassAggregation create() {
		if(mSkeleton==null) {
			mSkeleton = new MassAggregation();
		}
		float lenPerJoint = mRopeLength/Math.max(1,mJointCount-1);
		Joint prevJoint = null;
		CartoonBone prevBone = null;
		for(int i=0;i<mJointCount;i++) {
			Joint joint = new Joint("R"+i,prevJoint,0,-i*lenPerJoint,lenPerJoint*0.5f,mSkeleton);
			joint.mMass = mJointMass;
			joint.setInitials();
			mSkeleton.addJoint(joint);
			if(i>0) {
				CartoonBone bone = new CartoonBone(null,"RB"+i, prevJoint,joint);
				bone.setWidth(mWidth);
				mSkeleton.addSpringBone(bone, 30);
				if(prevBone!=null)
					mSkeleton.addConstraint(new AngleConstraint(prevBone,bone, -0.2f,0.2f));
				prevBone = bone;
			}else{
				joint.mFixed = true;
			}
			prevJoint = joint;
		}
		return mSkeleton;
	}

	public void setTarget(Skeleton2D target) {
		mSkeleton = target;
	}
	
}
