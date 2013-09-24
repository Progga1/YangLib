package yang.physics.massaggregation.editing;

import yang.physics.massaggregation.elements.Joint;


public class JointEditData {

	public int mSelectionIndex = -1;
	public Joint mJoint;

	public void set(Joint joint) {
		mJoint = joint;
	}
	
}
