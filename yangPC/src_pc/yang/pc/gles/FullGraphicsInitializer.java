package yang.pc.gles;

import yang.model.App;
import yang.pc.PCSensorFrame;
import yang.pc.PCSystemCalls;
import yang.pc.PCVibrator;
import yang.pc.fileio.PCDataStorage;
import yang.pc.fileio.PCResourceManager;
import yang.pc.fileio.PCSoundManager;
import yang.surface.YangSurface;

public class FullGraphicsInitializer {

	protected PCGL2ES2Graphics mTranslator;

	public FullGraphicsInitializer() {

	}

	public FullGraphicsInitializer init(int resolutionX,int resolutionY) {
		mTranslator = new PCGL2ES2Graphics(resolutionX,resolutionY);


		if(App.sensor==null)
			App.sensor = new PCSensorFrame();
		App.storage = new PCDataStorage();
		App.gfxLoader = mTranslator.mGFXLoader;
		App.resourceManager = new PCResourceManager();
		if(App.soundManager==null)
			App.soundManager = new PCSoundManager((PCResourceManager)App.resourceManager);
		App.vibrator = new PCVibrator();
		App.systemCalls = new PCSystemCalls();
		return this;
	}

	public FullGraphicsInitializer setSurface(YangSurface surface) {
		mTranslator.setSurface(surface);
		surface.setGraphics(mTranslator);

		return this;
	}

}
