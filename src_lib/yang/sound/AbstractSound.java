package yang.sound;

public abstract class AbstractSound {

	/**
	 * @param volume in dB of this Sound
	 */
	public abstract void init(float volume);

	public abstract void play();
	public abstract void playLoop();
	public abstract void stopLoop();

	public abstract boolean isLoaded();

}