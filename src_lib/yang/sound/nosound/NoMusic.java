package yang.sound.nosound;

import yang.sound.AbstractMusic;
import yang.sound.AbstractSoundManager;

public class NoMusic extends AbstractMusic {

	public NoMusic(AbstractSoundManager mgr) {
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
	public void pause() {

	}

	@Override
	public void seek(int time) {

	}

	@Override
	public void setBalance(float balance) {

	}

}
