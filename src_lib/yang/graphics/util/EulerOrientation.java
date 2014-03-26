package yang.graphics.util;

import yang.math.objects.Quaternion;
import yang.math.objects.YangMatrix;

public class EulerOrientation {

	private YangMatrix mMatrix = new YangMatrix();
	private Quaternion mQuaternion = new Quaternion();

	public float mYaw,mPitch,mRoll;

	public YangMatrix getUpdatedMatrix() {
		mMatrix.setFromEulerAngles(mYaw,mPitch,mRoll);
		return mMatrix;
	}

	public Quaternion getUpdatedQuaternion() {
		mQuaternion.setFromEuler(mYaw, mPitch, mRoll);
		return mQuaternion;
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
