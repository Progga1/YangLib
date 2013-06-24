package yang.graphics.defaults;

import yang.events.EventQueueHolder;
import yang.events.YangEventQueue;
import yang.events.Keys;
import yang.events.eventtypes.YangEvent;
import yang.events.eventtypes.YangPointerEvent;
import yang.events.listeners.YangEventListener;
import yang.graphics.YangSurface;
import yang.graphics.font.BitmapFont;
import yang.graphics.font.StringProperties;
import yang.graphics.model.GFXDebug;
import yang.graphics.translator.AbstractGFXLoader;
import yang.model.DebugYang;
import yang.systemdependent.AbstractResourceManager;
import yang.util.StringsXML;

public abstract class DefaultSurface extends YangSurface implements YangEventListener,EventQueueHolder{

	public AbstractResourceManager mResources;
	public StringsXML mStrings;
	public Default2DGraphics mGraphics2D;
	public Default3DGraphics mGraphics3D;
	public AbstractGFXLoader mGFXLoader;
	private boolean mInit2DGraphics;
	private boolean mInit3DGraphics;
	
	protected DefaultSurface(boolean init2DGraphics,boolean init3DGraphics) {
		super();
		mInit2DGraphics = init2DGraphics;
		mInit3DGraphics = init3DGraphics;
		mEventListener = this;
		mMetaEventListener = new DefaultMetaEventListener(this);
	}
	
	@Override
	protected final void initGraphics() {
		if(mInit2DGraphics) {
			mGraphics2D = new Default2DGraphics(mGraphics);
			mGraphics2D.init();
			if(!mInit3DGraphics) {
				mGraphics2D.activate();
				mGraphics2D.setDefaultProgram();
			}
		}
		if(mInit3DGraphics) {
			mGraphics3D = new Default3DGraphics(mGraphics);
			mGraphics3D.init();
			if(!mInit2DGraphics) {
				mGraphics3D.activate();
				mGraphics3D.setDefaultProgram();
			}
		}
		mGFXLoader = mGraphics.mGFXLoader;
		mResources = mGraphics.mGFXLoader.mResources;
		if(mResources.fileExists("strings/strings.xml"))
			mStrings = new StringsXML(mResources.getInputStream("strings/strings.xml"));
	}
	
	protected void initDebugOutput(BitmapFont font) {
		initDebugOutput(mGraphics2D,font);
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
