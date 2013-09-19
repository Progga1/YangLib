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
		if (mSound == null) return;
		if (mManager.isSoundMuted()) return;
		if (mSound.isPlaying()) return;

		mSound.setVolume(mVolume*mManager.getSoundVolume());
		mSound.setCycleCount(1);
		mSound.play();
	}

	@Override
	public void playLoop() {
		if (mSound == null) return;
		if (mManager.isSoundMuted()) return;
		if (mSound.isPlaying()) return;

		mSound.setVolume(mVolume*mManager.getSoundVolume());
		mSound.setCycleCount(MediaPlayer.INDEFINITE);
		mSound.play();
	}

	@Override
	public void stop() {
		if (mSound == null) return;
		mSound.stop();
	}

	@Override
	public void setSpeed(float speed) {
		if (mSound == null) return;
		mSound.setRate(speed);
	}

	@Override
	public void setVolume(float volume) {
		if (mSound == null) return;
		mSound.setVolume(volume);
	}

	@Override
	public void setRepeatCount(int count) {
		if (mSound == null) return;
		mSound.setCycleCount(count);
	}

	@Override
	public void setBalance(float balance) {
		if (mSound == null) return;
		mSound.setBalance(balance);
	}
}