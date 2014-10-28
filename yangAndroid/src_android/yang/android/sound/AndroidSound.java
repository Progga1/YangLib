package yang.android.sound;

import yang.sound.AbstractSound;
import yang.sound.AbstractSoundManager;
import android.media.SoundPool;

public class AndroidSound extends AbstractSound {

	private SoundPool mSoundPool;
	private int mId;
	private int mPlayingId;
	private boolean mIsLoaded;

	public AndroidSound(AbstractSoundManager mgr, int id, SoundPool soundPool) {
		super(mgr);
		mId = id;
		mIsLoaded = false;
		mSoundPool = soundPool;
	}

	public void setLoaded() {
		mIsLoaded = true;
	}

	@Override
	public void stop() {
		if (mIsLoaded && mPlayingId != -1) mSoundPool.stop(mPlayingId);
	}

	public int getId() {
		return mId;
	}

	@Override
	public void play(float volume, float balance, float speed, int repeat) {
		if (mManager.isSoundMuted()) return;
		if (!mIsLoaded) return;

		float volLeft = (1-balance)/2;
		float volRight = (1+balance)/2;
		mPlayingId = mSoundPool.play(mId, volLeft*volume*mManager.getSoundVolume(), volRight*volume*mManager.getSoundVolume(), 1, repeat, speed);
	}
}
