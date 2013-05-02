package yang.graphics;

import yang.graphics.interfaces.InitializationCallback;
import yang.graphics.translator.GraphicsTranslator;
import yang.util.StringsXML;

public abstract class SurfaceInterface {
	
	public GraphicsTranslator mGraphics;
	protected boolean mResuming;

	protected boolean mInitialized;
	protected Object mInitializedNotifier;
	protected InitializationCallback mInitCallback;
	public StringsXML mStrings;
	
	protected abstract void initGraphics();
	protected void resumingFinished(){};
	public abstract void draw();
	
	protected void initializationFinished() { }
	public void onResume() { }
	
	public SurfaceInterface() {
		mResuming = false;
		mInitializedNotifier = new Object();
	}
	
	public void setGraphics(GraphicsTranslator graphics) {
		mGraphics = graphics;
	}
	
	public void surfaceCreated() {
		if(mInitialized && !mResuming)
			return;
		mGraphics.start();
		if(mResuming) {
			mGraphics.restartPrograms();
			resumingFinished();
		}else{
			initGraphics();
			initializationFinished();
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
	
	public void surfaceChanged(int width,int height) {
		mGraphics.setScreenSize(width, height);
		surfaceCreated();
	}
	
	public void onPause() {
		mResuming = true;
	}
	
	public boolean isInitialized() {
		return mInitialized;
	}
	
	public void setInitializationCallback(InitializationCallback initCallback) {
		mInitCallback = initCallback;
	}
	
}
