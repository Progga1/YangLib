package yang.events.eventtypes;

import yang.events.listeners.KeyEventListener;
import yang.events.listeners.RawEventListener;

public class YangKeyEvent extends YangInputEvent {
	
	public final static int ACTION_KEYDOWN = 0;
	public final static int ACTION_KEYUP = 1;
	
	public int mKey;
	public int mAction;
	
	@Override
	public void handle(RawEventListener listener) {
		listener.rawEvent(this);
		if(!(listener instanceof KeyEventListener))
			return;
		if(mAction==ACTION_KEYDOWN)
			((KeyEventListener)listener).keyDown(mKey);
		else
			((KeyEventListener)listener).keyUp(mKey);
	}
	
}
