package yang.sound;

import java.io.File;
import java.util.HashMap;

public abstract class AbstractSoundLoader {

	protected static final String SOUND_EXT 	= ".wav";
	protected String SOUND_PATH 				= "sounds" + File.separatorChar;
	
	protected HashMap<String, AbstractSound> mSounds;
	
	public AbstractSoundLoader() {
		mSounds = new HashMap<String, AbstractSound>();
	}
	
	protected abstract AbstractSound loadSound(String name);

	public AbstractSound getSound(String name) {
		AbstractSound sound = mSounds.get(name);
		if (sound != null)
			return sound;
		sound = loadSound(name);
		mSounds.put(name, sound);
		return sound;
	}
	
}
