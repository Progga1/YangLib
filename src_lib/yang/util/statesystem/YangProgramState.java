package yang.util.statesystem;

import yang.events.eventtypes.PointerEvent;
import yang.events.eventtypes.YangInputEvent;
import yang.events.listeners.FullEventListener;
import yang.graphics.defaults.Default2DGraphics;
import yang.graphics.defaults.Default3DGraphics;
import yang.graphics.translator.GraphicsTranslator;
import yang.systemdependent.AbstractResourceManager;
import yang.util.StringsXML;

public abstract class YangProgramState<StateSystemType extends YangProgramStateSystem> implements FullEventListener {

	private boolean mInitialized = false;
	protected boolean mFirstFrame = true;
	protected StateSystemType mStateSystem;
	protected float mStateTimer = 0;
	protected GraphicsTranslator mGraphics;
	protected Default2DGraphics mGraphics2D;
	protected Default3DGraphics mGraphics3D;
	protected AbstractResourceManager mResources;
	protected StringsXML mStrings;
	
	protected abstract void step(float deltaTime);
	protected abstract void draw();
	
	public final YangProgramState<StateSystemType> init(StateSystemType stateSystem) {
		mStateSystem = stateSystem;
		mGraphics = stateSystem.mGraphics2D.mTranslator;
		mGraphics2D = stateSystem.mGraphics2D;
		mGraphics3D = stateSystem.mGraphics3D;
		mResources = stateSystem.mResources;
		mStrings = stateSystem.mStrings;
		postInit();
		mInitialized = true;
		return this;
	}
	
	public boolean isInitialized() {
		return mInitialized;
	}
	
	protected void postInit() {
		
	}
	
	protected void initGraphics() {
		
	}
	
	public void proceed(float deltaTime) {
		step(deltaTime);
		mStateTimer += deltaTime;
	}
	
	public void drawFrame() {
		if(mFirstFrame) {
			initGraphics();
		}
		draw();
		mFirstFrame = false;
	}
	
	public void start() {
		mStateTimer = 0;
	}
	
	public void stop() {
		
	}
	
	@Override
	public void rawEvent(YangInputEvent event) {
		
	}
	
	@Override
	public void pointerDown(float x, float y, PointerEvent event) {

	}

	@Override
	public void pointerMoved(float x, float y, PointerEvent event) {
		
	}

	@Override
	public void pointerDragged(float x, float y, PointerEvent event) {
		
	}

	@Override
	public void pointerUp(float x, float y, PointerEvent event) {
		
	}

	@Override
	public void keyDown(int code) {
		
	}

	@Override
	public void keyUp(int code) {
		
	}

	@Override
	public void zoom(float factor) {
		
	}
	
}
