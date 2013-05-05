package yang.util.gui.interfaces;

import yang.events.listeners.AbstractPointerEventListener;
import yang.util.gui.GUIPointerEvent;

public interface GUIPointerListener extends AbstractPointerEventListener<GUIPointerEvent>{

	public void onClick(GUIPointerEvent pointerEvent);
	
	public void onFocusedDrag(GUIPointerEvent event);
	
}
