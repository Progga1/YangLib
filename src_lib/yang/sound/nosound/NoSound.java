package yang.sound.nosound;

import yang.sound.AbstractSound;
import yang.sound.AbstractSoundManager;

public class NoSound extends AbstractSound {

	public NoSound(AbstractSoundManager mgr) {
		super(mgr);
	}

	@Override
	public void stop() {

	}

	@Override
	public void play(float volume, float balance, float speed, int repeat) {

	}
}
