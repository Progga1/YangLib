package yang.android.sound;

import yang.android.io.AndroidSoundLoader;
import yang.sound.AbstractSoundLoader;
import yang.sound.SoundManager;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class AndroidSoundManager extends SoundManager {

	private static final int MAX_SIMULTANEOUS_SOUNDS = 10;
	
	private SoundPool mSoundPool;
	
	public AndroidSoundManager(Context context) {
		super();
		mSoundPool = new SoundPool(MAX_SIMULTANEOUS_SOUNDS, AudioManager.STREAM_MUSIC, 0);
	}

	public void init(AbstractSoundLoader soundLoader) {
		mSoundPool.setOnLoadCompleteListener((AndroidSoundLoader)soundLoader);
	}
	
	public SoundPool getSoundPool() {
		return mSoundPool;
	}
	
}
