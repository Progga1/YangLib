package yang.physics.massaggregation.editing;

import yang.graphics.model.FloatColor;
import yang.math.objects.Point3f;
import yang.math.objects.Vector3f;
import yang.physics.massaggregation.constraints.DistanceConstraint;
import yang.physics.massaggregation.elements.Joint;


public class JointEditData {

	//Objects
	public Joint mJoint;
	public DistanceConstraint mParentConnection;
	public Skeleton3DEditing mSkeletonEdit;

	//Settings
	public float mOutputRadiusFactor = 1;

	//State
	public boolean mVisible = true;

	//Selection
	public int mSelectionGroup = -1;
	public int mSelectionDepth = -1;
	public float mLstSelectTime = -1;
	public boolean mSelectable = true;

	public Point3f mPrevPos = new Point3f();
	public Vector3f mMovement = new Vector3f();
	public FloatColor mDefaultColor = new FloatColor(0.8f,0.8f,0.8f);

	public void set(Joint joint,Skeleton3DEditing skeletonEdit) {
		mJoint = joint;
		mSkeletonEdit = skeletonEdit;
		if(mJoint.mAngleParent!=null)
			mParentConnection = mJoint.mMassAggregation.getDistanceConstraint(mJoint.mAngleParent,mJoint);
		else
			mParentConnection = null;
	}

	public void setPrevPos() {
		if(mJoint!=null) {
			mMovement.setFromTo(mPrevPos,mJoint);
			mPrevPos.set(mJoint);
		}

	}

	public boolean isSelected() {
		return mSelectionGroup>-1;
	}

	@Override
	public String toString() {
		return "selection group/depth="+mSelectionGroup+"/"+mSelectionDepth+"; Joint="+mJoint;
	}

	public float getOutputRadius() {
		if(mJoint!=null)
			return mJoint.getOutputRadius()*mOutputRadiusFactor;
		else
			return 0.3f;
	}

}
