package yang.physics.massaggregation.editing;

import yang.graphics.model.FloatColor;

public interface JointDrawCallback {

	public void getJointColor(JointEditData joint,FloatColor target);
	public void getJointLineColor(JointEditData joint,FloatColor target);
	public float getJointRadius(JointEditData joint);

}
