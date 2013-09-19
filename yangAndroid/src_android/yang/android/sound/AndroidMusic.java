package yang.android.sound;

import yang.sound.AbstractMusic;
import yang.sound.AbstractSoundManager;
import android.media.MediaPlayer;

public class AndroidMusic extends AbstractMusic {

	private MediaPlayer mMusic;
	private float mVolLeft;
	private float mVolRight;

	public AndroidMusic(MediaPlayer player, AbstractSoundManager mgr) {
		super(mgr);
		mMusic = player;
	}

	@Override
	public void play() {
		mMusic.setLooping(false);
		mMusic.setVolume(mVolLeft*mVolume*mManager.getMusicVolume(), mVolRight*mVolume*mManager.getMusicVolume());
		mMusic.start();
	}

	@Override
	public void playLoop() {
		mMusic.setLooping(true);
		mMusic.setVolume(mVolLeft*mVolume*mManager.getMusicVolume(), mVolRight*mVolume*mManager.getMusicVolume());
		mMusic.start();
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
		mMusic.seekTo(time*1000);
	}

	@Override
	public void setBalance(float balance) {
		mVolLeft = (1-balance)/2;
		mVolRight = (1+balance)/2;
	}
}
