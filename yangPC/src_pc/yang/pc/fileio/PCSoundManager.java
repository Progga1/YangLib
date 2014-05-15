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
	private final JFXPanel fxPanel;

	public PCSoundManager() {
		//need to call this to initialize javafx
		fxPanel = new JFXPanel();
	}

	@Override
	public synchronized AbstractSound derivedLoadSound(String filename) {
		File file = ((PCResourceManager)mResources).getAssetFile(filename);
		AudioClip clip = new AudioClip(file.toURI().toString());
		return new PCSound(this, clip);
	}

	@Override
	protected AbstractMusic derivedLoadMusic(String filename) {
		File file = ((PCResourceManager)mResources).getAssetFile(filename);
		MediaPlayer player = new MediaPlayer(new Media(file.toURI().toString()));
		return new PCMusic(this, player);
	}

}
