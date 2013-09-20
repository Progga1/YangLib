package yang.android.sound;

import yang.sound.AbstractSound;
import yang.sound.AbstractSoundManager;
import android.media.SoundPool;

public class AndroidSound extends AbstractSound {

	private SoundPool mSoundPool;
	private int mId;
	private int mPlayingId;
	private boolean mIsLoaded;
	private float mVolLeft;
	private float mVolRight;
	private float mSpeed;
	private int mRepeatCount;

	public AndroidSound(AbstractSoundManager mgr, int id, SoundPool soundPool) {
		super(mgr);
		setBalance(0.0f);
		mId = id;
		mIsLoaded = false;
		mSoundPool = soundPool;
	}

	public void setLoaded() {
		mIsLoaded = true;
	}

	@Override
	public void play() {
		if (mManager.isSoundMuted()) return;
		if (!mIsLoaded) return;

		mPlayingId = mSoundPool.play(mId, mVolLeft*mVolume*mManager.getSoundVolume(), mVolRight*mVolume*mManager.getSoundVolume(), 1, mRepeatCount, mSpeed);
	}

	@Override
	public void playLoop() {
		if (mManager.isSoundMuted()) return;
		if (!mIsLoaded) return;

		mPlayingId =  mSoundPool.play(mId, mVolLeft*mVolume*mManager.getSoundVolume(), mVolRight*mVolume*mManager.getSoundVolume(), 1, -1, mSpeed);
	}

	@Override
	public void stop() {
		if (mIsLoaded && mPlayingId != -1) mSoundPool.stop(mPlayingId);
	}

	public int getId() {
		return mId;
	}

	@Override
	public void setSpeed(float speed) {
		mSpeed = speed;
	}

	@Override
	public void setRepeatCount(int count) {
		mRepeatCount = count;
	}

	@Override
	public void setBalance(float balance) {
		mVolLeft = (1-balance)/2;
		mVolRight = (1+balance)/2;
	}
}
