package yang.physics.massaggregation.constraints;

import yang.graphics.skeletons.CartoonBone;
import yang.physics.massaggregation.MassAggregation;
import yang.physics.massaggregation.elements.JointConnection;

public class AngleConstraint extends Constraint{

	public JointConnection mBone1,mBone2;

	private float mPrevUNormDirX;
	private float mPrevUNormDirY;
	private float mPrevAngle;

	public float mSpanAngle;
	public float mShiftAngle;

	public AngleConstraint(JointConnection bone1,JointConnection bone2,float fromAngle,float toAngle) {
		mBone1 = bone1;
		mBone2 = bone2;

		mSpanAngle = (toAngle-fromAngle)/2;
		mShiftAngle = (mSpanAngle+fromAngle);
		mStrength = 3;
	}

	public AngleConstraint(JointConnection bone1,JointConnection bone2) {
		this(bone1,bone2,0,0);
	}

	@Override
	public void apply() {
		mBone1.refreshGeometry();
		mBone2.refreshGeometry();
		float uNormX = (float)(Math.cos(-mShiftAngle)*mBone1.mNormDirX + Math.sin(-mShiftAngle)*mBone1.mNormDirY);
		float uNormY = (float)(-Math.sin(-mShiftAngle)*mBone1.mNormDirX + Math.cos(-mShiftAngle)*mBone1.mNormDirY);
		float angle = (float)Math.acos(mBone2.mNormDirX*uNormX + mBone2.mNormDirY*uNormY);

		if(angle>mSpanAngle) {
			float cross = uNormY*mBone2.mNormDirX - uNormX*mBone2.mNormDirY;
			float fac = Math.abs(angle)>Math.abs(mPrevAngle)?0.4f:0.15f;
			float f = (angle-mSpanAngle)*mStrength*fac*20f;
			if(cross>0)
				f = -f;
			float off = 1;
			if(!mBone2.mJoint2.mFixed) {
				mBone2.mJoint2.mForceX += mBone2.mNormDirY*f;
				mBone2.mJoint2.mForceY -= mBone2.mNormDirX*f;
			}
			if(!mBone2.mJoint1.mFixed) {
				mBone2.mJoint1.mForceX -= mBone2.mNormDirY*f * off;
				mBone2.mJoint1.mForceY += mBone2.mNormDirX*f * off;
			}
			if(!mBone1.mJoint2.mFixed) {
				mBone1.mJoint2.mForceX -= mBone1.mNormDirY*f * off;
				mBone1.mJoint2.mForceY += mBone1.mNormDirX*f * off;
			}
			if(!mBone1.mJoint1.mFixed) {
				mBone1.mJoint1.mForceX += mBone1.mNormDirY*f;
				mBone1.mJoint1.mForceY -= mBone1.mNormDirX*f;
			}
			//mBone1.mJoint2.mForceX += mBone2.mOrthNormX*f*0.5f;
			//mBone1.mJoint2.mForceY += mBone2.mOrthNormY*f*0.5f;
		}
		mPrevUNormDirX = uNormX;
		mPrevUNormDirY = uNormY;
		mPrevAngle = angle;
	}

	@Override
	public boolean containsBone(CartoonBone bone) {
		return mBone1==bone || mBone2==bone;
	}

	@Override
	public AngleConstraint cloneInto(MassAggregation target) {
		AngleConstraint newConstr = new AngleConstraint(target.getConnectionByName(mBone1.mName),target.getConnectionByName(mBone2.mName));
		newConstr.mSpanAngle = mSpanAngle;
		newConstr.mShiftAngle = mShiftAngle;
		target.addConstraint(newConstr);
		return newConstr;
	}

}
