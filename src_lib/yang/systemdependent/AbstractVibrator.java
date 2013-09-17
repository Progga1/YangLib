package yang.systemdependent;

public abstract class AbstractVibrator {

	public boolean hasVibrator() {
		return false;
	}

	public void vibrate(long milliseconds) {

	}

	public void vibrate(long[] pattern, int repeat) {

	}

	public void cancel() {

	}
}
