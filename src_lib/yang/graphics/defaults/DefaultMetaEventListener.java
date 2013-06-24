package yang.graphics.defaults;


import yang.events.Keys;
import yang.events.eventtypes.YangEvent;
import yang.events.eventtypes.YangPointerEvent;
import yang.events.listeners.YangEventListener;
import yang.graphics.YangSurface;
import yang.model.DebugYang;

public class DefaultMetaEventListener implements YangEventListener {

	public YangSurface mSurface;
	
	public DefaultMetaEventListener(YangSurface surface) {
		mSurface = surface;
	}
	
	@Override
	public boolean rawEvent(YangEvent event) {
		return false;
	}

	@Override
	public void pointerDown(float x, float y, YangPointerEvent event) {
		
	}

	@Override
	public void pointerMoved(float x, float y, YangPointerEvent event) {
		
	}

	@Override
	public void pointerDragged(float x, float y, YangPointerEvent event) {
		
	}

	@Override
	public void pointerUp(float x, float y, YangPointerEvent event) {
		
	}

	@Override
	public void keyDown(int code) {
		
	}

	@Override
	public void keyUp(int code) {
		if(code==Keys.F2)
			DebugYang.DRAW_GFX_VALUES ^= true;
	}

	@Override
	public void zoom(float factor) {
		
	}

}
