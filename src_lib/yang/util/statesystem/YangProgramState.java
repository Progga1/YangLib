package yang.util.statesystem;

import yang.events.eventtypes.SurfacePointerEvent;
import yang.events.eventtypes.YangEvent;
import yang.events.eventtypes.YangSensorEvent;
import yang.events.listeners.YangEventListener;
import yang.graphics.defaults.Default2DGraphics;
import yang.graphics.defaults.Default3DGraphics;
import yang.graphics.translator.AbstractGFXLoader;
import yang.graphics.translator.GraphicsTranslator;
import yang.sound.AbstractSoundManager;
import yang.surface.StringsXML;
import yang.systemdependent.AbstractResourceManager;

public abstract class YangProgramState<StateSystemType extends YangProgramStateSystem> implements YangEventListener {

	public static final float PI = 3.1415926535f;

	private boolean mInitialized = false;
	protected boolean mFirstFrame = true;
	protected int mRecentSurfaceWidth = 0,mRecentSurfaceHeight = 0;
	protected StateSystemType mStateSystem;

	public double mStateTimer = 0;
	protected GraphicsTranslator mGraphics;
	protected Default2DGraphics mGraphics2D;
	protected Default3DGraphics mGraphics3D;
	protected AbstractSoundManager mSounds;
	protected AbstractGFXLoader mGFXLoader;
	protected AbstractResourceManager mResources;
	public StringsXML mStrings;
	private int mRestartCount = 0;

	protected abstract void step(float deltaTime);
	protected void preDraw() { }
	protected abstract void draw();

	protected void surfaceSizeChanged(int newWidth,int newHeight) { }
	protected void restartGraphics() { }
	protected void pause() { }
	protected void resume() { }

	public final YangProgramState<StateSystemType> init(StateSystemType stateSystem) {
		mStateSystem = stateSystem;
		mGraphics = stateSystem.mGraphics2D.mTranslator;
		mGraphics2D = stateSystem.mGraphics2D;
		mGraphics3D = stateSystem.mGraphics3D;
		mGFXLoader = stateSystem.mGFXLoader;
		mResources = stateSystem.mResources;
		mSounds = stateSystem.mSounds;
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
		if(mFirstFrame)
			return;
		step(deltaTime);
		mStateTimer += deltaTime;
	}

	public final void preDrawFrame() {
		assert mGraphics.preCheck("draw state frame");
		if(mFirstFrame) {
			initGraphics();
			mRestartCount = mGraphics.mRestartCount;
			start();
		}
		if(mRestartCount<mGraphics.mRestartCount) {
			restartGraphics();
			mRestartCount = mGraphics.mRestartCount;
		}
		final int surfWidth = mGraphics.mCurrentSurface.getSurfaceWidth();
		final int surfHeight = mGraphics.mCurrentSurface.getSurfaceHeight();
		if(surfWidth!=mRecentSurfaceWidth || surfHeight!=mRecentSurfaceHeight) {
			surfaceSizeChanged(surfWidth,surfHeight);
			mRecentSurfaceWidth = surfWidth;
			mRecentSurfaceHeight = surfHeight;
		}
		preDraw();
	}

	public void drawFrame() {
		draw();
		mFirstFrame = false;
	}

	public void start() {
		mStateTimer = 0;
	}

	public void stop() {

	}

	protected void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean rawEvent(YangEvent event) {
		return false;
	}

	@Override
	public void pointerDown(float x, float y, SurfacePointerEvent event) {

	}

	@Override
	public void pointerMoved(float x, float y, SurfacePointerEvent event) {

	}

	@Override
	public void pointerDragged(float x, float y, SurfacePointerEvent event) {

	}

	@Override
	public void pointerUp(float x, float y, SurfacePointerEvent event) {

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

	@Override
	public void sensorChanged(YangSensorEvent event) {

	}

}
