package yang.systemdependent;

public abstract class AbstractVibrator {

	protected boolean mEnabled;

	public boolean hasVibrator() {
		return false;
	}

	public void vibrate(long milliseconds) {

	}

	public void vibrate(long[] pattern, int repeat) {

	}

	public void cancel() {

	}

	public boolean isEnabled() {
		return mEnabled;
	}

	public void setEnabled(boolean enabled) {
		mEnabled = enabled;
	}
}
