package yang.pc.fileio;

import java.io.File;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import yang.model.PathSpecs;
import yang.pc.PCMusic;
import yang.pc.PCSound;
import yang.sound.AbstractMusic;
import yang.sound.AbstractSound;
import yang.sound.AbstractSoundManager;

public class PCSoundManager extends AbstractSoundManager {

	@SuppressWarnings("unused")
	private JFXPanel fxPanel;

	public PCSoundManager() {
		super();
		SOUND_PATH = PathSpecs.ASSET_PATH + SOUND_PATH;
		//need to call this to initialize javafx
		fxPanel = new JFXPanel();
	}

	@Override
	public synchronized AbstractSound loadSound(String name) {
		return new PCSound(this, new AudioClip(new File(SOUND_PATH+ name + SOUND_EXT).toURI().toString()));
	}

	@Override
	protected AbstractMusic loadMusic(String name) {
		return new PCMusic(this, new MediaPlayer(new Media(new File(SOUND_PATH+name+SOUND_EXT).toURI().toString())));
	}

}
