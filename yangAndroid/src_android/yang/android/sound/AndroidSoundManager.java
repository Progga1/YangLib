package yang.android.sound;

import java.io.IOException;
import java.util.HashMap;

import yang.model.App;
import yang.sound.AbstractSound;
import yang.sound.SoundManager;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

public class AndroidSoundManager extends SoundManager implements OnLoadCompleteListener {

	private static final int MAX_SIMULTANEOUS_SOUNDS = 10;
	
	private SoundPool mSoundPool;
	
	private HashMap<Integer, AndroidSound> mAndroidSounds;
	private Context mContext;
	
	public AndroidSoundManager(Context context) {
		super();
		mAndroidSounds = new HashMap<Integer, AndroidSound>();
		mContext = context;
		mSoundPool = new SoundPool(MAX_SIMULTANEOUS_SOUNDS, AudioManager.STREAM_MUSIC, 0);
		mSoundPool.setOnLoadCompleteListener(this);
	}
	
	@Override
	public AbstractSound loadSound(String name) {
		AndroidSound sound = null;
		
		try {
			int sId = mSoundPool.load(mContext.getAssets().openFd(SOUND_PATH + name + SOUND_EXT), 1);
			sound = new AndroidSound(sId, mSoundPool);
			mSounds.put(name, sound);
			mAndroidSounds.put(sId, sound);
		} catch (IOException e) {
			//throw new RuntimeException("Error reading resource: '"+name+"'");
			System.err.println("Error reading resource: '"+name+"'");
		}
	
		return sound;
	}
	
	public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
		AndroidSound sound = null;
		while ((sound = mAndroidSounds.get(sampleId)) == null) {
			try {Thread.sleep(30); } catch (Exception e) {};
		}
		
		sound.setLoaded();
	}
	
	public SoundPool getSoundPool() {
		return mSoundPool;
	}
	
}
