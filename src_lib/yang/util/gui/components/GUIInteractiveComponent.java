package yang.util.gui.components;

import yang.events.eventtypes.YangEvent;
import yang.util.NonConcurrentList;
import yang.util.gui.GUIPointerEvent;
import yang.util.gui.interfaces.GUIActionListener;
import yang.util.gui.interfaces.GUIPointerListener;

public abstract class GUIInteractiveComponent extends GUIMultipassComponent implements GUIPointerListener {

	public float mPressedTime;
	public GUIActionListener mActionListener;
	public NonConcurrentList<GUIPointerListener> mPointerListeners = new NonConcurrentList<GUIPointerListener>();
	
	public abstract boolean inArea(float x,float y);
	
	public GUIInteractiveComponent setActionListener(GUIActionListener actionListener) {
		mActionListener = actionListener;
		return this;
	}
	
	public GUIInteractiveComponent addPointerListener(GUIPointerListener pointerListener) {
		if(!mPointerListeners.contains(pointerListener))
			mPointerListeners.add(pointerListener);
		return this;
	}
	
	public GUIInteractiveComponent setPointerListener(NonConcurrentList<GUIPointerListener> pointerListeners) {
		for(GUIPointerListener listener:pointerListeners)
			mPointerListeners.add(listener);
		return this;		
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public GUIMultipassComponent setPasses(GUIComponentDrawPass... passes) {
		super.setPasses(passes);
		for(GUIComponentDrawPass pass:passes) {
			if(pass instanceof GUIPointerListener)
				addPointerListener((GUIPointerListener)pass);
		}
		return this;
	}
	
	public GUIComponent rawPointerEvent(GUIPointerEvent pointerEvent) {
		pointerEvent.handle(this);
		if(mPointerListeners!=null) {
			for(GUIPointerListener listener:mPointerListeners)
				pointerEvent.handle(listener);
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
