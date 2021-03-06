package yang.events.eventtypes;

import yang.events.listeners.RawEventListener;
import yang.events.listeners.YangSensorListener;
import yang.systemdependent.YangSensor;

public class YangSensorEvent extends YangEvent {

	public float mX,mY,mZ,mW = 1;
	public int mType;
	public float[] mMatrix;

	@Override
	public boolean handle(RawEventListener listener) {
		if(listener.rawEvent(this))
			return true;
		if(listener instanceof YangSensorListener) {
			((YangSensorListener)listener).sensorChanged(this);
		}
		return true;
	}


	public String typeToString() {
		switch(mType) {
		case YangSensor.TYPE_ACCELEROMETER: return "Accelerometer";
		case YangSensor.TYPE_GRAVITY: return "Gravity";
		case YangSensor.TYPE_GYROSCOPE: return "Gyroscope";
		case YangSensor.TYPE_LINEAR_ACCELERATION: return "Linear Acceleration";
		case YangSensor.TYPE_ROTATION_VECTOR: return "Rotation vector";
		case YangSensor.TYPE_EULER_ANGLES: return "Euler angles";
		default: return "<undefined>";
		}
	}

	@Override
	public String toString() {
		return typeToString()+": "+mX+","+mY+","+mZ+(mW!=1?","+mW:"");
	}

}
