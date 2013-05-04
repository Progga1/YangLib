package yang.samples.statesystem;

import yang.graphics.defaults.Default2DGraphics;
import yang.graphics.defaults.Default3DGraphics;
import yang.graphics.translator.GraphicsTranslator;

public class SampleProgramStateSystem {

	private ProgramState mCurrentState;
	protected GraphicsTranslator mGraphics;
	protected Default2DGraphics mGraphics2D;
	protected Default3DGraphics mGraphics3D;
	
	public void init(Default2DGraphics graphics2D,Default3DGraphics graphics3D) {
		mGraphics = graphics2D.mTranslator;
		mGraphics2D = graphics2D;
		mGraphics3D = graphics3D;
	}
	
	public void setState(ProgramState newState) {
		if(mCurrentState!=null)
			mCurrentState.stop();
		mCurrentState = newState;
		mCurrentState.start();
	}
	
	public void step(float deltaTime) {
		if(mCurrentState!=null)
			mCurrentState.step(deltaTime);
	}
	
	public void draw() {
		if(mCurrentState!=null)
			mCurrentState.draw();
	}
	
}
