package yang.events.eventtypes;

import yang.events.listeners.PointerEventListener;

public class PointerEvent extends AbstractPointerEvent {
	
	public void handlePointerEvent(PointerEventListener eventInterface) {
		switch(mAction) {
		case ACTION_POINTERDOWN:
			eventInterface.pointerDown(mX, mY, this);
			break;
		case ACTION_POINTERMOVE:
			eventInterface.pointerMoved(mX, mY, this);
			break;
		case ACTION_POINTERDRAG:
			eventInterface.pointerDragged(mX, mY, this);
			break;
		case ACTION_POINTERUP:
			eventInterface.pointerUp(mX, mY, this);
			break;
		}
	}
	
}
