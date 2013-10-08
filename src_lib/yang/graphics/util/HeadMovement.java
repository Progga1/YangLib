package yang.graphics.util;

import yang.math.objects.matrix.YangMatrix;

public class HeadMovement {

	private YangMatrix mMatrix = new YangMatrix();
	
	public float mYaw,mPitch,mRoll;

	public YangMatrix getUpdatedMatrix() {
		mMatrix.fromEulerAngles(-mYaw,-mPitch,mRoll);
		return mMatrix;
	}
	
	@Override
	public String toString() {
		return "yaw,pitch,roll="+mYaw+","+mPitch+","+mRoll;
	}

	public void reset() {
		mYaw = 0;
		mPitch = 0;
		mRoll = 0;
	}
	
}
