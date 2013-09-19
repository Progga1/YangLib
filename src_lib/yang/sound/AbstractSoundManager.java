package yang.sound;

import java.util.HashMap;

import yang.model.DebugYang;

public abstract class AbstractSoundManager {

	protected boolean mSoundMute;
	protected boolean mMusicMute;

	protected float mSoundVolume;
	protected float mMusicVolume;

	protected static final String SOUND_EXT 	= ".mp3";
	protected String SOUND_PATH 				= "sounds/";

	protected HashMap<String, AbstractSound> mSounds;
	protected HashMap<String, AbstractMusic> mMusics;

	protected abstract AbstractSound loadSound(String name);
	protected abstract AbstractMusic loadMusic(String name);

	public AbstractSoundManager() {
		if (DebugYang.showStart) DebugYang.showStackTrace("1", 1);
		mSounds = new HashMap<String, AbstractSound>();
		mSoundMute = false;
		mMusicMute = false;

		mSoundVolume = 1.0f;
		mMusicVolume = 1.0f;
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