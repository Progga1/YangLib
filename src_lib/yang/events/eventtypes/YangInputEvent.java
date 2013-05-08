package yang.events.eventtypes;

import yang.events.listeners.RawEventListener;

public class YangInputEvent {
	
	public void handle(RawEventListener eventInterface) {
		eventInterface.rawEvent(this);
	}
	
}
