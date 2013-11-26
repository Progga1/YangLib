package yang.physics.massaggregation.editing;

import yang.physics.massaggregation.elements.Joint;


public class JointEditData {

	public float mPrevPosX;
	public float mPrevPosY;
	public float mPrevPosZ;

	public int mSelectionGroup = -1;
	public int mSelectionDepth = -1;
	public Joint mJoint;

	public void set(Joint joint) {
		mJoint = joint;
	}

	public void setPrevPos() {
		if(mJoint!=null) {
			if(false && mJoint.mDragging) {
				mPrevPosX = mJoint.mDragDelayed.mX;
				mPrevPosY = mJoint.mDragDelayed.mY;
				mPrevPosZ = mJoint.mDragDelayed.mZ;
			}else{
				mPrevPosX = mJoint.mPosX;
				mPrevPosY = mJoint.mPosY;
				mPrevPosZ = mJoint.mPosZ;
			}
		}
	}

}
