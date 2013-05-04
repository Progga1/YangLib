package yang.android.graphics;

import yang.graphics.SurfaceInterface;
import yang.model.ExitCallback;
import android.app.Activity;
import android.media.AudioManager;

public abstract class YangActivity extends Activity implements ExitCallback {
	
	protected YangTouchSurface mGLView;
	
	public void defaultInit(YangTouchSurface androidSurface) {
		
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		mGLView = androidSurface;

		setContentView(mGLView.getView());
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
		mGLView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mGLView.onResume();
	}

	@Override
	public void onBackPressed() {
		mGLView.onBackPressed();
	}
	
	public void exit() {
		finish();
	}

	public void defaultInit(boolean useDebugEditText) {
		if(useDebugEditText)
			defaultInit(new YangKeyTouchSurface(super.getApplicationContext()));
		else
			defaultInit();
	}
}