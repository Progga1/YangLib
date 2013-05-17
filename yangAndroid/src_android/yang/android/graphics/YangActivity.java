package yang.android.graphics;

import yang.graphics.SurfaceInterface;
import yang.model.ExitCallback;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;

public abstract class YangActivity extends Activity implements ExitCallback {
	
	public static boolean PRINT_ACTIVITY_DEBUG = true;
	
	protected YangTouchSurface mGLView;
	
	public void defaultInit(YangTouchSurface androidSurface) {
		
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); 
		
		activityOut("INIT");
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		mGLView = androidSurface;
		
		setContentView(mGLView.getView());
	}
	
	protected void activityOut(Object msg) {
		if(PRINT_ACTIVITY_DEBUG)
			System.out.println("--------------------------("+(""+this).split("@")[1]+") "+msg+"---------------------------");
	}
	
	protected void setSurface(SurfaceInterface yangSurface) {
		mGLView.setSurface(yangSurface);
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
	protected void onStop() {
		super.onStop();
		mGLView.mSceneRenderer.mSurfaceInterface.onStop();
		activityOut("STOP");
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
//		if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//			
//		}
	}

	public void defaultInit(boolean useDebugEditText) {
		if(useDebugEditText)
			defaultInit(new YangKeyTouchSurface(super.getApplicationContext()));
		else
			defaultInit();
	}
}