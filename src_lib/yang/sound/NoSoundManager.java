package yang.sound;

public class NoSoundManager extends AbstractSoundManager {

	@Override
	protected AbstractSound loadSound(String name) {
		return null;
	}

	@Override
	protected AbstractMusic loadMusic(String name) {
		return null;
	}

}
