package yang.sound;

import java.io.File;
import java.util.HashMap;

import yang.model.DebugYang;

/**
 * To play a sound, use either:
 * 
 * 		sound.play(AbstractEffect.CRUMBLING);
 * 
 * 		AbstractEffect.CRUMBLING.play(); // do NOT use me!!!
 * 
 * The first form should be preferred, because it handles both
 * if the sound is not loaded and <code>AbstractSound.mute</code>.
 */

public abstract class SoundManager {

	protected boolean mute;
	protected boolean music;
	
	protected static final String SOUND_EXT 	= ".wav";
	protected String SOUND_PATH 				= "sounds" + File.separatorChar;
	
	protected HashMap<String, AbstractSound> mSounds;
	
	protected abstract AbstractSound loadSound(String name);

	public AbstractSound getSound(String name,float volume) {
		AbstractSound sound = mSounds.get(name);
		if (sound != null)
			return sound;
		sound = loadSound(name);
		sound.init(volume);
		mSounds.put(name, sound);
		return sound;
	}
	
	public AbstractSound getSound(String name) {
		return getSound(name,1.0f);
	}

	public SoundManager() {
		if (DebugYang.showStart) DebugYang.showStackTrace("1", 1);
		mSounds = new HashMap<String, AbstractSound>();
		mute = false;
		music = false;
	}

	public void toggleSound() {
		mute = !mute;
	}

	public void toggleMusic() {
		music = !music;
	}

	public void play(AbstractSound sound) {
		if (!mute && (sound != null)) sound.play();
	}

	public void playLoop(AbstractSound sound) {
		if (!mute && (sound != null)) sound.playLoop();
	}

	public void stopLoop(AbstractSound sound) {
		sound.stop();
	}

	public void enableSound(boolean enabled) {
		mute = !enabled;
	}
	
	public void enableMusic(boolean enabled) {
		music = !enabled;
	}
	
}