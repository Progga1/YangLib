package yang.util.gui.components;

import yang.events.eventtypes.YangEvent;
import yang.util.gui.GUIPointerEvent;
import yang.util.gui.interfaces.GUIActionListener;
import yang.util.gui.interfaces.GUIPointerListener;

public abstract class GUIInteractiveComponent extends GUIMultipassComponent implements GUIPointerListener {

	public float mPressedTime;
	public GUIActionListener mActionListener;
	public GUIPointerListener mPointerListener;
	
	public abstract boolean inArea(float x,float y);
	
	public GUIInteractiveComponent setActionListener(GUIActionListener actionListener) {
		mActionListener = actionListener;
		return this;
	}
	
	public GUIInteractiveComponent setPointerListener(GUIPointerListener pointerListener) {
		mPointerListener = pointerListener;
		return this;
	}
	
	public GUIComponent rawPointerEvent(GUIPointerEvent pointerEvent) {
		pointerEvent.handle(this);
		if(mPointerListener!=null) {
			pointerEvent.handle(mPointerListener);
			return this;
		}else
			return null;
	}
	
	public boolean rawEvent(YangEvent event) {
		return false;
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
		return false;
	}
	
}
