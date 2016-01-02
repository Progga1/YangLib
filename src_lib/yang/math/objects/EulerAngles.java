package yang.math.objects;

public class EulerAngles {

	public final static EulerAngles ZERO = new EulerAngles(0,0,0);

	public float mYaw,mPitch,mRoll;

	public EulerAngles() {
		set(0,0,0);
	}

	public EulerAngles(float yaw,float pitch,float roll) {
		set(yaw,pitch,roll);
	}

	public EulerAngles(EulerAngles values) {
		set(values);
	}

	public void set(float yaw,float pitch,float roll) {
		mYaw = yaw;
		mPitch = pitch;
		mRoll = roll;
	}

	public void set(float yaw,float pitch) {
		mYaw = yaw;
		mPitch = pitch;
	}

	public void set(EulerAngles values) {
		mYaw = values.mYaw;
		mPitch = values.mPitch;
		mRoll = values.mRoll;
	}

	@Override
	public EulerAngles clone() {
		return new EulerAngles(mYaw,mPitch,mRoll);
	}


	public void setDelayed(EulerAngles targetValues, float delay) {
		mYaw += (targetValues.mYaw-mYaw)*delay;
		mPitch += (targetValues.mPitch-mPitch)*delay;
		mRoll += (targetValues.mRoll-mRoll)*delay;
	}

	@Override
	public String toString() {
		return "yaw="+mYaw+", pitch="+mPitch+", roll="+mRoll;
	}

}
