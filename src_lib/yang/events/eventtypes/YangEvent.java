package yang.events.eventtypes;

import yang.events.YangEventQueue;
import yang.events.listeners.RawEventListener;

public abstract class YangEvent {

	public YangEventQueue mEventQueue;

	//handle potentially called multiple times, poll only once

	public void handle(RawEventListener eventInterface) {
		eventInterface.rawEvent(this);
	}

	public void onPoll() {

	}

}
