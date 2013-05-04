package yang.graphics.defaults;

import yang.graphics.AbstractGFXLoader;
import yang.graphics.SurfaceUpdating;
import yang.graphics.events.EventQueueHolder;
import yang.graphics.events.InputEventQueue;
import yang.graphics.events.eventtypes.InputEvent;
import yang.graphics.events.eventtypes.PointerEvent;
import yang.graphics.events.listeners.FullEventListener;
import yang.graphics.font.DrawableString;
import yang.systemdependent.AbstractResourceManager;

public abstract class DefaultSurface extends SurfaceUpdating implements FullEventListener,EventQueueHolder{

	public AbstractResourceManager mResources;
	public Default2DGraphics mGraphics2D;
	public Default3DGraphics mGraphics3D;
	public AbstractGFXLoader mGFXLoader;
	private boolean mInit2DGraphics;
	private boolean mInit3DGraphics;
	private InputEventQueue mEventQueue;
	
	protected DefaultSurface(boolean init2DGraphics,boolean init3DGraphics) {
		mInit2DGraphics = init2DGraphics;
		mInit3DGraphics = init3DGraphics;
		mEventQueue = new InputEventQueue(64);
	}
	
	public InputEventQueue getEventQueue() {
		return mEventQueue;
	}
	
	@Override
	protected final void initGraphics() {
		if(mInit2DGraphics) {
			mGraphics2D = new Default2DGraphics(mGraphics);
			mGraphics2D.init();
			DrawableString.DEFAULT_GRAPHICS = mGraphics2D;
			if(!mInit3DGraphics) {
				mGraphics2D.activate();
				mGraphics2D.setDefaultProgram();
			}
		}
		if(mInit3DGraphics) {
			mGraphics3D = new Default3DGraphics(mGraphics);
			mGraphics3D.init();
			if(!mInit2DGraphics) {
				DrawableString.DEFAULT_GRAPHICS = mGraphics3D;
				mGraphics3D.activate();
				mGraphics3D.setDefaultProgram();
			}
		}
		mGFXLoader = mGraphics.mGFXLoader;
		mResources = mGraphics.mGFXLoader.mResources;
		mEventQueue.setGraphics(mGraphics);
	}
	
	public void step(float deltaTime) {
		mEventQueue.handleEvents(this);
	}

	public void rawEvent(InputEvent event) {
		
	}
	
	public void pointerDown(float x, float y, PointerEvent event) {
		
	}

	public void pointerDragged(float x, float y, PointerEvent event) {
		
	}
	
	public void pointerMoved(float x, float y, PointerEvent event) {
		
	}

	public void pointerUp(float x, float y, PointerEvent event) {
		
	}
	
	public void keyDown(int code) {
		
	}

	public void keyUp(int code) {
		
	}

	public void zoom(float factor) {
		
	}
	
}
