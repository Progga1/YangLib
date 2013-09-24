package yang.physics.massaggregation.elements;

import yang.graphics.skeletons.CartoonBone;
import yang.graphics.skeletons.CartoonSkeleton2D;

public class JointNormalConstraint extends Joint{

	public CartoonBone mBone;
	public float mRelativeX,mRelativeY;
	public float mWeightJoint1,mWeightJoint2;
	
	public float mForcePosX,mForcePosY;
	
	public JointNormalConstraint(String name, Joint angleParent, CartoonBone cBone, float boneX, float boneY, float radius, CartoonSkeleton2D skeleton) {
		super(name, angleParent, 0, 0, radius, skeleton);
		mBone = cBone;
		mRelativeX = boneX;
		mRelativeY = boneY;
		mBone.refreshGeometry();
		refreshConstraintPos();
		mPosX = mForcePosX;
		mPosY = mForcePosY;
		mSavePose = false;
		setParent(angleParent);
		recalculate();
	}
	
	@Override
	public void recalculate() {
		super.recalculate();
		mWeightJoint1 = 1-Math.min(1, Math.max(0,mRelativeX));
		mWeightJoint2 = Math.min(1, Math.max(0,mRelativeX));
	}

	public void refreshConstraintPos() {
		mBone.refreshGeometry();
		mForcePosX = mBone.mJoint1.mPosX + mBone.mDistX * mRelativeX + mBone.mOrthNormX * mRelativeY;
		mForcePosY = mBone.mJoint1.mPosY + mBone.mDistY * mRelativeX + mBone.mOrthNormY * mRelativeY;
	}
	
	public void setPosByConstraint() {
		refreshConstraintPos();
		mPosX = mForcePosX;
		mPosY = mForcePosY;
	}
	
	@Override
	public void applyConstraint() {
		refreshConstraintPos();
		float dX = mForcePosX - mPosX;
		float dY = mForcePosY - mPosY;
		float dist = (float)Math.sqrt(dX*dX + dY*dY);
		float fac;
		fac = (mVelX*dX+mVelY*dY<0)?AWAY_FACTOR:TOWARDS_FACTOR;
		float fX = dX*2*fac;
		float fY = dY*2*fac;
		if(dist>0) {
			mForceX += fX;
			mForceY += fY;
			fX *= 0.8f;
			fY *= 0.8f;
			mBone.mJoint1.mForceX -= fX*mWeightJoint1;
			mBone.mJoint1.mForceY -= fY*mWeightJoint1;
			mBone.mJoint2.mForceX -= fX*mWeightJoint2;
			mBone.mJoint2.mForceY -= fY*mWeightJoint2;
		}
		
	}
	
}
