package yang.graphics.events.eventtypes;

import yang.graphics.events.listeners.FullEventListener;

public class InputEvent {

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
