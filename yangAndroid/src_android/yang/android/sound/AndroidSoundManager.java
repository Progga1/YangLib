package yang.android.sound;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.HashMap;

import yang.sound.AbstractMusic;
import yang.sound.AbstractSound;
import yang.sound.AbstractSoundManager;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

public class AndroidSoundManager extends AbstractSoundManager implements OnLoadCompleteListener {

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
	public AbstractSound derivedLoadSound(String filename) {
		AndroidSound sound = null;
		int sId = -1;
		try {
			sId = mSoundPool.load(mContext.getAssets().openFd(filename), 1);
		} catch (IOException e) {
			sId = -1;
			System.err.println("failed loading sound: "+filename);
		}
		sound = new AndroidSound(this, sId, mSoundPool);
		mAndroidSounds.put(sId, sound);
		return sound;
	}

	@Override
	protected AbstractMusic derivedLoadMusic(String filename) {
		MediaPlayer player = new MediaPlayer();
		AndroidMusic music = null;
		try {
			FileDescriptor fd = mContext.getAssets().openFd(filename).getFileDescriptor();
			player.setDataSource(fd);
			player.prepareAsync();
		} catch (Exception e) {
			player.release();
			player = null;
			System.err.println("failed loading sound: "+filename);
		}

		music = new AndroidMusic(player, this);
		return music;
	}

	@Override
	public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
		if (status != 0) return;
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
