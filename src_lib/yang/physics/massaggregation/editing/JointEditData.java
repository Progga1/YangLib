package yang.physics.massaggregation.editing;

import yang.physics.massaggregation.elements.Joint;


public class JointEditData {

	public float mPrevPosX;
	public float mPrevPosY;
	public float mPrevPosZ;

	public int mSelectionIndex = -1;
	public Joint mJoint;

	public void set(Joint joint) {
		mJoint = joint;
	}

	public void setPrevPos() {
		if(mJoint!=null) {
			if(false && mJoint.mDragging) {
				mPrevPosX = mJoint.mDragDelay.mX;
				mPrevPosY = mJoint.mDragDelay.mY;
				mPrevPosZ = mJoint.mDragDelay.mZ;
			}else{
				mPrevPosX = mJoint.mPosX;
				mPrevPosY = mJoint.mPosY;
				mPrevPosZ = mJoint.mPosZ;
			}
		}
	}

}
