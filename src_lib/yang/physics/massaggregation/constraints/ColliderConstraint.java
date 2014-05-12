package yang.physics.massaggregation.constraints;

import yang.math.objects.Point3f;
import yang.physics.massaggregation.MassAggregation;
import yang.physics.massaggregation.elements.Joint;

public class ColliderConstraint extends Constraint{

	public static float FIXED_FACTOR = 2;

	public static float IN_FACTOR = 500;
	public static float OUT_FACTOR = 50;

	public Joint mJoint1;
	public Joint mJoint2;
	public float mFriction = 1;
	public boolean m3D = true;
//	private Point3f mTempPnt1 = new Point3f(),mTempPnt2 = new Point3f();

	public ColliderConstraint(Joint joint1,Joint joint2) {
		mJoint1 = joint1;
		mJoint2 = joint2;
	}

	@Override
	public void apply() {
		Point3f point1 = mJoint1.mWorldPosition;
		Point3f point2 = mJoint2.mWorldPosition;
		float dist = point1.getDistance(point2);
		if(dist==0)
			return;

		float minDist = mJoint1.mRadius*mJoint1.mMassAggregation.mScale+mJoint2.mRadius*mJoint2.mMassAggregation.mScale;

		float diff = dist - minDist;

		if(diff>=0)
			return;

		float dDist = 1/dist;
		float normDirX = (point2.mX-point1.mX)*dDist;
		float normDirY = (point2.mY-point1.mY)*dDist;
		float normDirZ = (point2.mZ-point1.mZ)*dDist;

		float fX = normDirX * diff * mStrength;
		float fY = normDirY * diff * mStrength;
		float fZ = normDirZ * diff * mStrength;
		float fac;
		float dVX = mJoint1.mVelX-mJoint2.mVelX;
		float dVY = mJoint1.mVelY-mJoint2.mVelY;
		if(m3D)
			fac = Joint.TOWARDS_FACTOR;
		else
			fac = (dVX*fX+dVY*fY<0)?OUT_FACTOR:IN_FACTOR;
		fac *= mStrength;
		if(mJoint1.mFixed!=mJoint2.mFixed)
			fac *= FIXED_FACTOR;
		if(!mJoint1.mFixed)
			mJoint1.addForce(fX*fac,fY*fac,fZ*fac);
		if(!mJoint2.mFixed)
			mJoint2.addForce(-fX*fac,-fY*fac,-fZ*fac);

		if(mFriction!=1) {
			mJoint1.mVelX *= mFriction;
			mJoint1.mVelY *= mFriction;
			mJoint1.mVelZ *= mFriction;
			mJoint2.mVelX *= mFriction;
			mJoint2.mVelY *= mFriction;
			mJoint2.mVelZ *= mFriction;
		}
	}

	@Override
	public ColliderConstraint cloneInto(MassAggregation target) {
		ColliderConstraint newConstraint = new ColliderConstraint(target.getJointByName(mJoint1.mName),target.getJointByName(mJoint2.mName));
		newConstraint.m3D = m3D;
		newConstraint.mStrength = mStrength;
		return newConstraint;
	}

}
