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
		mManager.setCurrentMusicTrack(this);
		if (mMusic == null) return;
		if (mManager.isMusicMuted()) return;
		mMusic.setVolume(mVolume*mManager.getMusicVolume());
		mMusic.setCycleCount(1);
		mMusic.play();
	}

	@Override
	public void playLoop() {
		mManager.setCurrentMusicTrack(this);
		if (mMusic == null) return;
		if (mManager.isMusicMuted()) return;
		mMusic.setVolume(mVolume*mManager.getMusicVolume());
		mMusic.setCycleCount(MediaPlayer.INDEFINITE);
		mMusic.play();
	}

	@Override
	public void stop() {
		if (mMusic == null) return;
		mMusic.stop();
	}

	@Override
	public void pause() {
		if (mMusic == null) return;
		mMusic.pause();
	}

	@Override
	public void seek(int time) {
		if (mMusic == null) return;
		mMusic.seek(Duration.seconds(time));
	}

	@Override
	public void setBalance(float balance) {
		if (mMusic == null) return;
		mMusic.setBalance(balance);
	}

}
