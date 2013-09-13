package yang.android;

import yang.systemdependent.AbstractVibrator;
import android.content.Context;
import android.os.Vibrator;

public class AndroidVibrator extends AbstractVibrator {

	private Context mContext;
	private Vibrator mVibrator;
	private boolean mHasVibrator;

	public AndroidVibrator(Context context) {
		mContext = context;

		mVibrator = (Vibrator)mContext.getSystemService(Context.VIBRATOR_SERVICE);

		mHasVibrator = false;

		if (mVibrator != null) {
			try {
				mVibrator.vibrate(1);
				mHasVibrator = true;
			} catch (Exception e) {
				System.out.println("no vibrator found");
			}
		}
	}

	@Override
	public boolean hasVibrator() {
		return mHasVibrator;
	}

	@Override
	public void vibrate(long milliseconds) {
		if (mHasVibrator) mVibrator.vibrate(milliseconds);
	}

	@Override
	public void vibrate(long[] pattern, int repeat) {
		if (mHasVibrator) mVibrator.vibrate(pattern, repeat);
	}
}
