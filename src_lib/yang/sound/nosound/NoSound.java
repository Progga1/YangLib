package yang.sound.nosound;

import yang.sound.AbstractSound;
import yang.sound.AbstractSoundManager;

public class NoSound extends AbstractSound {

	public NoSound(AbstractSoundManager mgr) {
		super(mgr);
	}

	@Override
	public void play() {

	}

	@Override
	public void playLoop() {

	}

	@Override
	public void stop() {

	}

	@Override
	public void setSpeed(float speed) {

	}

	@Override
	public void setRepeatCount(int count) {

	}

	@Override
	public void setBalance(float balance) {

	}

}
