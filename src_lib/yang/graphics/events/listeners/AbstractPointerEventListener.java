package yang.graphics.events.listeners;

import yang.graphics.events.eventtypes.PointerEvent;


public interface AbstractPointerEventListener<PointerEventType extends PointerEvent> {
	
	public void pointerDown(float x, float y, PointerEventType event);
	
	public void pointerMoved(float x, float y, PointerEventType event);
	
	public void pointerDragged(float x, float y, PointerEventType event);
	
	public void pointerUp(float x, float y, PointerEventType event);
	
}
