package yang.events.eventtypes;

import yang.events.listeners.ZoomEventListener;

public class AbstractZoomEvent extends YangInputEvent {

	public float mValue;
	
	public void handleZoomEvent(ZoomEventListener eventListener) {
		eventListener.zoom(mValue);
	}
}
