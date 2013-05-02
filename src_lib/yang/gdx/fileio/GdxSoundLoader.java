package yang.gdx.fileio;

import yang.gdx.sound.GdxSound;
import yang.sound.AbstractSound;
import yang.sound.AbstractSoundLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;


public class GdxSoundLoader extends AbstractSoundLoader {

	@Override
	protected AbstractSound loadSound(String name) {
		Sound sound = null;
		try {
			sound = Gdx.audio.newSound(Gdx.files.internal(SOUND_PATH + name + SOUND_EXT));
		} catch (Exception e) {
			System.err.println("could not load sound: " + name);
		}
		
		return new GdxSound(sound);
	}

}
