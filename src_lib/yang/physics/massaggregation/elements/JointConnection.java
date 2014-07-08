package yang.physics.massaggregation.elements;

import yang.physics.massaggregation.MassAggregation;

public class JointConnection {

	//Properties
	public String mName;
	public Joint mJoint1;
	public Joint mJoint2;

	//State
	public float mNormDirX,mNormDirY,mNormDirZ;
	public float mDistX, mDistY, mDistZ;
	public float mDistance;
	public float mPrevNormDirX,mPrevNormDirY,mPrevNormDirZ;

	public JointConnection(String name, Joint joint1, Joint joint2) {
		mName = name;
		mJoint1 = joint1;
		mJoint2 = joint2;
		refreshGeometry();
	}


	public float calcAngle2D() {
		if(mNormDirY<0)
			return -(float)Math.acos(mNormDirX);
		else
			return (float)Math.acos(mNormDirX);
	}

	public void refreshGeometry() {
		mDistX = mJoint2.mX - mJoint1.mX;
		mDistY = mJoint2.mY - mJoint1.mY;
		mDistZ = mJoint2.mZ - mJoint1.mZ;
		mDistance = (float)Math.sqrt(mDistX*mDistX + mDistY*mDistY + mDistZ*mDistZ);

		mPrevNormDirX = mNormDirX;
		mPrevNormDirY = mNormDirY;
		mPrevNormDirZ = mNormDirZ;

		if(mDistance!=0) {
			float d = 1 / mDistance;
			mNormDirX = mDistX * d;
			mNormDirY = mDistY * d;
			mNormDirZ = mDistZ * d;
		}
	}

	public void setAngle2D(float angle) {
		mJoint2.setPosByAngle2D(mJoint1, this, angle);
	}

	@Override
	public String toString() {
		return "Bone:"+mName+" "+mJoint1.mName+"-"+mJoint2.mName;
	}

	public JointConnection cloneInto(MassAggregation massAggregation) {
		JointConnection bone = new JointConnection(mName,massAggregation.getJointByName(mJoint1.mName),massAggregation.getJointByName(mJoint2.mName));
		massAggregation.addBone(bone);
		return bone;
	}

	public boolean connectsChildParent() {
		return mJoint1.mParent==mJoint2 || mJoint2.mParent==mJoint1;
	}



}
