package yang.events.eventtypes;

import yang.events.listeners.RawEventListener;
import yang.events.listeners.ZoomEventListener;

public class YangZoomEvent extends YangEvent {

	public float mValue;

	@Override
	public boolean handle(RawEventListener listener) {
		if(listener.rawEvent(this))
			return true;
		if(!(listener instanceof ZoomEventListener))
			return false;
		((ZoomEventListener)listener).zoom(mValue);
		return true;
	}
}
