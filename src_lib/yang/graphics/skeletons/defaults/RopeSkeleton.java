package yang.graphics.skeletons.defaults;

import yang.graphics.skeletons.elements.Bone;
import yang.graphics.skeletons.elements.Joint;
import yang.physics.massaggregation.MassAggregation;
import yang.physics.massaggregation.constraints.AngleConstraint;

public class RopeSkeleton extends MassAggregation {
	
	private int mJointCount = 1;
	private float mRopeLength = 2;
	private float mWidth = 1;

	public float mJointMass = 1.5f;
	
	public RopeSkeleton(int jointCount,float length,float width) {
		super();
		mJointCount = jointCount;
		mRopeLength = length;
		this.mLowerLimit = -1024;
		mWidth = width;
		mConstantForceY = -0.3f;
	}
	
	protected void build() {
		float lenPerJoint = mRopeLength/Math.max(1,mJointCount-1);
		Joint prevJoint = null;
		Bone prevBone = null;
		for(int i=0;i<mJointCount;i++) {
			Joint joint = new Joint("R"+i,prevJoint,0,-i*lenPerJoint,lenPerJoint*0.5f,this);
			joint.mMass = mJointMass;
			joint.setInitials();
			super.addJoint(joint);
			if(i>0) {
				Bone bone = new Bone(mTranslator,"RB"+i, prevJoint,joint);
				bone.setWidth(mWidth);
				bone.putTextureCoords(0,0, 1,1);
				bone.setShiftY(0, -0.1f);
				//DistanceConstraint constraint = new DistanceConstraint(bone);
				super.addBone(bone, 0, 65);
				if(prevBone!=null)
					super.addConstraint(new AngleConstraint(prevBone,bone, -0.2f,0.2f));
				prevBone = bone;
			}else{
				joint.mFixed = true;
			}
			prevJoint = joint;
		}
		mJoints.get(5).mVelX = 1.5f;
	}
	
}
