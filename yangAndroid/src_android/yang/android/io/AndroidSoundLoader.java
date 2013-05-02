package yang.android.io;

import java.io.IOException;
import java.util.HashMap;

import yang.android.sound.AndroidSound;
import yang.android.sound.AndroidSoundManager;
import yang.model.App;
import yang.sound.AbstractSound;
import yang.sound.AbstractSoundLoader;
import android.content.Context;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

public class AndroidSoundLoader extends AbstractSoundLoader implements OnLoadCompleteListener {

	private HashMap<Integer, AndroidSound> mAndroidSounds;
	private Context mContext;
	
	public AndroidSoundLoader(Context context) {
		mAndroidSounds = new HashMap<Integer, AndroidSound>();
		mContext = context;
	}
	
	@Override
	public AbstractSound loadSound(String name) {
		AndroidSound sound = null;
		
		try {
			int sId = ((AndroidSoundManager)App.soundManager).getSoundPool().load(mContext.getAssets().openFd(SOUND_PATH + name + SOUND_EXT), 1);
			sound = new AndroidSound(sId, ((AndroidSoundManager)App.soundManager).getSoundPool());
			mSounds.put(name, sound);
			mAndroidSounds.put(sId, sound);
		} catch (IOException e) {
			throw new RuntimeException("Error reading resource: '"+name+"'");
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

}
