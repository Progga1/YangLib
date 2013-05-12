package yang.events.eventtypes;

import yang.events.listeners.PointerEventListener;
import yang.events.listeners.RawEventListener;

public class YangPointerEvent extends AbstractPointerEvent {
	
	@Override
	public void handle(RawEventListener listener) {
		if(listener.rawEvent(this))
			return;
		if(!(listener instanceof PointerEventListener))
			return;
		PointerEventListener pointerListener = (PointerEventListener)listener;
		
		switch(mAction) {
		case ACTION_POINTERDOWN:
			pointerListener.pointerDown(mX, mY, this);
			break;
		case ACTION_POINTERMOVE:
			pointerListener.pointerMoved(mX, mY, this);
			break;
		case ACTION_POINTERDRAG:
			pointerListener.pointerDragged(mX, mY, this);
			break;
		case ACTION_POINTERUP:
			pointerListener.pointerUp(mX, mY, this);
			break;
		}
	}
	
}
