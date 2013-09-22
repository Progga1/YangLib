package yang.graphics.skeletons.defaults;

import yang.graphics.skeletons.Skeleton;
import yang.graphics.skeletons.elements.Bone;
import yang.graphics.skeletons.elements.Joint;

public class RopeSkeleton extends Skeleton {

	private int mJointCount = 1;
	private float mRopeLength = 2;
	private float mWidth = 1;
	
	public RopeSkeleton(int jointCount,float length,float width) {
		super();
		mJointCount = jointCount;
		mRopeLength = length;
		this.mLowerLimit = -1024;
		mWidth = width;
	}
	
	protected void build() {
		float lenPerJoint = mRopeLength/Math.max(1,mJointCount-1);
		Joint prevJoint = null;
		for(int i=0;i<mJointCount;i++) {
			Joint joint = new Joint("R"+i,prevJoint,0,-i*lenPerJoint,lenPerJoint*0.5f,this);
			super.addJoint(joint);
			if(i>0) {
				Bone bone = new Bone(mTranslator,"RB"+i, prevJoint,joint);
				bone.setWidth(mWidth);
				bone.putTextureCoords(0,0, 1,1);
				bone.setShiftY(0, -0.1f);
				//DistanceConstraint constraint = new DistanceConstraint(bone);
				super.addBone(bone, 0, 2);
			}else{
				joint.mFixed = true;
			}
			prevJoint = joint;
		}
		mJoints.get(5).mVelX = 1.5f;
	}
	
}
