package yang.events.listeners;

import yang.events.eventtypes.SurfacePointerEvent;


public interface PointerEventListener extends RawEventListener {
	
	public void pointerDown(float x, float y, SurfacePointerEvent event);
	
	public void pointerMoved(float x, float y, SurfacePointerEvent event);
	
	public void pointerDragged(float x, float y, SurfacePointerEvent event);
	
	public void pointerUp(float x, float y, SurfacePointerEvent event);
	
	
}
