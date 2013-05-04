package yang.samples.statesystem;

import yang.graphics.defaults.Default2DGraphics;
import yang.graphics.defaults.Default3DGraphics;
import yang.graphics.translator.GraphicsTranslator;

public abstract class ProgramState {

	protected SampleProgramStateSystem mStateSystem;
	protected GraphicsTranslator mGraphics;
	protected Default2DGraphics mGraphics2D;
	protected Default3DGraphics mGraphics3D;
	
	public abstract void step(float deltaTime);
	public abstract void draw();
	
	public final ProgramState init(SampleProgramStateSystem stateSystem) {
		mGraphics = stateSystem.mGraphics2D.mTranslator;
		mGraphics2D = stateSystem.mGraphics2D;
		mGraphics3D = stateSystem.mGraphics3D;
		postInit();
		return this;
	}
	
	protected void postInit() {
		
	}
	
	protected void initGraphics() {
		
	}
	
	public void start() {
		
	}
	
	public void stop() {
		
	}
	
}
