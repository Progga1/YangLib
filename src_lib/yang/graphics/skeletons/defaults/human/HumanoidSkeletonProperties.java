package yang.graphics.skeletons.defaults.human;

public class HumanoidSkeletonProperties {

	public float mHeight;
	public float mShoulderWidth;
	public float mShoulderOffsetY = 0.02f;
	public float mBreastY = 0.8f;
	public float mBreastZ = 0;
	public float mHeadZ = 0;
	public float mHipsY = 0.5f;
	public float mHipsZ = 0;
	public float mHipWidth;
	public float mArmLength = 0.4f;

	public HumanoidSkeletonProperties(float height,float shoulderWidth,float hipWidth) {
		mHeight = height;
		mShoulderWidth = shoulderWidth;
		mHipWidth = hipWidth;

	}

	public HumanoidSkeletonProperties() {
		this(2,0.6f,0.3f);
	}

}
