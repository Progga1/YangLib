package yang.util.gui.interfaces;

import yang.events.listeners.RawEventListener;
import yang.util.gui.GUIPointerEvent;

public interface GUIPointerListener extends RawEventListener {

	public void guiClick(GUIPointerEvent pointerEvent);
	
	public void guiFocusedDrag(GUIPointerEvent event);
	
	public void guiPointerDown(float x, float y, GUIPointerEvent event);
	
	public void guiPointerMoved(float x, float y, GUIPointerEvent event);
	
	public void guiPointerDragged(float x, float y, GUIPointerEvent event);
	
	public void guiPointerUp(float x, float y, GUIPointerEvent event);
	
}
