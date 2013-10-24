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
	private boolean mThreadActive = false;

	private final Thread mThread = new Thread(){

		@Override
		public void run() {
			while (!mStopThread) {
				mRift.poll();

				if(mSensorActive[YangSensor.TYPE_ROTATION_VECTOR] && mFrameCount%(SPEED_FASTEST+1-mSpeed[YangSensor.TYPE_ROTATION_VECTOR])==0) {
					mTempQuat.setFromEuler(mRift.getYaw(), mRift.getPitch(), mRift.getRoll());
					//mTempQuat.setFromEuler2(mRift.getYaw(), mRift.getPitch(), mRift.getRoll());
					mEvents.putSensorEvent(YangSensor.TYPE_ROTATION_VECTOR, mTempQuat.mX,mTempQuat.mY,mTempQuat.mZ);
				}
//				if(mSensorActive[YangSensor.TYPE_EULER_ANGLES] && mFrameCount%(SPEED_FASTEST+1-mSpeed[YangSensor.TYPE_EULER_ANGLES])==0)
//					System.out.println(mRift.getYaw());
				if(mSensorActive[YangSensor.TYPE_EULER_ANGLES] && mFrameCount%(SPEED_FASTEST+1-mSpeed[YangSensor.TYPE_EULER_ANGLES])==0)
					mEvents.putSensorEvent(YangSensor.TYPE_EULER_ANGLES, mRift.getYaw(), mRift.getPitch(), mRift.getRoll());

				try {
					Thread.sleep(20);
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
			}
			mThreadActive = false;
		}

	};

	public boolean connected() {
		if(mRift==null) {
			mRift = new OculusRift();
			mRift.init();
			mHMDInfo = mRift.getHMDInfo();;
			mFrameCount = 0;
		}
		return mRift.getHMDInfo().VScreenSize!=0;
	}

	@Override
	protected void derivedStartSensor(int type, int speed) {
		if(mThreadActive)
			return;
		if(type==YangSensor.TYPE_ROTATION_VECTOR || type==YangSensor.TYPE_EULER_ANGLES) {
			if(mRift==null) {
				mRift = new OculusRift();
				mRift.init();
				mHMDInfo = mRift.getHMDInfo();
				mFrameCount = 0;
			}
			mStopThread = false;
			mThreadActive = true;
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
