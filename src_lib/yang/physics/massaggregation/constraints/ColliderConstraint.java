package yang.physics.massaggregation.constraints;

import yang.physics.massaggregation.MassAggregation;
import yang.physics.massaggregation.elements.Joint;

public class ColliderConstraint extends Constraint{

	public static float FIXED_FACTOR = 2;

	public Joint mJoint1;
	public Joint mJoint2;
	public boolean m3D = true;

	public ColliderConstraint(Joint joint1,Joint joint2) {
		mJoint1 = joint1;
		mJoint2 = joint2;
	}

	@Override
	public void apply() {
		float dist = mJoint1.getDistance(mJoint2);
		if(dist==0)
			return;

		float minDist = mJoint1.mRadius+mJoint2.mRadius;

		float diff = dist - minDist;

		if(diff>=0)
			return;

		float dDist = 1/dist;
		float normDirX = (mJoint2.mX-mJoint1.mX)*dDist;
		float normDirY = (mJoint2.mY-mJoint1.mY)*dDist;
		float normDirZ = (mJoint2.mZ-mJoint1.mZ)*dDist;

		float fX = normDirX * diff * mStrength;
		float fY = normDirY * diff * mStrength;
		float fZ = normDirZ * diff * mStrength;
		float fac;
		float dVX = mJoint1.mVelX-mJoint2.mVelX;
		float dVY = mJoint1.mVelY-mJoint2.mVelY;
		if(m3D)
			fac = Joint.TOWARDS_FACTOR;
		else
			fac = (dVX*fX+dVY*fY<0)?Joint.AWAY_FACTOR:Joint.TOWARDS_FACTOR;

		if(mJoint1.mFixed!=mJoint2.mFixed)
			fac *= FIXED_FACTOR;
		if(!mJoint1.mFixed)
			mJoint1.addForce(fX*fac,fY*fac,fZ*fac);
		if(!mJoint2.mFixed)
			mJoint2.addForce(-fX*fac,-fY*fac,-fZ*fac);
	}

	@Override
	public Constraint cloneInto(MassAggregation target) {
		//TODO implement me!
		return null;
	}

}
