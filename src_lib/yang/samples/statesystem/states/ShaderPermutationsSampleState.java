package yang.samples.statesystem.states;

import yang.graphics.defaults.programs.subshaders.LightSubShader;
import yang.graphics.defaults.programs.subshaders.NormalSubShader;
import yang.graphics.programs.permutations.BasicSubShader;
import yang.graphics.programs.permutations.ShaderPermutations;
import yang.graphics.programs.permutations.SubShader;
import yang.graphics.translator.Texture;
import yang.graphics.translator.glconsts.GLMasks;
import yang.samples.statesystem.SampleState;

public class ShaderPermutationsSampleState extends SampleState {

	private ShaderPermutations mShader1;
	private Texture mGrassTex,mCubeTex;
	
	@Override
	public void initGraphics() {
		SubShader[] subShaders = new SubShader[]{new BasicSubShader(true,true,true),new NormalSubShader(true),new LightSubShader()};

		mShader1 = mGraphics.addProgram(new ShaderPermutations(subShaders));
		mCubeTex = mGFXLoader.getImage("cube");
		mGrassTex = mGFXLoader.getImage("grass");
	}
	
	@Override
	protected void step(float deltaTime) {
		
	}

	@Override
	protected void draw() {
		mGraphics.clear(0, 0, 0.1f, GLMasks.DEPTH_BUFFER_BIT);
		mGraphics3D.activate();
		mGraphics3D.setWhite();
		mGraphics.switchZBuffer(true);
		mGraphics3D.setCameraAlphaBeta((float)mStateTimer*0.3f, 0.4f, 2);
		mGraphics3D.setPerspectiveProjection(100);
		mGraphics3D.setShaderProgram(mShader1);
		
		mGraphics.bindTexture(mGrassTex);
		mGraphics3D.drawSphere(32, 16, -0.5f, 0, 0, 0.65f, 2, 1);
		mGraphics.bindTexture(mCubeTex);
		mGraphics3D.drawCubeCentered(0.5f, 0, 0.5f, 0.5f);
		
		
	}

	
	
}
