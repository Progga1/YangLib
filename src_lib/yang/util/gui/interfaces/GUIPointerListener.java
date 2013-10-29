package yang.util.gui.interfaces;

import yang.events.listeners.RawEventListener;
import yang.util.gui.GUIPointerEvent;
import yang.util.gui.components.GUIComponent;

public interface GUIPointerListener extends RawEventListener {

	public void guiClick(GUIPointerEvent pointerEvent);

	public void guiFocusedDrag(GUIPointerEvent event);

	public void guiEnter(GUIComponent sender);

	public void guiExit(GUIComponent sender);

	public void guiPointerDown(float x, float y, GUIPointerEvent event);

	public void guiPointerMoved(float x, float y, GUIPointerEvent event);

	public void guiPointerDragged(float x, float y, GUIPointerEvent event);

	public void guiPointerUp(float x, float y, GUIPointerEvent event);

}
