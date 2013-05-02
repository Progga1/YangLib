package yang.pc.gles;

import yang.graphics.SurfaceInterface;
import yang.graphics.defaults.Default2DGraphics;

public class FullGraphicsInitializer {

	protected PCGL2ES2Graphics mTranslator;
	public Default2DGraphics mGraphics2D;
	
	public FullGraphicsInitializer(SurfaceInterface surface,int resolutionX,int resolutionY) {
		mTranslator = new PCGL2ES2Graphics(resolutionX,resolutionY);
		mTranslator.setSurface(surface);
		surface.setGraphics(mTranslator);
		mGraphics2D = new Default2DGraphics(mTranslator);
	}
	
}
