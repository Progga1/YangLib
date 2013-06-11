package yang.samples.statesystem.states;

import yang.graphics.translator.Texture;
import yang.graphics.translator.glconsts.GLMasks;
import yang.samples.statesystem.SampleState;

public class MatrixStackState extends SampleState {

	private Texture mCubeTex;
	
	@Override
	protected void initGraphics() {
		mCubeTex = mGFXLoader.getImage("cube");
	}
	
	@Override
	protected void step(float deltaTime) {
		
	}

	@Override
	protected void draw() {
//		mGraphics3D.activate();
//		mGraphics.clear(0,0,0,0,GLMasks.DEPTH_BUFFER_BIT);
//		mGraphics.bindTexture(mCubeTex);
//		mGraphics3D.drawCubeCentered(0, 0, 0, 1);
		
		mGraphics2D.activate();
		mGraphics.clear(0, 0, 0);
		mGraphics.bindTexture(mCubeTex);
		mGraphics2D.drawRectCentered(0, 0, 1);
	}

	
	
}
