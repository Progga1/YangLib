package yang.graphics;

import java.io.FileNotFoundException;

import yang.events.YangEventQueue;
import yang.events.listeners.YangEventListener;
import yang.events.macro.AbstractMacroIO;
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
import yang.sound.SoundManager;
import yang.systemdependent.AbstractResourceManager;
import yang.util.StringsXML;
import yang.util.Util;

public abstract class YangSurface {
	
	public static boolean CATCH_EXCEPTIONS = true;
	
	public GraphicsTranslator mGraphics;
	public StringsXML mStrings;
	public AbstractResourceManager mResources;
	public AbstractGFXLoader mGFXLoader;
	public SoundManager mSounds;
	public GFXDebug mGFXDebug;
	public String mPlatformKey = "";
	
	private UpdateMode mUpdateMode;
	protected boolean mAutoReloadTexturesOnResume = true;
	protected boolean mInitialized;
	protected Object mInitializedNotifier;
	protected InitializationCallback mInitCallback;
	
	protected long mCatchUpTime;
	public double mProgramTime;
	public int mStepCount;
	public static float deltaTimeSeconds;
	protected static long deltaTimeNanos;
	protected int mUpdateWaitMillis = 1000/70;
	protected int mRuntimeState = 0;
	private float mLoadingProgress = -1;
	private Thread mUpdateThread = null;
	public YangEventListener mEventListener;
	public YangEventListener mMetaEventListener;
	public YangEventQueue mEventQueue;
	public int mDebugSwitchKey = -1;
	public boolean mPaused = false;
	public float mPlaySpeed = 1;
	public String mMacroFilename;
	public boolean mException = false;
	
	public MacroExecuter mMacro;
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
	
	protected void onException(Exception ex) {
		
	}
	
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
		mMacroFilename = null;
	}
	
	protected void exceptionOccurred(Exception ex) {
		try{
			mEventQueue.close();
			if(mMacro!=null)
				mMacro.close();
		}catch(Exception ex2) {
			
		}
		onException(ex);
		if(!CATCH_EXCEPTIONS) {
			if(ex instanceof RuntimeException)
				throw (RuntimeException)ex;
			else
				throw new RuntimeException(ex);
		}
		mException = true;
		mPaused = true;
		if(mGFXDebug!=null)
			mGFXDebug.setErrorString(ex.getClass()+": "+ex.getMessage()+"\n\n"+Util.arrayToString(ex.getStackTrace(),"\n").replace("(", " ("));
		ex.printStackTrace();
	}
	
	public void setMacroFilename(String filename) {
		mMacroFilename = filename;
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
	
	public void recordMacro(String filename,AbstractMacroIO macroIO) throws FileNotFoundException {
		MacroWriter writer = new MacroWriter(mResources.getFileSystemOutputStream(filename), mDefaultMacroIO);
		mEventQueue.registerEventWriter(writer);
	}
	
	public void recordMacro(String filename) throws FileNotFoundException {
		recordMacro(filename,mDefaultMacroIO);
	}
	
	public void playMacro(String filename,AbstractMacroIO macroIO) {
		try {
			mMacro = new MacroExecuter(mResources.getFileSystemInputStream(filename), macroIO);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void playMacro(String filename) {
		playMacro(filename,mDefaultMacroIO);
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
		try{
			initGraphics();
			
			mDefaultMacroIO = new DefaultMacroIO(this);
			if(mMacroFilename!=null && mResources.fileExistsInFileSystem(mMacroFilename))
				playMacro(mMacroFilename);
			if(mMacro==null && DebugYang.AUTO_RECORD_MACRO) {
				String filename = "run.ym";
				try {
					recordMacro(filename);
				} catch (FileNotFoundException e) {
					DebugYang.printerr("Could not create '"+filename+"'");
				}
			}
			
			postInitGraphics();
			
			if(mInitCallback!=null)
				mInitCallback.initializationFinished();
			mInitialized = true;
			synchronized(mInitializedNotifier) {
				mInitializedNotifier.notifyAll();
			}
			
			if(mUpdateMode == UpdateMode.ASYNCHRONOUS)
				mUpdateThread.start();
		}catch(Exception ex) {
			exceptionOccurred(ex);
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
		try{
			mGraphics.setSurfaceSize(width, height);
			if(mGFXDebug!=null)
				mGFXDebug.surfaceChanged();
		}catch(Exception ex) {
			exceptionOccurred(ex);
		}
	}
	
	public boolean isInitialized() {
		return mInitialized;
	}
	
	public void setInitializationCallback(InitializationCallback initCallback) {
		mInitCallback = initCallback;
	}
	
	public static void setUpdatesPerSecond(int updatesPerSecond) {
		deltaTimeSeconds = 1f/updatesPerSecond;
		deltaTimeNanos = 1000000000/updatesPerSecond;
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
		mGFXDebug = new GFXDebug(this,graphics,font);
		mGFXDebug.surfaceChanged();
	}
	
	protected void catchUp() {		
//		if(alt) {
//		alt = false;
//		return;
//	}
//	alt = true;
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
		if(true) {
			while(mCatchUpTime<System.nanoTime()) {
				mCatchUpTime += (long)(deltaTimeNanos*mPlaySpeed);
				proceed();
			}
		}else{
			mCatchUpTime += (long)(deltaTimeNanos*mPlaySpeed);
			proceed();
			mCatchUpTime += (long)(deltaTimeNanos*mPlaySpeed);
			proceed();
		}
	}
	boolean alt = false;
	public void proceed() {

		if(!mPaused && !mException) {
			try{
				if(mMacro!=null)
					mMacro.step();
				if(mEventListener!=null)
					mEventQueue.handleEvents(mEventListener);
				mStepCount ++;
				mProgramTime += deltaTimeSeconds;
	
				step(deltaTimeSeconds);
			}catch(Exception ex){
				exceptionOccurred(ex);
			}
		}
	}
	
	protected void step(float deltaTime) {
		
	}
	
	public final void drawFrame() {
		
		if(mException) {
			mGraphics.beginFrame();
			mGraphics.clear(0.1f,0,0);
			if(mGFXDebug!=null)
				mGFXDebug.draw();
			mGraphics.endFrame();
			return;
		}
		try{
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
	
			if(DebugYang.DEBUG_LEVEL>0 && mGFXDebug!=null) {
				mGFXDebug.reset();
				if(DebugYang.DRAW_GFX_VALUES)
					mGFXDebug.printGFXDebugValues();
			}
			mGraphics.beginFrame();
			draw();
			if(DebugYang.DEBUG_LEVEL>0 && mGFXDebug!=null) {
				mGFXDebug.draw();
			}
			mGraphics.endFrame();
		
		}catch(Exception ex){
			exceptionOccurred(ex);
		}
		
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
		mEventQueue.close();
		if(mMacro!=null)
			mMacro.close();
		System.exit(0);
	}
	
	public void handleArgs(String[] args,int startIndex) {
		if(args==null)
			return;
		if(args.length>=1)
			setMacroFilename(args[0]);
	}
	
}
