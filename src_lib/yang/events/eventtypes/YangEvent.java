package yang.events.eventtypes;

import yang.events.YangEventQueue;
import yang.events.listeners.RawEventListener;

public abstract class YangEvent {

	public YangEventQueue mEventQueue;

	//handle() potentially called multiple times, poll() only once

	public boolean handle(RawEventListener eventInterface) {
		return eventInterface.rawEvent(this);
	}

	public void onPoll() {

	}

}
