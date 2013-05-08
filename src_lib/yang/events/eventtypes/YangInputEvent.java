package yang.events.eventtypes;

import yang.events.listeners.KeyEventListener;
import yang.events.listeners.PointerEventListener;
import yang.events.listeners.RawEventListener;
import yang.events.listeners.ZoomEventListener;

public class YangInputEvent {

	public final void handle(RawEventListener eventInterface) {
		eventInterface.rawEvent(this);
		if(this instanceof PointerEvent && eventInterface instanceof PointerEventListener)
			((PointerEvent)this).handlePointerEvent((PointerEventListener)eventInterface);
		if(this instanceof AbstractKeyEvent && eventInterface instanceof KeyEventListener)
			((AbstractKeyEvent)this).handleKeyEvent((KeyEventListener)eventInterface);
		if(this instanceof AbstractZoomEvent && eventInterface instanceof ZoomEventListener)
			((AbstractZoomEvent)this).handleZoomEvent((ZoomEventListener)eventInterface);
	}
	
}
