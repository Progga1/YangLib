package yang.pc.fileio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import yang.model.PathSpecs;
import yang.pc.PCSound;
import yang.sound.AbstractSound;
import yang.sound.SoundManager;

public class PCSoundManager extends SoundManager {

	public PCSoundManager() {
		super();
		SOUND_PATH = PathSpecs.ASSET_PATH + SOUND_PATH;
	}
	
	@Override
	public synchronized AbstractSound loadSound(String name) {
		File file = new File(SOUND_PATH, name + SOUND_EXT);
		byte[] data = new byte[(int) file.length()];
		try {
			new FileInputStream(file).read(data);
		} catch (FileNotFoundException e) {
			System.err.println("**ERROR** Could not find '" + SOUND_PATH + name + SOUND_EXT + "'.");
			data = null;
		} catch (IOException e) {
			System.err.println("**ERROR** " + e.getMessage());
			e.printStackTrace();
			data = null;
		}
		return new PCSound(name, data);
	}
	
}
