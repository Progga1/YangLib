package yang.sound;

public abstract class AbstractMusic {

	protected AbstractSoundManager mManager;
	protected float mVolume;

	public AbstractMusic(AbstractSoundManager mgr) {
		mManager = mgr;
		mVolume = 1.0f;
	}

	public abstract void play();
	public abstract void playLoop();
	public abstract void stop();
	public abstract void pause();

	public void play(float volume, float balance, boolean loop) {
		setVolume(volume);
		setBalance(balance);
		if (loop) playLoop();
		else play();
	}

	/**
	 * Sets the volume of this sound file.
	 * @param volume range: 0.0 - 1.0
	 */
	public void setVolume(float volume) {
		mVolume = volume;
	}

	/**
	 * Seeks to the given value in seconds
	 * @param time time in seconds to seek to
	 */
	public abstract void seek(int time);

	/**
	 * Sets the sound balance of this file.
	 * @param balance -1.0 left only ... 1.0 right only
	 */
	public abstract void setBalance(float balance);

	public abstract boolean hasReachedEnd();

	public abstract void setPlayrate(float playrate);

}
