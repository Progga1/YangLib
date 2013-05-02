package yang.graphics.events.eventtypes;

import yang.graphics.events.listeners.ZoomEventListener;

public class AbstractZoomEvent extends InputEvent {

	public float mValue;
	
	public void handleZoomEvent(ZoomEventListener eventListener) {
		eventListener.zoom(mValue);
	}
}
