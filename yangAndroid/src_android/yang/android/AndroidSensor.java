package yang.android;

import yang.android.graphics.YangActivity;
import yang.events.eventtypes.YangSensorEvent;
import yang.systemdependent.YangSensor;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class AndroidSensor extends YangSensor implements SensorEventListener{

	private SensorManager mSensorManager;
	private Sensor[] mSensors;
	private YangActivity mActivity;
	private long mTimeStamp;

	public AndroidSensor(YangActivity activity) {
		mActivity = activity;
	}
	
	private void registerListener(Sensor sensor,int speed) {
		int andSpeed = 0;
		switch(speed) {
		case SPEED_SLOW: andSpeed=SensorManager.SENSOR_DELAY_UI;break;
		case SPEED_NORMAL: andSpeed=SensorManager.SENSOR_DELAY_NORMAL;break;
		case SPEED_GAME: andSpeed=SensorManager.SENSOR_DELAY_GAME;break;
		case SPEED_FASTEST: andSpeed=SensorManager.SENSOR_DELAY_FASTEST;break;
		}
		mSensorManager.registerListener(this,sensor, andSpeed);
	}
	
	@Override
	protected void derivedStartSensor(int type,int speed) {
		if(type<0 || type>4)
			throw new RuntimeException("Unknown type: "+type);
		if(speed<0 || speed>3)
			throw new RuntimeException("Unknown speed: "+speed);
		if(mSensorManager==null) {
			mSensorManager = (SensorManager)mActivity.getSystemService(Context.SENSOR_SERVICE);
			mSensors = new Sensor[SENSOR_COUNT];
		}
		int andType = 0;
		switch(type) {
		case YangSensor.TYPE_ACCELEROMETER: andType = Sensor.TYPE_ACCELEROMETER;break;
		case YangSensor.TYPE_GRAVITY: andType = Sensor.TYPE_GRAVITY;break;
		case YangSensor.TYPE_GYROSCOPE: andType = Sensor.TYPE_GYROSCOPE;break;
		case YangSensor.TYPE_LINEAR_ACCELERATION: andType = Sensor.TYPE_LINEAR_ACCELERATION;break;
		case YangSensor.TYPE_ROTATION_VECTOR: andType = Sensor.TYPE_ROTATION_VECTOR;break;
		}
		if(mSensors[type]==null)
			mSensors[type] = mSensorManager.getDefaultSensor(andType);
		registerListener(mSensors[type],speed);
	}

	@Override
	protected void derivedStopSensor(int type) {
		mSensorManager.unregisterListener(this, mSensors[type]);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		
		int type = -1;
		switch(event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER: type = YangSensor.TYPE_ACCELEROMETER;break;
		case Sensor.TYPE_LINEAR_ACCELERATION: type = YangSensor.TYPE_LINEAR_ACCELERATION;break;
		case Sensor.TYPE_GRAVITY: type = YangSensor.TYPE_GRAVITY;break;
		case Sensor.TYPE_GYROSCOPE: 
			type = YangSensor.TYPE_GYROSCOPE;
			long time = System.nanoTime();
			float deltaTime = (float)((System.nanoTime()-mTimeStamp)*0.000000001);
			mTimeStamp = time;

			mEvents.putSensorEvent(type, event.values[0]*deltaTime,event.values[1]*deltaTime,event.values[2]*deltaTime);

			return;
		case Sensor.TYPE_ROTATION_VECTOR: type = YangSensor.TYPE_ROTATION_VECTOR;break;
		}
		if(type<0)
			return;

		mEvents.putSensorEvent(type, event.values);
	}

}
