package yang.model;

import yang.graphics.AbstractGFXLoader;
import yang.sound.AbstractSoundLoader;
import yang.sound.SoundManager;
import yang.systemdependent.AbstractDataStorage;
import yang.systemdependent.AbstractResourceManager;

public class App {
	
	public static AbstractDataStorage storage;
	public static SoundManager soundManager;
	public static ExitCallback exit;
	public static AbstractSoundLoader soundLoader;
	public static AbstractGFXLoader gfxLoader;
	public static AbstractResourceManager resourceManager;
	
}
