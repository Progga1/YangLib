package yang.model;

import yang.graphics.translator.AbstractGFXLoader;
import yang.sound.AbstractSoundManager;
import yang.systemdependent.AbstractDataStorage;
import yang.systemdependent.AbstractResourceManager;
import yang.systemdependent.AbstractVibrator;
import yang.systemdependent.YangSensor;
import yang.systemdependent.YangSystemCalls;

public class App {

	public static AbstractDataStorage storage;
	public static AbstractSoundManager soundManager;
	public static AbstractGFXLoader gfxLoader;
	public static AbstractResourceManager resourceManager;
	public static AbstractVibrator vibrator;
	public static YangSensor sensor;
	public static YangSystemCalls systemCalls = new YangSystemCalls();

}
