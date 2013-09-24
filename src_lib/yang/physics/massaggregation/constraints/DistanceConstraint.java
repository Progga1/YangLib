package yang.physics.massaggregation.constraints;

import yang.graphics.skeletons.CartoonBone;
import yang.physics.massaggregation.elements.Joint;

public class DistanceConstraint extends Constraint{
	
	public float mForceDistance;
	public CartoonBone mBone;
	public boolean m3D = true;
	
	public DistanceConstraint(CartoonBone bone, float strength) {
		mBone = bone;
		mStrength = strength;
		mForceDistance = mBone.mDistance;
		m3D = bone.mJoint1.mSkeleton.m3D;
	}
	
	public DistanceConstraint(CartoonBone bone) {
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
			
			mBone.mJoint1.addForce(fX*fac,fY*fac,fZ*fac);
			//fac = (mBone.mJoint2.mVelX*(-fX)+mBone.mJoint2.mVelY*(-fY)<0)?Joint.AWAY_FACTOR:Joint.TOWARDS_FACTOR;
			mBone.mJoint2.addForce(-fX*fac,-fY*fac,-fZ*fac);
		}
	}

	@Override
	public boolean containsBone(CartoonBone bone) {
		return mBone==bone;
	}
	
}
