package yang.graphics;

import yang.events.YangEventQueue;
import yang.events.listeners.YangEventListener;
import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.font.BitmapFont;
import yang.graphics.interfaces.InitializationCallback;
import yang.graphics.model.GFXDebug;
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
	public GFXDebug mDebug;
	
	protected long mCatchUpTime;
	public static float deltaTimeSeconds;
	protected int mUpdateWaitMillis = 1000/70;
	protected long mDeltaTimeNanos;
	protected int mRuntimeState = 0;
	private float mLoadingProgress = -1;
	private Thread mUpdateThread = null;
	public YangEventListener mEventListener;
	public YangEventListener mMetaEventListener;
	public YangEventQueue mEventQueue;
	public int mDebugSwitchKey = -1;
	public boolean mPaused = false;
	public float mPlaySpeed = 1;
	
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
		mCatchUpTime = 0;
		mEventQueue = new YangEventQueue(getMaxEventCount());
		setUpdatesPerSecond(120);
		mUpdateMode = UpdateMode.SYNCHRONOUS;
	}
	
	public YangEventQueue getEventQueue() {
		return mEventQueue;
	}
	
	protected int getMaxEventCount() {
		return 2048;
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
						catchUp();
					}
				}
			};
		}
	}
	
	protected void initDebugOutput(DefaultGraphics<?> graphics, BitmapFont font) {
		mDebug = new GFXDebug(this,graphics,font);
	}
	
	public final void drawFrame() {
		
		if(mMetaEventListener!=null)
			mEventQueue.handleMetaEvents(mMetaEventListener);
		
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
			catchUp();

		if(mDebug!=null) {
			mDebug.reset();
			if(DebugYang.DRAW_GFX_VALUES)
				mDebug.printGFXDebugValues();
		}
		mGraphics.beginFrame();
		draw();
		if(mDebug!=null) {
			mDebug.draw();
		}
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
		mEventQueue.setGraphics(mGraphics);
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
	
	protected void catchUp() {
		if(!mInitialized || mRuntimeState>0)
			return;
		if(mPlaySpeed==0) {
			if(!mPaused && mEventListener!=null)
				mEventQueue.handleEvents(mEventListener);
			mCatchUpTime = 0;
			return;
		}
		while(mCatchUpTime<System.nanoTime()) {
			proceed(deltaTimeSeconds);
		}
	}
	
	public void proceed(float deltaTime) {
		if(!mInitialized || mRuntimeState>0)
			return;
		if(mCatchUpTime==0)
			mCatchUpTime = System.nanoTime()-1;

		mCatchUpTime += (long)(mDeltaTimeNanos*mPlaySpeed);

		if(!mPaused) {
			if(mEventListener!=null)
				mEventQueue.handleEvents(mEventListener);
			step(deltaTime);
		}
	}
	
	protected void step(float deltaTime) {
		
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
		mCatchUpTime = 0;
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
		mCatchUpTime = 0;
	}
	
	public void setPaused(boolean paused) {
		mPaused = paused;
		mEventQueue.mMetaMode = paused;
	}
	
	public boolean isPaused() {
		return mPaused;
	}
	
	public void exit() {
		System.exit(0);
	}
	
}
