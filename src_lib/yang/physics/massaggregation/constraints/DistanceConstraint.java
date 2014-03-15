package yang.physics.massaggregation.constraints;

import yang.physics.massaggregation.MassAggregation;
import yang.physics.massaggregation.elements.Joint;
import yang.physics.massaggregation.elements.JointConnection;

public class DistanceConstraint extends Constraint{

	public static float FIXED_FACTOR = 2;

	public float mForceDistance;
	public JointConnection mBone;
	public boolean mApplyToJoint1 = true, mApplyToJoint2 = true;
	public boolean m3D = true;
	public float mMaxDiff = 0;

	public DistanceConstraint(JointConnection bone, float strength) {
		mBone = bone;
		mStrength = strength;
		mForceDistance = mBone.mDistance;
		m3D = bone.mJoint1.mMassAggregation.m3D;
	}

	public DistanceConstraint(JointConnection bone) {
		this(bone,10);
	}

	@Override
	public void recalculate() {
		mBone.refreshGeometry();
		mForceDistance = mBone.mDistance;
	}

	@Override
	public void apply() {
		if(mEnabled) {
			mBone.refreshGeometry();

			float diff = mBone.mDistance - mForceDistance;

			if(diff>=0 && diff<mMaxDiff)
				return;

			float fX = mBone.mNormDirX * diff * mStrength;
			float fY = mBone.mNormDirY * diff * mStrength;
			float fZ = mBone.mNormDirZ * diff * mStrength;
			float fac;
			float dVX = mBone.mJoint1.mVelX-mBone.mJoint2.mVelX;
			float dVY = mBone.mJoint1.mVelY-mBone.mJoint2.mVelY;
			if(m3D)
				fac = Joint.TOWARDS_FACTOR;
			else
				fac = (dVX*fX+dVY*fY<0)?Joint.AWAY_FACTOR:Joint.TOWARDS_FACTOR;

			if(mBone.mJoint1.mFixed!=mBone.mJoint2.mFixed)
				fac *= FIXED_FACTOR;
			if(mApplyToJoint1 && !mBone.mJoint1.mFixed)
				mBone.mJoint1.addForce(fX*fac,fY*fac,fZ*fac);
			//fac = (mBone.mJoint2.mVelX*(-fX)+mBone.mJoint2.mVelY*(-fY)<0)?Joint.AWAY_FACTOR:Joint.TOWARDS_FACTOR;
			if(mApplyToJoint2 && !mBone.mJoint2.mFixed)
				mBone.mJoint2.addForce(-fX*fac,-fY*fac,-fZ*fac);
		}
	}

	public void setAngle2D(float angle) {
		mBone.mJoint2.setPosByAngle2D(mBone.mJoint1, mForceDistance, angle);
	}

	@Override
	public boolean containsBone(JointConnection bone) {
		return mBone==bone;
	}

	public void disableJoint(Joint joint) {
		if(mBone.mJoint1==joint)
			mApplyToJoint1 = false;
		else if(mBone.mJoint2==joint)
			mApplyToJoint2 = false;
		else
			throw new RuntimeException(joint+" is not a joint of this constraint ("+this+")");
	}

	@Override
	public String toString() {
		return mBone.mJoint1+"-"+mBone.mJoint2;
	}

	@Override
	public DistanceConstraint cloneInto(MassAggregation target) {
		DistanceConstraint result = new DistanceConstraint(target.getConnectionByName(mBone.mName));
		result.mForceDistance = mForceDistance;
		result.mStrength = mStrength;
		target.addConstraint(result);
		return result;
	}

}
