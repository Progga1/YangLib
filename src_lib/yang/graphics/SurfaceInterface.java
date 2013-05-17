package yang.graphics;

import yang.graphics.interfaces.InitializationCallback;
import yang.graphics.translator.GraphicsTranslator;
import yang.util.StringsXML;

public abstract class SurfaceInterface {
	
	public GraphicsTranslator mGraphics;
	protected boolean mResuming;

	protected boolean mAutoReloadOnResume = true;
	protected boolean mInitialized;
	protected Object mInitializedNotifier;
	protected InitializationCallback mInitCallback;
	public StringsXML mStrings;
	
	protected double mProgramStartTime;
	protected long mProgramTime;
	protected float mDeltaTimeSeconds;
	protected long mDeltaTimeNanos;
	
	protected abstract void initGraphics();
	protected void resumingFinished(){};
	protected abstract void draw();
	
	protected void postInitGraphics() { }
	
	public SurfaceInterface() {
		mResuming = false;
		mInitializedNotifier = new Object();
		mProgramTime = 0;
		setUpdatesPerSecond(60);
	}
	
	public void drawFrame() {
		mGraphics.beginFrame();
		draw();
		mGraphics.endFrame();
	}
	
	public void setGraphics(GraphicsTranslator graphics) {
		mGraphics = graphics;
	}
	
	public void onSurfaceCreated() {
		//System.out.println("--------------------------"+(""+this).split("@")[1]+" "+mResuming+"-------------------------------");
		if(mInitialized && !mResuming)
			return;
		if(mResuming) {
			mGraphics.restart();
			if(mAutoReloadOnResume)
				mGraphics.mGFXLoader.reloadTextures();
			resumingFinished();
		}else{
			mGraphics.init();
			initGraphics();
			postInitGraphics();
			if(mInitCallback!=null)
				mInitCallback.initializationFinished();
		}
		
		mInitialized = true;
		mResuming = false;
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
	
	public void onPause() {
		mResuming = true;
	}
	
	public void onResume() {
		
	}
	
	public void exit() {
		System.exit(0);
	}
	
}
