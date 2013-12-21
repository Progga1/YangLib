package yang.physics.massaggregation.editing;

import yang.math.objects.Point3f;
import yang.physics.massaggregation.constraints.DistanceConstraint;
import yang.physics.massaggregation.elements.Joint;


public class JointEditData {

	//Objects
	public Joint mJoint;
	public DistanceConstraint mParentConnection;
	public Skeleton3DEditing mSkeletonEdit;

	//Selection
	public int mSelectionGroup = -1;
	public int mSelectionDepth = -1;
	public float mLstSelectTime = -1;

	public Point3f mPrevPos = new Point3f();

	public void set(Joint joint,Skeleton3DEditing skeletonEdit) {
		mJoint = joint;
		mSkeletonEdit = skeletonEdit;
		if(mJoint.mAngleParent!=null)
			mParentConnection = mJoint.mSkeleton.getDistanceConstraint(mJoint.mAngleParent,mJoint);
		else
			mParentConnection = null;
	}

	public void setPrevPos() {
		if(mJoint!=null) {
			if(false && mJoint.mDragging) {
				mPrevPos.mX = mJoint.mDragDelayed.mX;
				mPrevPos.mY = mJoint.mDragDelayed.mY;
				mPrevPos.mZ = mJoint.mDragDelayed.mZ;
			}else{
				mPrevPos.mX = mJoint.mX;
				mPrevPos.mY = mJoint.mY;
				mPrevPos.mZ = mJoint.mZ;
			}
		}
	}

	public boolean isSelected() {
		return mSelectionGroup>-1;
	}

	@Override
	public String toString() {
		return "selection group/depth="+mSelectionGroup+"/"+mSelectionDepth+"; Joint="+mJoint;
	}

}
