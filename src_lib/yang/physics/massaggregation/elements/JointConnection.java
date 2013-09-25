package yang.physics.massaggregation.elements;

public class JointConnection {

	//Properties
	public String mName;
	public Joint mJoint1;
	public Joint mJoint2;
	
	//State
	public float mNormDirX,mNormDirY,mNormDirZ;
	public float mDistX, mDistY, mDistZ;
	public float mDistance;
	public float mPrevNormDirX,mPrevNormDirY,mPrevNormDirZ;

	public JointConnection(String name, Joint joint1, Joint joint2) {
		mName = name;
		mJoint1 = joint1;
		mJoint2 = joint2;
		refreshGeometry();
	}
	
	public void refreshGeometry() {
		mDistX = mJoint2.mPosX - mJoint1.mPosX;
		mDistY = mJoint2.mPosY - mJoint1.mPosY;
		mDistZ = mJoint2.mPosZ - mJoint1.mPosZ;
		mDistance = (float)Math.sqrt(mDistX*mDistX + mDistY*mDistY + mDistZ*mDistZ);
		
		mPrevNormDirX = mNormDirX;
		mPrevNormDirY = mNormDirY;
		mPrevNormDirZ = mNormDirZ;
		
		if(mDistance!=0) {
			float d = 1 / mDistance;
			mNormDirX = mDistX * d;
			mNormDirY = mDistY * d;
			mNormDirZ = mDistZ * d;
		}
	}
	
	public void setAngle2D(float angle) {
		mJoint2.setPosByAngle(mJoint1, this, angle);
	}
	
}
