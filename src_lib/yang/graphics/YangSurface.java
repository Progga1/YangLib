package yang.graphics;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;

import yang.events.YangEventQueue;
import yang.events.listeners.YangEventListener;
import yang.events.macro.DefaultMacroIO;
import yang.events.macro.MacroExecuter;
import yang.events.macro.MacroWriter;
import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.font.BitmapFont;
import yang.graphics.interfaces.InitializationCallback;
import yang.graphics.model.GFXDebug;
import yang.graphics.translator.AbstractGFXLoader;
import yang.graphics.translator.GraphicsTranslator;
import yang.model.DebugYang;
import yang.model.enums.UpdateMode;
import yang.systemdependent.AbstractResourceManager;
import yang.util.StringsXML;

public abstract class YangSurface {
	
	public GraphicsTranslator mGraphics;
	
	private UpdateMode mUpdateMode;
	protected boolean mAutoReloadTexturesOnResume = true;
	protected boolean mInitialized;
	protected Object mInitializedNotifier;
	protected InitializationCallback mInitCallback;
	public StringsXML mStrings;
	public AbstractResourceManager mResources;
	public AbstractGFXLoader mGFXLoader;
	public GFXDebug mDebug;
	
	protected long mCatchUpTime;
	public double mProgramTime;
	public int mStepCount;
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
	
	private MacroExecuter mMacro;
	public DefaultMacroIO mDefaultMacroIO;
	
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
	
	public void setGraphics(GraphicsTranslator graphics) {
		mGraphics = graphics;
	}
	
	private boolean assertMessage() {
		System.out.println("ASSERTIONS ARE ACTIVATED");
		return true;
	}
	
	public void setMacro(BufferedInputStream stream) {
		
	}
	
	public void onSurfaceCreated() {
		if(mInitialized) {
			DebugYang.println("ALREADY INITIALIZED");
			return;
		}
		mProgramTime = 0;
		mStepCount = 0;
		assert assertMessage();
		mGraphics.init();
		mGFXLoader = mGraphics.mGFXLoader;
		mResources = mGraphics.mGFXLoader.mResources;
		mEventQueue.setGraphics(mGraphics);
		if(mResources.fileExists("strings/strings.xml"))
			mStrings = new StringsXML(mResources.getInputStream("strings/strings.xml"));
		initGraphics();
		
		if(mInitCallback!=null)
			mInitCallback.initializationFinished();
		
		mDefaultMacroIO = new DefaultMacroIO(this);
		//mMacro = new MacroExecuter(mResources.getFileSystemInputStream("run.ym"), mDefaultMacroIO);
		if(mMacro==null && DebugYang.AUTO_RECORD_MACRO) {
			MacroWriter writer;
			String filename = "run.ym";
			try {
				writer = new MacroWriter(mResources.getFileSystemOutputStream(filename), mDefaultMacroIO);
				mEventQueue.registerEventWriter(writer);
			} catch (FileNotFoundException e) {
				DebugYang.printerr("Could not create '"+filename+"'");
			}
		}
		mInitialized = true;
		synchronized(mInitializedNotifier) {
			mInitializedNotifier.notifyAll();
		}
		
		if(mUpdateMode == UpdateMode.ASYNCHRONOUS)
			mUpdateThread.start();
		
		postInitGraphics();
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
	
	protected void catchUp() {
		if(!mInitialized || mRuntimeState>0)
			return;
		if(mCatchUpTime==0)
			mCatchUpTime = System.nanoTime()-1;
		
		if(mPlaySpeed==0) {
			if(!mPaused && mEventListener!=null)
				mEventQueue.handleEvents(mEventListener);
			mCatchUpTime = 0;
			return;
		}
		while(mCatchUpTime<System.nanoTime()) {
			mCatchUpTime += (long)(mDeltaTimeNanos*mPlaySpeed);
			proceed(deltaTimeSeconds);
		}
	}
	
	public void proceed(float deltaTime) {
		if(!mPaused) {
			if(mMacro!=null)
				mMacro.step();
			if(mEventListener!=null)
				mEventQueue.handleEvents(mEventListener);
			mProgramTime += deltaTime;
			mStepCount ++;
			step(deltaTime);
		}
	}
	
	protected void step(float deltaTime) {
		
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
