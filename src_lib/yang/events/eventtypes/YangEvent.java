package yang.events.eventtypes;

import yang.events.YangEventQueue;
import yang.events.listeners.RawEventListener;

public abstract class YangEvent {
	
	public YangEventQueue mEventQueue;
	
	public void handle(RawEventListener eventInterface) {
		eventInterface.rawEvent(this);
	}

	public void onPut() {
		
	}
	
}
