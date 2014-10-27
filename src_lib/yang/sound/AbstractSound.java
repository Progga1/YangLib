package yang.sound;

public abstract class AbstractSound {

	protected AbstractSoundManager mManager;
	protected float mVolume;

	public AbstractSound(AbstractSoundManager mgr) {
		mManager = mgr;
		mVolume = 1.0f;
	}

	public abstract void play();
	public abstract void playLoop();
	public abstract void stop();

	public void play(float volume, float balance, float speed, int repeat) {
		setVolume(volume);
		setBalance(balance);
		setSpeed(speed);
		setRepeatCount(repeat);
		play();
	}

	public void play(float volume) {
		setVolume(volume);
		play();
	}

	public void play(float volume,float balance) {
		setVolume(volume);
		setBalance(balance);
		play();
	}

	/**
	 * Sets the volume of this sound file.
	 * @param volume range: 0.0 - 1.0
	 */
	public void setVolume(float volume) {
		mVolume = volume;
	}

	/**
	 * Sets the speed of this sound file.
	 * @param speed 1.0 is default
	 */
	public abstract void setSpeed(float speed);


	/**
	 * Sets the amount of times this sound is repeated until it stops.
	 * @param count Amount of repeats, -1 for infinite
	 */
	public abstract void setRepeatCount(int count);

	/**
	 * Sets the sound balance of this file.
	 * @param balance -1.0 left only ... 1.0 right only
	 */
	public abstract void setBalance(float balance);
}