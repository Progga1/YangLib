package yang.events.eventtypes;

import yang.events.listeners.FullEventListener;

public class YangInputEvent {

	public final void handle(FullEventListener eventInterface) {
		eventInterface.rawEvent(this);
		if(this instanceof PointerEvent)
			((PointerEvent)this).handlePointerEvent(eventInterface);
		if(this instanceof AbstractKeyEvent)
			((AbstractKeyEvent)this).handleKeyEvent(eventInterface);
		if(this instanceof AbstractZoomEvent)
			((AbstractZoomEvent)this).handleZoomEvent(eventInterface);
	}
	
}
