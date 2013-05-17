package yang.android.graphics;

import yang.graphics.SurfaceInterface;
import yang.model.ExitCallback;
import android.app.Activity;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;

public abstract class YangActivity extends Activity implements ExitCallback {
	
	public static boolean initialized = false;
	protected YangTouchSurface mGLView;
	
	public void defaultInit(YangTouchSurface androidSurface) {
		
		activityOut("INIT");
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		mGLView = androidSurface;

		setContentView(mGLView.getView());
	}
	
	protected void activityOut(Object msg) {
		System.out.println("--------------------------("+(""+this).split("@")[1]+") "+msg+"---------------------------");
	}
	
	protected void setSurface(SurfaceInterface yangSurface) {
		mGLView.setSurface(yangSurface);
		initialized = true;
	}
	
	public void defaultInit() {
		defaultInit(new YangTouchSurface(super.getApplicationContext()));
	}

	@Override
	protected void onPause() {
		super.onPause();
		activityOut("PAUSED");
		mGLView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		activityOut("RESUME");
		mGLView.onResume();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		activityOut("START");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		activityOut("DESTROY");
	}
	
	@Override
	protected void onRestoreInstanceState (Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		activityOut("RESTORE INSTANCE STATE");
	}
	
	@Override
	protected void onSaveInstanceState (Bundle outState) {
		super.onSaveInstanceState(outState);
		activityOut("SAVED INSTANCE STATE");
	}
		
	@Override
	protected void onStop() {
		super.onStop();
		activityOut("STOP");
	}

	@Override
	public void onBackPressed() {
		mGLView.onBackPressed();
	}
	
	public void exit() {
		activityOut("EXIT");
		finish();
	}
	
	@Override
	public void onConfigurationChanged(Configuration config) {
		super.onConfigurationChanged(config);
		activityOut("CONFIG_CHANGED: "+config);
	}

	public void defaultInit(boolean useDebugEditText) {
		if(useDebugEditText)
			defaultInit(new YangKeyTouchSurface(super.getApplicationContext()));
		else
			defaultInit();
	}
}