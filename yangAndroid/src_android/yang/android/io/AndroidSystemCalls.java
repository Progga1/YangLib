package yang.android.io;

import yang.android.graphics.YangActivity;
import yang.systemdependent.YangSystemCalls;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

public class AndroidSystemCalls extends YangSystemCalls{

	protected final Context mContext;
	protected final YangActivity mActivity;

	public AndroidSystemCalls(YangActivity activity) {
		mActivity = activity;
		mContext = mActivity.getApplicationContext();
	}

	@Override
	public void openKeyBoard() {

	}


	@Override
	public boolean reloadAfterPause() {
		return Build.VERSION.SDK_INT<11;
	}

	@Override
	public void throwDebugIntent() {
		final Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("http://www.youtube.com/watch?v=5PvA9LQ1Sfc"));
		mActivity.startActivity(intent);
	}

}
