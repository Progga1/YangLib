package yang.events.eventtypes;

import yang.events.listeners.RawEventListener;

public abstract class YangEvent {
	
	public void handle(RawEventListener eventInterface) {
		eventInterface.rawEvent(this);
	}
	
}
