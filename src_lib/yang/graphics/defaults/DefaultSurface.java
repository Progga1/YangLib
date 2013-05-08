package yang.graphics.defaults;

import yang.events.EventQueueHolder;
import yang.events.InputEventQueue;
import yang.events.eventtypes.YangPointerEvent;
import yang.events.eventtypes.YangEvent;
import yang.events.listeners.InputEventListener;
import yang.graphics.AbstractGFXLoader;
import yang.graphics.SurfaceInterface;
import yang.graphics.font.DrawableString;
import yang.systemdependent.AbstractResourceManager;
import yang.util.StringsXML;

public abstract class DefaultSurface extends SurfaceInterface implements InputEventListener,EventQueueHolder{

	public AbstractResourceManager mResources;
	public StringsXML mStrings;
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
		if(mResources.fileExists("strings/strings.xml"))
			mStrings = new StringsXML(mResources.getInputStream("strings/strings.xml"));
		mEventQueue.setGraphics(mGraphics);
	}
	
	public void step(float deltaTime) {
		mEventQueue.handleEvents(this);
	}

	public boolean rawEvent(YangEvent event) {
		return false;
	}
	
	public void pointerDown(float x, float y, YangPointerEvent event) {

	}

	public void pointerDragged(float x, float y, YangPointerEvent event) {
		
	}
	
	public void pointerMoved(float x, float y, YangPointerEvent event) {
		
	}

	public void pointerUp(float x, float y, YangPointerEvent event) {
		
	}
	
	public void keyDown(int code) {
		
	}

	public void keyUp(int code) {
		
	}

	public void zoom(float factor) {
		
	}
	
}
