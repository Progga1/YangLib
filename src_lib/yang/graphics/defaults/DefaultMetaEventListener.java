package yang.graphics.defaults;


import yang.events.Keys;
import yang.events.YangEventQueue;
import yang.events.eventtypes.YangEvent;
import yang.events.eventtypes.YangPointerEvent;
import yang.events.listeners.YangEventListener;
import yang.graphics.YangSurface;
import yang.model.DebugYang;

public class DefaultMetaEventListener implements YangEventListener {

	public YangSurface mSurface;
	
	public DefaultMetaEventListener(YangSurface surface) {
		mSurface = surface;
		initMetaKeys();
	}
	
	protected void initMetaKeys() {
		YangEventQueue eventQueue = mSurface.mEventQueue;
		eventQueue.setMetaKeys(Keys.F1,12);
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
		if(code==Keys.F2)
			DebugYang.DRAW_GFX_VALUES ^= true;
		if(code==Keys.F3)
			mSurface.setPaused(!mSurface.isPaused());
		if(code==Keys.F4) {
			if(mSurface.mPlaySpeed<32)
				mSurface.mPlaySpeed *= 2;
			else{
				mSurface.mPlaySpeed = 0;
			}
		}
		if(code==Keys.F5) {
			mSurface.setPaused(false);
			mSurface.mPlaySpeed = 1;
		}
		if(code==Keys.F6) {
			if(mSurface.mPlaySpeed>0.125f*0.25f)
				mSurface.mPlaySpeed *= 0.5f;
			else if(mSurface.mPlaySpeed==0) {
				mSurface.mPlaySpeed = 32;
			}
		}
		if(code==Keys.F7) {
			mSurface.proceed();
		}
	}

	@Override
	public void keyUp(int code) {
		
	}

	@Override
	public void zoom(float factor) {
		
	}

}