package yang.events.listeners;

import yang.events.eventtypes.YangSensorEvent;

public interface YangSensorListener {

	public void sensorChanged(YangSensorEvent event);
	
}
