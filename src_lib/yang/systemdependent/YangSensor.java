package yang.systemdependent;

import yang.events.YangEventQueue;
import yang.surface.YangSurface;

public abstract class YangSensor {

	public final static int SENSOR_COUNT = 6;

	public static final int SPEED_SLOW = 0;
	public static final int SPEED_NORMAL = 1;
	public static final int SPEED_GAME = 2;
	public static final int SPEED_FASTEST = 3;

	public final static int TYPE_ACCELEROMETER = 0;
	public final static int TYPE_GRAVITY = 1;
	public final static int TYPE_GYROSCOPE = 2;
	public final static int TYPE_LINEAR_ACCELERATION = 3;
	public final static int TYPE_ROTATION_VECTOR = 4;
	public static final int TYPE_EULER_ANGLES = 5;

	protected int mActiveSensorsCount = 0;
	protected boolean[] mSensorActive = new boolean[SENSOR_COUNT];
	protected int[] mSpeed = new int[SENSOR_COUNT];
	protected YangSurface mSurface;
	protected YangEventQueue mEvents;
	private boolean mPaused = false;

	protected abstract void derivedStartSensor(int type,int speed);
	protected abstract void derivedStopSensor(int type);
	protected void derivedPause() { };
	protected void derivedResume() { };

	public void startSensor(int type,int speed) {
		if(!mSensorActive[type]) {
			mActiveSensorsCount++;
			mSpeed[type] = speed;
		}else
			throw new RuntimeException("Sensor "+type+" already active");
		mSensorActive[type] = true;
		if(type<0 || type>5)
			throw new RuntimeException("Unknown type: "+type);
		if(speed<0 || speed>3)
			throw new RuntimeException("Unknown speed: "+speed);
		derivedStartSensor(type,speed);
	}

	public void startSensor(int type) {
		startSensor(type,SPEED_GAME);
	}

	public void stopSensor(int type) {
		if(mSensorActive[type])
			mActiveSensorsCount--;
		derivedStopSensor(type);
	}

	public void stopAllSensors() {
		for(int type=0;type<5;type++) {
			stopSensor(type);
		}
	}

	public boolean isAnySensorActive() {
		return mActiveSensorsCount>0;
	}

	public boolean isSensorActive(int type) {
		return mSensorActive[type];
	}

	public void init(YangSurface surface) {
		if(mSurface!=null)
			throw new RuntimeException("Double init");
		mSurface = surface;
		mEvents = surface.mEventQueue;
	}

	public void pause() {
		mPaused = true;
		if(!isAnySensorActive())
			return;
		derivedPause();
		for(int i=0;i<SENSOR_COUNT;i++) {
			if(mSensorActive[i])
				derivedStopSensor(i);
		}

	}

	public void resume() {
		mPaused = false;
		if(!isAnySensorActive())
			return;
		derivedResume();
		for(int type=0;type<SENSOR_COUNT;type++) {
			if(mSensorActive[type])
				derivedStartSensor(type,mSpeed[type]);
		}
	}

	public boolean isPaused() {
		return mPaused;
	}

}
