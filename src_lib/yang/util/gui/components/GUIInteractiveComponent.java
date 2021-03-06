package yang.util.gui.components;

import yang.events.eventtypes.YangEvent;
import yang.util.YangList;
import yang.util.gui.GUIPointerEvent;
import yang.util.gui.interfaces.GUIActionListener;
import yang.util.gui.interfaces.GUIPointerListener;

public abstract class GUIInteractiveComponent extends GUIMultipassComponent implements GUIPointerListener {

	public float mPressedTime = -1;
	public float mHoverTime = -1;
	public float mLastMovement = -Float.MAX_VALUE;
	public GUIActionListener mActionListener;
	public YangList<GUIPointerListener> mPointerListeners = new YangList<GUIPointerListener>();

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

	public GUIInteractiveComponent setPointerListener(YangList<GUIPointerListener> pointerListeners) {
		for(final GUIPointerListener listener:pointerListeners)
			mPointerListeners.add(listener);
		return this;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public GUIMultipassComponent setPasses(GUIComponentDrawPass... passes) {
		super.setPasses(passes);
		for(final GUIComponentDrawPass pass:passes) {
			if(pass instanceof GUIPointerListener)
				addPointerListener((GUIPointerListener)pass);
		}
		return this;
	}

	public GUIComponent rawPointerEvent(GUIPointerEvent pointerEvent) {
		pointerEvent.handle(this);
		if(mPointerListeners!=null) {
			for(final GUIPointerListener listener:mPointerListeners)
				pointerEvent.handle(listener);
			return this;
		}else
			return null;
	}

	@Override
	public boolean rawEvent(YangEvent event) {
		return false;
	}

	@Override
	public void guiClick(GUIPointerEvent event) {

	}

	@Override
	public void guiFocusedDrag(GUIPointerEvent event) {

	}

	@Override
	public void guiPointerDown(float x, float y, GUIPointerEvent event) {

	}

	@Override
	public void guiPointerMoved(float x, float y, GUIPointerEvent event) {

	}

	@Override
	public void guiPointerDragged(float x, float y, GUIPointerEvent event) {

	}

	@Override
	public void guiPointerUp(float x, float y, GUIPointerEvent event) {

	}

	@Override
	public void guiEnter(GUIComponent sender) {

	}

	@Override
	public void guiExit(GUIComponent sender) {

	}

	@Override
	public boolean isPressable() {
		return false;
	}

}
