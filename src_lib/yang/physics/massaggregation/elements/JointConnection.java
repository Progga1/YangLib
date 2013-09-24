package yang.physics.massaggregation.elements;

public class JointConnection {

	//Properties
	public String mName;
	public Joint mJoint1;
	public Joint mJoint2;
	
	//State
	public float mDistX, mDistY, mDistZ;
	public float mDistance;

	public JointConnection(String name, Joint joint1, Joint joint2) {
		mName = name;
		mJoint1 = joint1;
		mJoint2 = joint2;
	}
	
	public void refreshGeometry() {
		mDistX = mJoint2.mPosX - mJoint1.mPosX;
		mDistY = mJoint2.mPosY - mJoint1.mPosY;
		mDistZ = mJoint2.mPosZ - mJoint1.mPosZ;
		mDistance = (float)Math.sqrt(mDistX*mDistX + mDistY*mDistY + mDistZ*mDistZ);
	}
	
}
