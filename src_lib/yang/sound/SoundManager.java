package yang.sound;

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

public class SoundManager {

	protected boolean mute;
	protected boolean music;

	public SoundManager() {
		if (DebugYang.showStart) DebugYang.showStackTrace("1", 1);
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
		sound.stopLoop();
	}

	public void enableSound(boolean enabled) {
		mute = !enabled;
	}
	
	public void enableMusic(boolean enabled) {
		music = !enabled;
	}
	
}