package yang.samples.statesystem.states;

import yang.graphics.programs.permutations.BasicSubShader;
import yang.graphics.programs.permutations.ShaderPermutations;
import yang.graphics.programs.permutations.SubShader;
import yang.graphics.translator.glconsts.GLMasks;
import yang.samples.statesystem.SampleState;

public class ShaderPermutationsSampleState extends SampleState {

	private ShaderPermutations mShader1;
	
	@Override
	public void initGraphics() {
		SubShader[] subShaders = new SubShader[]{new BasicSubShader(true,true,true)};

		mShader1 = mGraphics.addProgram(new ShaderPermutations(subShaders));
	}
	
	@Override
	protected void step(float deltaTime) {
		
	}

	@Override
	protected void draw() {
		mGraphics.clear(0, 0, 0.1f, GLMasks.DEPTH_BUFFER_BIT);
		mGraphics3D.activate();
		mGraphics.switchZBuffer(true);
		mGraphics3D.setCameraAlphaBeta((float)mStateTimer*0.3f, 0.4f, 2);
		mGraphics3D.setPerspectiveProjection(100);
		mGraphics3D.setShaderProgram(mShader1);
		mGraphics3D.drawSphere(32, 16, -0.5f, 0, 0, 0.8f, 1, 1);
		mGraphics3D.drawCubeCentered(0.5f, 0, 0.5f, 0.5f);
		
		
	}

	
	
}
