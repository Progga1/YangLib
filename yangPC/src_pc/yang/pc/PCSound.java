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
		if (mSound == null) return;
		new Thread() {
			@Override
			public void run() {
				mSound.stop();
			}
		}.start();
	}

	@Override
	public void play(float volume, float balance, float speed, int repeat) {
		if (mManager.isSoundMuted()) return;
		if (mSound == null) return;
		mSound.setCycleCount(repeat);
		mSound.play(volume, balance, speed, 0, 1);
	}
}