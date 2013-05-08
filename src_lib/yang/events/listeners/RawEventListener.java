package yang.events.listeners;

import yang.events.eventtypes.YangEvent;

public interface RawEventListener {

	public boolean rawEvent(YangEvent event);
	
}
