package yang.android.io;

import yang.android.graphics.YangActivity;
import yang.systemdependent.YangSystemCalls;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.inputmethod.InputMethodManager;

public class AndroidSystemCalls extends YangSystemCalls {

	protected final Context mContext;
	protected final YangActivity mActivity;

	public AndroidSystemCalls(YangActivity activity) {
		mActivity = activity;
		mContext = mActivity.getApplicationContext();
	}

	@Override
	public void openKeyBoard() {
		final InputMethodManager inputMgr = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMgr.showSoftInput(mActivity.getWindow().getCurrentFocus(), InputMethodManager.SHOW_FORCED);
	}

	@Override
	public void hideKeyBoard() {
		final InputMethodManager inputMgr = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMgr.hideSoftInputFromWindow(mActivity.getWindow().getCurrentFocus().getWindowToken(), 0);
	}

	@Override
	public boolean reloadAfterPause() {
		return Build.VERSION.SDK_INT<11 || ALWAYS_RELOAD_AFTER_PAUSE;
	}

	@Override
	public void throwDebugIntent() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
				builder.setMessage("debug intent")
				.setPositiveButton("foo", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {

					}
				})
				.setNegativeButton("bar", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {

					}
				});
				builder.create().show();
			}
		});
	}

	@Override
	public void exit() {
		mActivity.finish();
	}

}
