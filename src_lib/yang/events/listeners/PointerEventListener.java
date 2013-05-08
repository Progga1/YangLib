package yang.events.listeners;

import yang.events.eventtypes.PointerEvent;


public interface PointerEventListener extends RawEventListener {
	
	public void pointerDown(float x, float y, PointerEvent event);
	
	public void pointerMoved(float x, float y, PointerEvent event);
	
	public void pointerDragged(float x, float y, PointerEvent event);
	
	public void pointerUp(float x, float y, PointerEvent event);
	
	
}
