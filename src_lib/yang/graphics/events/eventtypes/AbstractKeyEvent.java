package yang.graphics.events.eventtypes;

import yang.graphics.events.listeners.KeyEventListener;

public class AbstractKeyEvent extends InputEvent {
	
	public final static int ACTION_KEYDOWN = 0;
	public final static int ACTION_KEYUP = 1;
	
	public int mKey;
	public int mAction;
	
	public void handleKeyEvent(KeyEventListener event) {
		if(mAction==ACTION_KEYDOWN)
			event.keyDown(mKey);
		else
			event.keyUp(mKey);
	}
	
}
