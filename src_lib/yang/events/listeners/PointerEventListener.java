package yang.events.listeners;

import yang.events.eventtypes.YangPointerEvent;


public interface PointerEventListener extends RawEventListener {
	
	public void pointerDown(float x, float y, YangPointerEvent event);
	
	public void pointerMoved(float x, float y, YangPointerEvent event);
	
	public void pointerDragged(float x, float y, YangPointerEvent event);
	
	public void pointerUp(float x, float y, YangPointerEvent event);
	
	
}
