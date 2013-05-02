package yang.android.sound;

import yang.sound.AbstractSound;
import android.media.SoundPool;

public class AndroidSound extends AbstractSound {

	private SoundPool mSoundPool;
	private int mId;
	private boolean mIsLoaded;
	private float mVolume;
	
	public AndroidSound(int id, SoundPool soundPool) {
		mId = id;
		mIsLoaded = false;
		mSoundPool = soundPool;
	}

	public void setLoaded() {
		mIsLoaded = true;
	}
	
	@Override
	public void play() {
		if (mIsLoaded) mSoundPool.play(mId, mVolume, mVolume, 1, 0, 1.0f);
	}
	
	@Override
	public void playLoop() {
		if (mIsLoaded) mSoundPool.play(mId, mVolume, mVolume, 1, -1, 1.0f);
	}

	@Override
	public void stopLoop() {
		if (mIsLoaded) mSoundPool.stop(mId);
	}

	@Override
	public boolean isLoaded() {
		return mIsLoaded;
	}

	public int getId() {
		return mId;
	}

	@Override
	public void init(float volume) {
		mVolume = volume;
	}

}
