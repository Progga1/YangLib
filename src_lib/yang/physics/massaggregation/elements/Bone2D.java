package yang.physics.massaggregation.elements;


public class Bone2D extends JointConnection {

	//State
	public float mNormDirX,mNormDirY,mNormDirZ,mPrevNormDirX,mPrevNormDirY,mPrevNormDirZ;
	public float mOrthNormX,mOrthNormY;
	
	public Bone2D(String name, Joint joint1, Joint joint2) {
		super(name,joint1,joint2);
	}
	
	public void setAngle(float angle) {
		mJoint2.setPosByAngle(mJoint1, this, angle);
	}

	public void refreshGeometry() {
		super.refreshGeometry();
		mPrevNormDirX = mNormDirX;
		mPrevNormDirY = mNormDirY;
		mPrevNormDirZ = mNormDirZ;
		
		if(mDistance!=0) {
			float d = 1 / mDistance;
			mNormDirX = mDistX * d;
			mNormDirY = mDistY * d;
			mNormDirZ = mDistZ * d;
			mOrthNormX = mNormDirY;
			mOrthNormY = -mNormDirX;
		}
	}
	
}
