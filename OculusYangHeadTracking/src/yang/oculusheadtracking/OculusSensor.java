package yang.oculusheadtracking;

import yang.math.objects.Quaternion;
import yang.systemdependent.YangSensor;
import de.fruitfly.ovr.HMDInfo;
import de.fruitfly.ovr.OculusRift;

public class OculusSensor extends YangSensor {

	private OculusRift mRift;
	public boolean mStopThread = false;
	private final Quaternion mTempQuat = new Quaternion();
	private HMDInfo mHMDInfo;
	private long mFrameCount;

	private final Thread mThread = new Thread(){

		@Override
		public void run() {
			while (!mStopThread && mRift.isInitialized()) {
				mRift.poll();

				mTempQuat.setFromEuler(mRift.getYaw(), mRift.getPitch(), mRift.getRoll());
				if(mFrameCount%(SPEED_FASTEST+1-mSpeed[YangSensor.TYPE_ROTATION_VECTOR])==0)
					mEvents.putSensorEvent(YangSensor.TYPE_ROTATION_VECTOR, mTempQuat.mX,mTempQuat.mY,mTempQuat.mZ);

				try {
					Thread.sleep(20);
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	};

	public boolean connected() {
		if(mRift==null) {
			mRift = new OculusRift();
			mRift.init();
			mHMDInfo = mRift.getHMDInfo();
			mFrameCount = 0;
		}
		return mRift.isInitialized();
	}

	@Override
	protected void derivedStartSensor(int type, int speed) {
		if(type==YangSensor.TYPE_ROTATION_VECTOR) {
			if(mRift==null) {
				mRift = new OculusRift();
				mRift.init();
				mHMDInfo = mRift.getHMDInfo();
				mFrameCount = 0;
			}
			mStopThread = false;
			mThread.start();
		}
	}

	@Override
	protected void derivedStopSensor(int type) {
		mStopThread = true;
	}

	public HMDInfo getHmdInfo() {
		return mHMDInfo;
	}

}
