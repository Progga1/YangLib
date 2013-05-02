package yang.gdx.sound;

import yang.sound.AbstractSound;

import com.badlogic.gdx.audio.Sound;

public class GdxSound extends AbstractSound {

	private boolean mIsLoaded;
	private Sound mSound;
	private float mVolume;
	
	public GdxSound(Sound sound) {
		mSound = sound;
		mIsLoaded = true;
	}
	
	@Override
	public void init(float volume) {
		mVolume = 0.5f;		//XXX sound hack
	}

	@Override
	public void play() {
		if (mSound != null) mSound.play(mVolume);
	}

	@Override
	public void playLoop() {
		
	}

	@Override
	public void stopLoop() {
		
	}

	@Override
	public boolean isLoaded() {
		return mIsLoaded;
	}

}
