package yang.graphics;

import yang.graphics.interfaces.InitializationCallback;
import yang.graphics.translator.GraphicsTranslator;
import yang.model.DebugYang;
import yang.util.StringsXML;

public abstract class YangSurface {
	
	public GraphicsTranslator mGraphics;

	protected boolean mAutoReloadTexturesOnResume = true;
	protected boolean mInitialized;
	protected Object mInitializedNotifier;
	protected InitializationCallback mInitCallback;
	public StringsXML mStrings;
	
	protected double mProgramStartTime;
	protected long mProgramTime;
	protected float mDeltaTimeSeconds;
	protected long mDeltaTimeNanos;
	protected int mRuntimeState = 0;
	private float mLoadingProgress = -1;
	
	/**
	 * GL-Thread
	 */
	protected abstract void initGraphics();
	/**
	 * GL-Thread
	 */
	protected void resumedFromStop(){};
	/**
	 * GL-Thread
	 */
	protected void resumedFromPause(){};
	protected abstract void draw();
	
	//Optional methods
	protected void postInitGraphics() { }
	protected void initGraphicsForResume() { }
	
	protected void drawResume() {
		mGraphics.clear(0, 0, 0.1f);
	}
	
	public YangSurface() {
		mInitializedNotifier = new Object();
		mProgramTime = 0;
		setUpdatesPerSecond(60);
	}
	
	public final void drawFrame() {
		
		if(mRuntimeState>0 && mLoadingProgress<0) {
			mGraphics.restart();
			mLoadingProgress = 0;
			initGraphicsForResume();
			
			if(mAutoReloadTexturesOnResume) {
				mGraphics.beginFrame();
				drawResume();
				mGraphics.endFrame();
				return;
			}
		}
		
		if(mRuntimeState>0) {
			if(mAutoReloadTexturesOnResume) {
				mGraphics.mGFXLoader.reloadTextures();
			}
			mGraphics.unbindTextures();
			
			if(mRuntimeState==1)
				resumedFromPause();
			if(mRuntimeState==2)
				resumedFromStop();
			mRuntimeState = 0;
		}
		
		mGraphics.beginFrame();
		draw();
		mGraphics.endFrame();
		
	}
	
	public void setGraphics(GraphicsTranslator graphics) {
		mGraphics = graphics;
	}
	
	private boolean assertMessage() {
		System.out.println("ASSERTIONS ARE ACTIVATED");
		return true;
	}
	
	public void onSurfaceCreated() {
		if(mInitialized) {
			DebugYang.println("ALREADY INITIALIZED");
			return;
		}
		assert assertMessage();
		mGraphics.init();
		initGraphics();
		postInitGraphics();
		if(mInitCallback!=null)
			mInitCallback.initializationFinished();
		
		mInitialized = true;
		synchronized(mInitializedNotifier) {
			mInitializedNotifier.notifyAll();
		}
	}
	
	public void waitUntilInitialized() {
		synchronized(mInitializedNotifier) {
			try {
				mInitializedNotifier.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void onSurfaceChanged(int width,int height) {
		mGraphics.setSurfaceSize(width, height);
		
	}
	
	public boolean isInitialized() {
		return mInitialized;
	}
	
	public void setInitializationCallback(InitializationCallback initCallback) {
		mInitCallback = initCallback;
	}
	
	public void setUpdatesPerSecond(int updatesPerSecond) {
		mDeltaTimeSeconds = 1f/updatesPerSecond;
		mDeltaTimeNanos = 1000000000/updatesPerSecond;
	}
	
	public void step(float deltaTime) {
		
	}
	
	protected boolean update() {
		if(!mInitialized)
			return true;
		if(mProgramTime==0)
			mProgramTime = System.nanoTime()-1;
		boolean result = mProgramTime<System.nanoTime();
		while(mProgramTime<System.nanoTime()) {
			mProgramTime += mDeltaTimeNanos;
			step(mDeltaTimeSeconds);
		}
		return result;
	}
	
	public void stop() {
		mRuntimeState = 2;
		mLoadingProgress = -1;
	}
	
	/**
	 * Non-GL-Thread!
	 */
	public void pause() {
		mProgramTime = 0;
		mRuntimeState = 1;
		mLoadingProgress = -1;
	}
	
	/**
	 * Non-GL-Thread!
	 */
	public void destroy() {
		mGraphics.mGFXLoader.deleteTextures();
	}
	
	/**
	 * Non-GL-Thread!
	 */
	public void resume() {
		
	}
	
	public void exit() {
		System.exit(0);
	}
	
}
