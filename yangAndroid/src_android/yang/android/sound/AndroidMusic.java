package yang.android.sound;

import yang.sound.AbstractMusic;
import yang.sound.AbstractSoundManager;
import android.media.MediaPlayer;
import android.media.PlaybackParams;

public class AndroidMusic extends AbstractMusic {

	private MediaPlayer mMusic;
	private float mVolLeft;
	private float mVolRight;
	private PlaybackParams params;

	public AndroidMusic(MediaPlayer player, AbstractSoundManager mgr) {
		super(mgr);
		mMusic = player;
		setBalance(0);
		
		params = new PlaybackParams();
	}

	@Override
	public void play() {
		mManager.setCurrentMusicTrack(this);
		if (mManager.isMusicMuted()) return;
		if (mMusic == null) return;
		mMusic.setLooping(false);
		mMusic.setVolume(mVolLeft*mVolume*mManager.getMusicVolume(), mVolRight*mVolume*mManager.getMusicVolume());
		mMusic.start();
	}

	@Override
	public void playLoop() {
		mManager.setCurrentMusicTrack(this);
		if (mManager.isMusicMuted()) return;
		if (mMusic == null) return;
		mMusic.setLooping(true);
		mMusic.setVolume(mVolLeft*mVolume*mManager.getMusicVolume(), mVolRight*mVolume*mManager.getMusicVolume());
		mMusic.start();
	}

	@Override
	public void stop() {
		if (mMusic == null) return;
		mMusic.stop();
	}

	@Override
	public void pause() {
		if (mMusic == null || !mMusic.isPlaying()) return;
		mMusic.pause();
	}

	@Override
	public void seek(int time) {
		if(!mMusic.isPlaying()) return;
		if (mMusic == null) return;
		mMusic.seekTo(time*1000);
	}

	@Override
	public void setBalance(float balance) {
		mVolLeft = (1-balance)/2;
		mVolRight = (1+balance)/2;
	}
	
	@Override
	public boolean hasReachedEnd() {
		return !mMusic.isPlaying();
	}

	@Override
	public void setPlayrate(float playrate) {
		if (mMusic == null || !mMusic.isPlaying()) return;
		params.setSpeed(playrate);
		params.setPitch(playrate);
		mMusic.setPlaybackParams(params);
	}
}
