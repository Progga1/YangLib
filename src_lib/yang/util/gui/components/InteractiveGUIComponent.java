package yang.util.gui.components;

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
			pointerEvent.handlePointerEvent(mPointerListener);
		}
	}

	public void onClick(GUIPointerEvent event) {
		
	}
	
	public void onFocusedDrag(GUIPointerEvent event) {
		
	}
	
	public void pointerDown(float x, float y, GUIPointerEvent event) {
		
	}
	
	public void pointerMoved(float x, float y, GUIPointerEvent event) {
		
	}
	
	public void pointerDragged(float x, float y, GUIPointerEvent event) {
		
	}
	
	public void pointerUp(float x, float y, GUIPointerEvent event) {
		
	}
	
	public boolean isPressable() {
		return true;
	}
	
}
