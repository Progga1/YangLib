package yang.pc;

import javafx.scene.media.AudioClip;
import javafx.scene.media.MediaPlayer;
import yang.sound.AbstractSound;
import yang.sound.AbstractSoundManager;


public class PCSound extends AbstractSound {

	private AudioClip mSound;

	public PCSound(AbstractSoundManager mgr, AudioClip player) {
		super(mgr);
		mSound = player;
	}

	@Override
	public void play() {
		if (mManager.isSoundMuted()) return;
		if (mSound.isPlaying()) return;

		mSound.setVolume(mVolume*mManager.getSoundVolume());
		mSound.setCycleCount(1);
		mSound.play();
	}

	@Override
	public void playLoop() {
		if (mManager.isSoundMuted()) return;
		if (mSound.isPlaying()) return;

		mSound.setVolume(mVolume*mManager.getSoundVolume());
		mSound.setCycleCount(MediaPlayer.INDEFINITE);
		mSound.play();
	}

	@Override
	public void stop() {
		mSound.stop();
	}

	@Override
	public void setSpeed(float speed) {
		mSound.setRate(speed);
	}

	@Override
	public void setVolume(float volume) {
		mSound.setVolume(volume);
	}

	@Override
	public void setRepeatCount(int count) {
		mSound.setCycleCount(count);
	}

	@Override
	public void setBalance(float balance) {
		mSound.setBalance(balance);
	}
}