package yang.graphics;

import yang.graphics.interfaces.InitializationCallback;
import yang.graphics.translator.GraphicsTranslator;
import yang.model.DebugYang;
import yang.model.enums.UpdateMode;
import yang.util.StringsXML;

public abstract class YangSurface {
	
	public GraphicsTranslator mGraphics;
	
	private UpdateMode mUpdateMode;
	protected boolean mAutoReloadTexturesOnResume = true;
	protected boolean mInitialized;
	protected Object mInitializedNotifier;
	protected InitializationCallback mInitCallback;
	public StringsXML mStrings;
	
	protected double mProgramStartTime;
	protected long mProgramTime;
	public static float deltaTimeSeconds;
	protected int mUpdateWaitMillis = 1000/70;
	protected long mDeltaTimeNanos;
	protected int mRuntimeState = 0;
	private float mLoadingProgress = -1;
	private Thread mUpdateThread = null;
	
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
		setUpdatesPerSecond(120);
		mUpdateMode = UpdateMode.SYNCHRONOUS;
	}
	
	public void setUpdateMode(UpdateMode updateMode) {
		mUpdateMode = updateMode;
		if(mUpdateMode==UpdateMode.ASYNCHRONOUS) {
			mUpdateThread = new Thread() {
				@Override
				public void run() {
					
					while(true) {
						try {
							if(mRuntimeState>0) {
								Thread.sleep(100);
								continue;
							}else
								Thread.sleep(mUpdateWaitMillis);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						update();
					}
				}
			};
		}
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
			if(mGraphics.mCurDrawListener!=null)
				mGraphics.mCurDrawListener.onRestartGraphics();
		}
		
		if(mUpdateMode==UpdateMode.SYNCHRONOUS)
			update();
		
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
		
		if(mUpdateMode == UpdateMode.ASYNCHRONOUS)
			mUpdateThread.start();
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
		deltaTimeSeconds = 1f/updatesPerSecond;
		mDeltaTimeNanos = 1000000000/updatesPerSecond;
	}
	
	public void step(float deltaTime) {
		
	}
	
	protected boolean update() {
		if(!mInitialized || mRuntimeState>0)
			return true;
		if(mProgramTime==0)
			mProgramTime = System.nanoTime()-1;
		boolean result = mProgramTime<System.nanoTime();
		while(mProgramTime<System.nanoTime()) {
			mProgramTime += mDeltaTimeNanos;
			step(deltaTimeSeconds);
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
//		if(mUpdateThread!=null)
//			synchronized (mUpdateThread) {
//				mUpdateThread.suspend();
//			}
		mRuntimeState = 1;
		mProgramTime = 0;
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
//		if(mUpdateThread!=null && mUpdateThread.isAlive())
//			synchronized (mUpdateThread) {
//				mUpdateThread.resume();
//			}
		mProgramTime = 0;
	}
	
	public void exit() {
		System.exit(0);
	}
	
}
