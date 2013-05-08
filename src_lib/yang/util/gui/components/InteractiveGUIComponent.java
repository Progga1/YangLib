package yang.util.gui.components;

import yang.events.eventtypes.YangInputEvent;
import yang.util.gui.GUIPointerEvent;
import yang.util.gui.interfaces.GUIActionListener;
import yang.util.gui.interfaces.GUIPointerListener;

public abstract class InteractiveGUIComponent extends GUIComponent implements GUIPointerListener {

	public GUIActionListener mActionListener;
	public GUIPointerListener mPointerListener;
	public boolean mEnabled = true;
	
	public abstract boolean inArea(float x,float y);
	
	public InteractiveGUIComponent setActionListener(GUIActionListener actionListener) {
		mActionListener = actionListener;
		return this;
	}
	
	public InteractiveGUIComponent setPointerListener(GUIPointerListener pointerListener) {
		mPointerListener = pointerListener;
		return this;
	}
	
	public void rawPointerEvent(GUIPointerEvent pointerEvent) {
		if(mPointerListener!=null) {
			pointerEvent.handle(mPointerListener);
		}
	}
	
	public void rawEvent(YangInputEvent event) {
		
	}

	public void guiClick(GUIPointerEvent event) {
		
	}
	
	public void guiFocusedDrag(GUIPointerEvent event) {
		
	}
	
	public void guiPointerDown(float x, float y, GUIPointerEvent event) {
		
	}
	
	public void guiPointerMoved(float x, float y, GUIPointerEvent event) {
		
	}
	
	public void guiPointerDragged(float x, float y, GUIPointerEvent event) {
		
	}
	
	public void guiPointerUp(float x, float y, GUIPointerEvent event) {
		
	}
	
	public boolean isPressable() {
		return true;
	}
	
}
