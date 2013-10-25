package yang.surface;

import java.io.FileNotFoundException;
import java.io.InputStream;

import yang.events.EventQueueHolder;
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
import yang.graphics.stereovision.StereoVision;
import yang.graphics.translator.AbstractGFXLoader;
import yang.graphics.translator.AbstractGraphics;
import yang.graphics.translator.GraphicsTranslator;
import yang.model.App;
import yang.model.DebugYang;
import yang.model.enums.UpdateMode;
import yang.sound.AbstractSoundManager;
import yang.systemdependent.AbstractResourceManager;
import yang.systemdependent.AbstractVibrator;
import yang.systemdependent.YangSensor;
import yang.util.Util;

public abstract class YangSurface implements EventQueueHolder {

	public static boolean CATCH_EXCEPTIONS = true;
	public static int ALWAYS_STEREO_VISION = 0;

	public final static int RUNTIME_STATE_RUNNING = 0;
	public final static int RUNTIME_STATE_PAUSED = 1;
	public final static int RUNTIME_STATE_STOPPED = 2;

	public GraphicsTranslator mGraphics;
	public StringsXML mStrings;
	public AbstractResourceManager mResources;
	public AbstractGFXLoader mGFXLoader;
	public AbstractSoundManager mSounds;
	public AbstractVibrator mVibrator;
	public YangSensor mSensor;
	public GFXDebug mGFXDebug;
	public String mPlatformKey = "";

	private UpdateMode mUpdateMode;
	protected boolean mInitialized = false;
	protected Object mInitializedNotifier;
	protected InitializationCallback mInitCallback;

	protected long mCatchUpTime;
	public double mProgramTime;
	public int mStepCount;
	public static float deltaTimeSeconds;
	protected static long deltaTimeNanos;
	protected int mUpdateWaitMillis = 1000/70;
	protected int mRuntimeState = 0;
	protected boolean mInactive = false;

	private Thread mUpdateThread = null;
	public YangEventListener mEventListener;
	public YangEventListener mMetaEventListener;
	public YangEventQueue mEventQueue;
	public int mDebugSwitchKey = -1;
	public boolean mPaused = false;
	public float mPlaySpeed = 1;
	public String mMacroFilename;
	public boolean mException = false;
	private int mStartupSteps = 1;
	private int mLoadingSteps = 1;
	private int mLoadingState = 0;
	private int mInitSteps = 0;
	private boolean mResuming = false;
	private boolean mLoadedOnce = false;

	private boolean mUseStereoVision = false;
	public StereoVision mStereoVision = null;
	private int mStereoResolution = 1024;
	public MacroExecuter mMacro;
	public DefaultMacroIO mDefaultMacroIO;

	/**
	 * GL-Thread
	 */
	protected abstract void initGraphics();
	protected abstract void draw();

	protected void onException(Exception ex) {

	}

	//Optional methods
	protected void postInitGraphics() { }
	protected void initGraphicsForResume() { }
	protected void onLoadingInterrupted(boolean resuming) { }

	public YangSurface() {
		mInitializedNotifier = new Object();
		mCatchUpTime = 0;
		mEventQueue = new YangEventQueue(getMaxEventCount());
		mStrings = new StringsXML();
		setUpdatesPerSecond(120);
		mUpdateMode = UpdateMode.SYNCHRONOUS;
		mMacroFilename = null;
		setStereoVision(ALWAYS_STEREO_VISION);
	}

	public void setStereoVision(int resolution) {
		final int widthFac = mUseStereoVision?2:1;
		if(resolution==0) {
			mUseStereoVision = false;
		}else{
			mUseStereoVision = true;
			mStereoResolution = resolution;
		}
		if(mGraphics!=null)
			this.onSurfaceChanged(mGraphics.mScreenWidth*widthFac, mGraphics.mScreenHeight);

	}

	protected void setStartupSteps(int loadingSteps,int initSteps) {
		mLoadingSteps = loadingSteps;
		mInitSteps = initSteps;
		mStartupSteps = initSteps+loadingSteps;
	}

	protected void setStartupSteps(int loadingSteps) {
		setStartupSteps(loadingSteps,0);
	}

	protected void exceptionOccurred(Exception ex) {
		try{
			mEventQueue.close();
			if(mMacro!=null)
				mMacro.close();
		}catch(final Exception ex2) {

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

	@Override
	public YangEventQueue getEventQueue() {
		return mEventQueue;
	}

	protected int getMaxEventCount() {
		return 2048;
	}

	public void setGraphics(GraphicsTranslator graphics) {
		mGraphics = graphics;
	}

	protected boolean assertMessage() {
		System.out.println("ASSERTIONS ARE ACTIVATED");
		return true;
	}

	public void recordMacro(String filename,AbstractMacroIO macroIO) throws FileNotFoundException {
		final MacroWriter writer = new MacroWriter(mResources.getSystemOutputStream(filename), mDefaultMacroIO);
		mEventQueue.registerEventWriter(writer);
	}

	public void recordMacro(String filename) throws FileNotFoundException {
		recordMacro(filename,mDefaultMacroIO);
	}

	public void playMacro(String filename,AbstractMacroIO macroIO) {
		final InputStream is = mResources.getSystemInputStream(filename);
		if(is==null)
			throw new RuntimeException("File not found: "+filename);
		mMacro = new MacroExecuter(is, macroIO);
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
		mSounds = App.soundManager;
		mSensor = App.sensor;
		mSensor.init(this);
		mEventQueue.setGraphics(mGraphics);
		if(mResources.assetExists("strings/strings.xml"))
			mStrings.load(mResources.getAssetInputStream("strings/strings.xml"));
		try{
			initGraphics();

			mDefaultMacroIO = new DefaultMacroIO(this);
			if(mMacroFilename!=null && mResources.fileExistsInFileSystem(mMacroFilename))
				playMacro(mMacroFilename);
			if(mMacro==null && DebugYang.AUTO_RECORD_MACRO) {
				final String filename = "run.ym";
				try {
					recordMacro(filename);
				} catch (final FileNotFoundException e) {
					DebugYang.printerr("Could not create '"+filename+"'");
				}
			}

			mGFXLoader.startEnqueuing();
			postInitGraphics();

			if(mInitCallback!=null)
				mInitCallback.initializationFinished();
			mInitialized = true;
			synchronized(mInitializedNotifier) {
				mInitializedNotifier.notifyAll();
			}

			if(mUpdateMode == UpdateMode.ASYNCHRONOUS)
				mUpdateThread.start();
		}catch(final Exception ex) {
			exceptionOccurred(ex);
		}

	}

	public void waitUntilInitialized() {
		synchronized(mInitializedNotifier) {
			try {
				mInitializedNotifier.wait();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void onSurfaceChanged(int width,int height) {
		try{
			mGraphics.setSurfaceSize(width, height, mUseStereoVision);
			if(mGFXDebug!=null)
				mGFXDebug.surfaceChanged();
			if(mStereoVision!=null)
				mStereoVision.surfaceChanged(mGraphics);
		}catch(final Exception ex) {
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
						} catch (final InterruptedException e) {
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

	protected boolean isLoadingFinished() {
		return mLoadingState>=mStartupSteps;
	}

	private void handleEvents() {
		assert mGraphics.preCheck("Handle events");
		if(mEventListener!=null) {
			if(!mPaused && mLoadingState>=mStartupSteps)
				mEventQueue.handleEvents(mEventListener);
			else
				mEventQueue.clearEvents();
		}
		assert mGraphics.checkErrorInst("Handle events");
	}

	protected void catchUp() {
//		if(alt) {
//		alt = false;
//		return;
//	}
//	alt = true;

		if(!mInitialized || mRuntimeState>0 || mLoadingState<mStartupSteps)
			return;
		if(mCatchUpTime==0)
			mCatchUpTime = System.nanoTime()-1;

		if(mPlaySpeed==0) {
			handleEvents();
			mCatchUpTime = 0;
		}else{
			while(mCatchUpTime<System.nanoTime()) {
				mCatchUpTime += (long)(deltaTimeNanos*mPlaySpeed);
				proceed();
			}
		}
	}
	boolean alt = false;
	public void proceed() {

		if(!mPaused && !mException) {
			try{
				if(mMacro!=null)
					mMacro.step();
				handleEvents();
				if(mLoadingState>=mStartupSteps) {
					mStepCount ++;
					mProgramTime += deltaTimeSeconds;
					step(deltaTimeSeconds);
				}
			}catch(final Exception ex){
				exceptionOccurred(ex);
			}
		}
	}

	protected void step(float deltaTime) {

	}

	protected void prepareLoading(boolean resuming) {
		//if(resuming)
			mGFXLoader.reenqueueTextures();
		if(mStartupSteps>0)
			mGFXLoader.divideQueueLoading(mStartupSteps-1);
		mGFXLoader.mEnqueueMode = false;
	}

	protected void loadAssets(int loadState,boolean resuming) {
		if(loadState>0 || mStartupSteps==1)
			mGFXLoader.loadEnqueuedTextures();
	}

	protected void initializeAssets(int initStep,boolean resuming) {
		System.out.println(mLoadingState+" "+initStep);
	}


	protected void drawLoadingScreen(int loadState,float progress,boolean resuming) {

	}

	protected void onLoadingFinished(boolean resuming) {

	}

	public final void drawFrame() {
		if(mUseStereoVision) {
			//STEREO VISION
			if(mStereoVision == null) {
				mStereoVision = new StereoVision();
				mStereoVision.init(mGraphics,mStereoResolution);
			}
			try{
				mGraphics.setTextureRenderTarget(mStereoVision.mStereoRightRenderTarget);
				mGraphics.mCameraShiftX = mStereoVision.mInterOcularDistance*AbstractGraphics.METERS_PER_UNIT;
				drawContent();
				mGraphics.leaveTextureRenderTarget();
				mGraphics.mCameraShiftX = -mStereoVision.mInterOcularDistance*AbstractGraphics.METERS_PER_UNIT;
				mGraphics.setTextureRenderTarget(mStereoVision.mStereoLeftRenderTarget);
				drawContent();
			}finally{
				mGraphics.leaveTextureRenderTarget();
			}

			mStereoVision.draw();
		}else{
			drawContent();
		}

	}

	private final void drawContent() {
		mGraphics.clear(0,0,0);
		try{
			assert mGraphics.preCheck("Draw content");
			if(mMetaEventListener!=null)
				mEventQueue.handleMetaEvents(mMetaEventListener);

			if(mInactive) {
				mGraphics.beginFrame();
				mGraphics.clear(0,0,0);
				mGraphics.endFrame();
				return;
			}

			if(mException) {
				mGraphics.beginFrame();
				mGraphics.clear(0.1f,0,0);
				if(mGFXDebug!=null)
					mGFXDebug.draw();
				mGraphics.endFrame();
				return;
			}

			if(mRuntimeState>0 && !mResuming) {
				mResuming = mLoadedOnce;
				mGraphics.restart();
				if(mGraphics.mCurDrawListener!=null)
					mGraphics.mCurDrawListener.onRestartGraphics();
			}

			assert mGraphics.preCheck("Catchup");
			if(mUpdateMode==UpdateMode.SYNCHRONOUS)
				catchUp();
			assert mGraphics.checkErrorInst("Catchup");

			if(mLoadingState>=mStartupSteps && DebugYang.DEBUG_LEVEL>0 && mGFXDebug!=null) {
				assert mGraphics.preCheck("Debug values");
				mGFXDebug.reset();
				if(DebugYang.DRAW_GFX_VALUES)
					mGFXDebug.printGFXDebugValues();
				assert mGraphics.checkErrorInst("Debug values");
			}
			mGraphics.beginFrame();
			if(mLoadingState>=mStartupSteps) {
				assert mGraphics.preCheck("Draw call");
				draw();
				if(DebugYang.DEBUG_LEVEL>0 && mGFXDebug!=null) {
					mGFXDebug.draw();
				}
			}else{
				if(mLoadingState==0)
					prepareLoading(mResuming);
				if(mLoadingState>=mLoadingSteps)
					initializeAssets(mLoadingState-mLoadingSteps,mResuming);
				else
					loadAssets(mLoadingState,mResuming);
				if(mLoadingState==mStartupSteps-1) {
					mGFXLoader.finishLoading();
					mGraphics.unbindTextures();
					onLoadingFinished(mResuming);
					mLoadedOnce = true;
					mEventQueue.clearEvents();
					mRuntimeState = 0;
					mResuming = false;
					draw();
				}else{
					float progress;
					if(mStartupSteps==2)
						progress = 1;
					else
						progress = (float)mLoadingState/(mStartupSteps-2);
					drawLoadingScreen(mLoadingState,progress,mResuming);
				}
				mCatchUpTime = 0;
				mLoadingState++;
			}
			mGraphics.endFrame();

		}catch(final Exception ex){
			exceptionOccurred(ex);
		}
	}

	public void stop() {
		mInactive = true;
		mRuntimeState = 2;
		mLoadingState = 0;
	}

	/**
	 * Non-GL-Thread!
	 */
	public void pause() {
//		if(mUpdateThread!=null)
//			synchronized (mUpdateThread) {
//				mUpdateThread.suspend();
//			}
		mInactive = true;
		mRuntimeState = 1;
		mCatchUpTime = 0;
		mLoadingState = 0;
		if(!mLoadedOnce) {
			onLoadingInterrupted(false);
		}
		if(mResuming)
			onLoadingInterrupted(true);
		if(mSensor!=null)
			mSensor.pause();
	}

	/**
	 * Non-GL-Thread!
	 */
	public void resume() {
//		if(mUpdateThread!=null && mUpdateThread.isAlive())
//			synchronized (mUpdateThread) {
//				mUpdateThread.resume();
//			}
		mInactive = false;
		mCatchUpTime = 0;
		mResuming = false;
		mLoadingState = 0;
		if(mSensor!=null)
			mSensor.resume();
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

	public void simulatePause() {
		pause();
		stop();
		mGraphics.deleteAllTextures();
	}

	public void simulateResume() {
		if(mInactive)
			resume();
	}

	public boolean isInactive() {
		return mInactive;
	}

	public boolean isStereoVision() {
		return mUseStereoVision;
	}

}
