package yang.sound;

import java.io.File;
import java.util.HashMap;

import yang.model.DebugYang;
import yang.sound.nosound.NoMusic;
import yang.sound.nosound.NoSound;
import yang.systemdependent.AbstractResourceManager;

public abstract class AbstractSoundManager {

	protected boolean mSoundMute;
	protected boolean mMusicMute;
	protected AbstractResourceManager mResources;

	protected float mSoundVolume;
	protected float mMusicVolume;

	protected static final String[] SOUND_EXT 	= {".mp3",".wav"};
	protected String SOUND_PATH[] 				= {"sounds"+File.separatorChar};

	protected HashMap<String, AbstractSound> mSounds;
	protected HashMap<String, AbstractMusic> mMusics;

	protected abstract AbstractSound derivedLoadSound(String name);
	protected abstract AbstractMusic derivedLoadMusic(String name);

	public AbstractSoundManager() {
		if (DebugYang.showStart) DebugYang.showStackTrace("1", 1);
		mSounds = new HashMap<String, AbstractSound>();
		mMusics = new HashMap<String, AbstractMusic>();

		mSoundMute = false;
		mMusicMute = false;

		mSoundVolume = 1.0f;
		mMusicVolume = 1.0f;
	}

	public void init(AbstractResourceManager resources) {
		mResources = resources;
	}

	public String getSoundAssetFilename(String name) {
		return mResources.getAssetFilename(name, SOUND_PATH, SOUND_EXT);
	}

	protected AbstractSound loadSound(String name) {
		String filename = getSoundAssetFilename(name);
		if(filename==null) {
			DebugYang.printerr("sound not found: "+name, 1);
			return new NoSound(this);
		}else
			return derivedLoadSound(filename);
	}

	protected AbstractMusic loadMusic(String name) {
		String filename = getSoundAssetFilename(name);
		if(filename==null) {
			DebugYang.printerr("music not found: "+name, 1);
			return new NoMusic(this);
		}else
			return derivedLoadMusic(filename);
	}

	public AbstractSound getSound(String name, float volume) {
		AbstractSound sound = mSounds.get(name);
		if (sound != null)
			return sound;
		sound = loadSound(name);
		mSounds.put(name, sound);
		return sound;
	}

	public AbstractMusic getMusic(String name, float volume) {
		AbstractMusic music = mMusics.get(name);
		if (music != null)
			return music;
		music = loadMusic(name);
		mMusics.put(name, music);
		return music;
	}

	public AbstractSound getSound(String name) {
		return getSound(name, 1.0f);
	}

	public AbstractMusic getMusic(String name) {
		return getMusic(name, 1.0f);
	}

	public void setSoundMuted(boolean enabled) {
		mSoundMute = enabled;
	}

	public void setMusicMuted(boolean enabled) {
		mMusicMute = enabled;
	}

	public boolean isSoundMuted() {
		return mSoundMute;
	}

	public boolean isMusicMuted() {
		return mMusicMute;
	}

	public void setMusicVolume(float volume) {
		mMusicVolume = volume;
	}

	public void setSoundVolume(float volume) {
		mSoundVolume = volume;
	}

	public float getSoundVolume() {
		return mSoundVolume;
	}

	public float getMusicVolume() {
		return mMusicVolume;
	}
}