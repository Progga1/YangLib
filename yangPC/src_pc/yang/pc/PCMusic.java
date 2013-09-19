package yang.pc;

import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import yang.sound.AbstractMusic;
import yang.sound.AbstractSoundManager;

public class PCMusic extends AbstractMusic {

	private MediaPlayer mMusic;

	public PCMusic(AbstractSoundManager mgr, MediaPlayer player) {
		super(mgr);
		mMusic = player;
	}

	@Override
	public void play() {
		if (mManager.isMusicMuted()) return;
		mMusic.setVolume(mVolume*mManager.getMusicVolume());
		mMusic.setCycleCount(1);
		mMusic.play();
	}

	@Override
	public void playLoop() {
		if (mManager.isMusicMuted()) return;
		mMusic.setVolume(mVolume*mManager.getMusicVolume());
		mMusic.setCycleCount(MediaPlayer.INDEFINITE);
		mMusic.play();
	}

	@Override
	public void stop() {
		mMusic.stop();
	}

	@Override
	public void pause() {
		mMusic.pause();
	}

	@Override
	public void seek(int time) {
		mMusic.seek(Duration.seconds(time));
	}

	@Override
	public void setBalance(float balance) {
		mMusic.setBalance(balance);
	}

}
