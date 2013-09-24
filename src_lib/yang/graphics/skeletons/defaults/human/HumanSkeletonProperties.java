package yang.graphics.skeletons.defaults.human;

public class HumanSkeletonProperties {

	public float mHeight;
	public float mShoulderWidth;
	public float mShoulderOffsetY = 0.02f;
	public float mHeadRatio = 0.2f;
	public float mHipRatio = 0.5f;
	public float mHipWidth;
	public float mArmLength = 0.4f;
	
	public HumanSkeletonProperties(float height,float shoulderWidth,float hipWidth) {
		mHeight = height;
		mShoulderWidth = shoulderWidth;
		mHipWidth = hipWidth;
		
	}
	
	public HumanSkeletonProperties() {
		this(2,0.6f,0.3f);
	}
	
}
