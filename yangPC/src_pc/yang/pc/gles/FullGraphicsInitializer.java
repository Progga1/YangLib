package yang.pc.gles;

import yang.graphics.SurfaceInterface;
import yang.model.App;
import yang.model.Factory;
import yang.pc.PCFactory;
import yang.pc.fileio.PCDataStorage;
import yang.pc.fileio.PCResourceManager;
import yang.pc.fileio.PCSoundLoader;
import yang.sound.SoundManager;

public class FullGraphicsInitializer {

	protected PCGL2ES2Graphics mTranslator;
	
	public FullGraphicsInitializer() {
		
	}
	
	public FullGraphicsInitializer init(int resolutionX,int resolutionY) {
		mTranslator = new PCGL2ES2Graphics(resolutionX,resolutionY);
		Factory.init(new PCFactory());
		
		App.soundManager = new SoundManager();
		App.storage = new PCDataStorage();
		App.soundLoader = new PCSoundLoader();
		App.gfxLoader = mTranslator.mGFXLoader;
		App.resourceManager = new PCResourceManager();
		return this;
	}
	
	public FullGraphicsInitializer setSurface(SurfaceInterface surface) {
		mTranslator.setSurface(surface);
		surface.setGraphics(mTranslator);
		
		return this;
	}
	
}
