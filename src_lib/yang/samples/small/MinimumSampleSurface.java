package yang.samples.small;

import yang.graphics.defaults.DefaultSurface;

public class MinimumSampleSurface extends DefaultSurface {

	public MinimumSampleSurface() {
		super(true, false);
	}
	
	@Override
	public void draw() {
		mGraphics.clear(0, 0, 0.1f);
		mGraphics2D.drawRect(0,0,0.5f,0.5f);
	}
	
}