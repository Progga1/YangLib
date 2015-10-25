package yang.util.statesystem;

import yang.events.eventtypes.SurfacePointerEvent;
import yang.events.eventtypes.YangEvent;
import yang.events.eventtypes.YangSensorEvent;
import yang.events.listeners.YangEventListener;
import yang.graphics.defaults.Default2DGraphics;
import yang.graphics.defaults.Default3DGraphics;
import yang.graphics.interfaces.ClockInterface;
import yang.graphics.translator.AbstractGFXLoader;
import yang.graphics.translator.GraphicsTranslator;
import yang.graphics.translator.Texture;
import yang.sound.AbstractSoundManager;
import yang.surface.StringsXML;
import yang.surface.YangSurface;
import yang.systemdependent.AbstractResourceManager;

public abstract class YangProgramState<StateSystemType extends YangProgramStateSystem> implements YangEventListener,ClockInterface {

	public static final float PI = 3.1415926535f;

	private boolean mInitialized = false;
	protected boolean mFirstFrame = true;
	protected int mRecentSurfaceWidth = 0,mRecentSurfaceHeight = 0;
	protected StateSystemType mStateSystem;
	private StateSystemInterface mParentStateSystem;
	private int mStateSystemLayer;
	public boolean mBlockEvents = false;
	public boolean mBlockSteps = false;

	public double mStateTimer = 0;
	public GraphicsTranslator mGraphics;
	protected Default2DGraphics mGraphics2D;
	protected Default3DGraphics mGraphics3D;
	protected AbstractSoundManager mSounds;
	protected AbstractGFXLoader mGFXLoader;
	protected AbstractResourceManager mResources;
	public StringsXML mStrings;
	private int mRestartCount = 0;

	public boolean mBlocked = false;
	public float mFadeProgress = 1;

	protected abstract void step(float deltaTime);
	protected void preDraw() { }
	protected abstract void draw();

	protected void surfaceSizeChanged(int newWidth,int newHeight) { }
	protected void restartGraphics() { }
	protected void pause() { }
	protected void resume() { }

	public final YangProgramState<StateSystemType> init(StateSystemType stateSystem) {
		mStateSystem = stateSystem;
		mGraphics2D = stateSystem.mGraphics2D;
		mGraphics3D = stateSystem.mGraphics3D;
		if(mGraphics2D!=null)
			mGraphics = stateSystem.mGraphics2D.mTranslator;
		else
			mGraphics = stateSystem.mGraphics3D.mTranslator;
		mGFXLoader = stateSystem.mGFXLoader;
		mResources = stateSystem.mResources;
		mSounds = stateSystem.mSounds;
		mStrings = stateSystem.mStrings;
		postInit();
		mInitialized = true;
		return this;
	}

	protected void setDisplayDebugTexture(Texture texture,boolean flipY) {
		mStateSystem.mGFXDebug.mDisplayTexture = texture;
		mStateSystem.mGFXDebug.mDisplayTextureFlipY = flipY;
	}

	protected void setDisplayDebugTexture(Texture texture) {
		setDisplayDebugTexture(texture,false);
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
			step(YangSurface.deltaTimeSeconds);
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

	protected void start() {
		mStateTimer = 0;
	}

	public void onSet(StateSystemInterface stateSystem,int layer) {
		mParentStateSystem = stateSystem;
		mStateSystemLayer = layer;
		if(!mFirstFrame)
			start();
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

	public void onBlock() {

	}

	public void onUnblock() {

	}

	public void onFadeOut() {

	}

	public void onFadeInFinished() {

	}

	public int getStateSystemLayer() {
		return mStateSystemLayer;
	}

	public StateSystemInterface getParentStateSystem() {
		return mParentStateSystem;
	}

	public void activate() {
		mParentStateSystem.setState(this);
	}

	@Override
	public double getTime() {
		return mStateTimer;
	}

}
