package yang.pc;

import javafx.scene.media.AudioClip;
import yang.sound.AbstractSound;
import yang.sound.AbstractSoundManager;


public class PCSound extends AbstractSound {

	private AudioClip mSound;

	public PCSound(AbstractSoundManager mgr, AudioClip player) {
		super(mgr);
		mSound = player;
	}

	@Override
	public void stop() {
		if(true)
			return;
		if (mSound == null) return;
		mSound.stop();
	}

	@Override
	public void play(float volume, float balance, float speed, int repeat) {
		if (mSound == null) return;
		mSound.setCycleCount(repeat);
		mSound.play(volume, balance, speed, 0, 1);
	}
}