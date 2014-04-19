package yang.physics.massaggregation.elements;

import yang.graphics.skeletons.CartoonBone;

public class JointNormalConstraint extends Joint{

	public CartoonBone mBone;
	public float mRelativeX,mRelativeY;
	public float mWeightJoint1,mWeightJoint2;
	public float mStrength = 1;

	public float mForcePosX,mForcePosY;

	public JointNormalConstraint(String name, Joint angleParent, CartoonBone cBone, float boneX, float boneY, float radius) {
		super(name, angleParent, 0, 0, radius);
		mBone = cBone;
		mRelativeX = boneX;
		mRelativeY = boneY;
		mBone.refreshGeometry();
		refreshConstraintPos();
		mX = mForcePosX;
		mY = mForcePosY;
		mSavePose = false;
		setParent(angleParent);
		recalculate();
		super.mAnimate = false;
	}

	@Override
	public void recalculate() {
		super.recalculate();
		mWeightJoint1 = 1-Math.min(1, Math.max(0,mRelativeX));
		mWeightJoint2 = Math.min(1, Math.max(0,mRelativeX));
	}

	public void refreshConstraintPos() {
		mBone.refreshGeometry();
		mForcePosX = mBone.mJoint1.mX + mBone.mDistX * mRelativeX + mBone.mNormDirY * mRelativeY;
		mForcePosY = mBone.mJoint1.mY + mBone.mDistY * mRelativeX - mBone.mNormDirX * mRelativeY;
	}

	public void setPosByConstraint() {
		refreshConstraintPos();
		mX = mForcePosX;
		mY = mForcePosY;
	}

	@Override
	public void applyConstraint() {
		refreshConstraintPos();
		float dX = mForcePosX - mX;
		float dY = mForcePosY - mY;
		float dist = (float)Math.sqrt(dX*dX + dY*dY);
		float fac;
		fac = (mVelX*dX+mVelY*dY<0)?AWAY_FACTOR:TOWARDS_FACTOR;
		float fX = dX*2*fac;
		float fY = dY*2*fac;
		if(dist>0) {
			mForceX += fX;
			mForceY += fY;
			fX *= mStrength;
			fY *= mStrength;
			if(!mBone.mJoint1.mFixed) {
				mBone.mJoint1.mForceX -= fX*mWeightJoint1;
				mBone.mJoint1.mForceY -= fY*mWeightJoint1;
			}
			if(!mBone.mJoint2.mFixed) {
				mBone.mJoint2.mForceX -= fX*mWeightJoint2;
				mBone.mJoint2.mForceY -= fY*mWeightJoint2;
			}
		}

	}

}
