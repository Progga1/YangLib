package yang.pc.fileio;

import java.io.File;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import yang.pc.PCMusic;
import yang.pc.PCSound;
import yang.sound.AbstractMusic;
import yang.sound.AbstractSound;
import yang.sound.AbstractSoundManager;

public class PCSoundManager extends AbstractSoundManager {

	@SuppressWarnings("unused")
	private final JFXPanel fxPanel;
	private final PCResourceManager mResources;

	public PCSoundManager(PCResourceManager resources) {
		super();
		mResources = resources;
		//SOUND_PATH = resources.getAssetFile(SOUND_PATH).getAbsolutePath();
		//need to call this to initialize javafx
		fxPanel = new JFXPanel();
	}

	private File getSoundFile(String name) {
		File file = null;
		for(int i=0;i<SOUND_EXT.length;i++) {
			file = mResources.getAssetFile(SOUND_PATH+ name + SOUND_EXT[i]);
			if(file!=null && file.exists())
				return file;
		}
		return null;
	}

	@Override
	public synchronized AbstractSound loadSound(String name) {
		AudioClip clip = null;

		final File file = getSoundFile(name);
		if (file!=null) {System.out.println(file);
			clip = new AudioClip(file.toURI().toString());
		}
		return new PCSound(this, clip);
	}

	@Override
	protected AbstractMusic loadMusic(String name) {
		MediaPlayer player = null;

		final File file = getSoundFile(name);
		if (file!=null) {
			player = new MediaPlayer(new Media(file.toURI().toString()));
		}
		return new PCMusic(this, player);
	}

}
