package yang.events.eventtypes;

import yang.events.listeners.RawEventListener;

public class YangEvent {
	
	public void handle(RawEventListener eventInterface) {
		eventInterface.rawEvent(this);
	}
	
}
