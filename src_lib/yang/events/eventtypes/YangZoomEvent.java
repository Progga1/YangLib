package yang.events.eventtypes;

import yang.events.listeners.RawEventListener;
import yang.events.listeners.ZoomEventListener;

public class YangZoomEvent extends YangEvent {

	public float mValue;
	
	@Override
	public void handle(RawEventListener listener) {
		super.handle(listener);
		if(listener.rawEvent(this))
			return;
		if(!(listener instanceof ZoomEventListener))
			return;
		((ZoomEventListener)listener).zoom(mValue);
	}
}
