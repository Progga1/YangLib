package yang.sound.nosound;

import yang.sound.AbstractMusic;
import yang.sound.AbstractSound;
import yang.sound.AbstractSoundManager;


public class NoSoundManager extends AbstractSoundManager {

	@Override
	protected AbstractSound derivedLoadSound(String name) {
		return new NoSound(this);
	}

	@Override
	protected AbstractMusic derivedLoadMusic(String name) {
		return new NoMusic(this);
	}

}
