package yang.android.io;

import yang.systemdependent.YangSystemCalls;
import android.content.Context;
import android.os.Build;

public class AndroidSystemCalls extends YangSystemCalls{

	protected final Context mContext;

	public AndroidSystemCalls(Context context) {
		mContext = context;
	}

	@Override
	public void openKeyBoard() {

	}

	@Override
	public boolean reloadAfterPause() {
		return Build.VERSION.SDK_INT<11;
	}

}
