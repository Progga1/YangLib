package yang.physics.massaggregation;

import yang.physics.massaggregation.elements.Joint;

public class Skeleton2D extends MassAggregation {

	public float mRotation;
	public float mRotAnchorX;
	public float mRotAnchorY;
	public int mLookDirection;

	public Skeleton2D() {
		super();
		m3D = false;
		mRotation = 0;
		mLookDirection = 1;
	}

	public Joint getJoint(int id) {
		return mJoints.get(id);
	}

}
