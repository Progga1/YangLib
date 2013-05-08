package yang.events.eventtypes;

import yang.events.listeners.RawEventListener;
import yang.events.listeners.ZoomEventListener;

public class YangZoomEvent extends YangInputEvent {

	public float mValue;
	
	public void handleZoomEvent(RawEventListener listener) {
		listener.rawEvent(this);
		if(!(listener instanceof ZoomEventListener))
			return;
		((ZoomEventListener)listener).zoom(mValue);
	}
}
