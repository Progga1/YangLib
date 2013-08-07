package yang.samples.statesystem.states;

import yang.graphics.defaults.programs.subshaders.AmbientSubShader;
import yang.graphics.defaults.programs.subshaders.CameraVectorSubShader;
import yang.graphics.defaults.programs.subshaders.DiffuseLightSubShader;
import yang.graphics.defaults.programs.subshaders.NormalSubShader;
import yang.graphics.defaults.programs.subshaders.properties.LightProperties;
import yang.graphics.defaults.programs.subshaders.properties.SpecularMatProperties;
import yang.graphics.defaults.programs.subshaders.realistic.LightSubShader;
import yang.graphics.defaults.programs.subshaders.realistic.SpecularLightSubShader;
import yang.graphics.model.FloatColor;
import yang.graphics.programs.permutations.BasicSubShader;
import yang.graphics.programs.permutations.ShaderPermutations;
import yang.graphics.programs.permutations.SubShader;
import yang.graphics.translator.Texture;
import yang.graphics.translator.glconsts.GLMasks;
import yang.samples.statesystem.SampleState;

public class ShaderPermutationsSampleState extends SampleState {

	private ShaderPermutations mShader1;
	private LightProperties mLightProperties;
	private SpecularMatProperties mSpecularProperties;
	private FloatColor mAmbientColor;
	private Texture mGrassTex,mCubeTex;
	
	@Override
	public void initGraphics() {
		mLightProperties = new LightProperties();
		mSpecularProperties = new SpecularMatProperties();
		mAmbientColor = new FloatColor(0.1f,0.1f,0.12f);
		SubShader[] subShaders = new SubShader[]{
				new BasicSubShader(true,true,true),new NormalSubShader(true,true),
				new LightSubShader(mLightProperties),new DiffuseLightSubShader(),
				new CameraVectorSubShader(mGraphics3D.mCameraMatrix.mMatrix),new SpecularLightSubShader(mSpecularProperties),
				new AmbientSubShader(mAmbientColor)
				};

		mShader1 = mGraphics.addProgram(new ShaderPermutations(mGraphics,subShaders));
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
		mGraphics.switchCulling(true);
		mGraphics3D.setCameraAlphaBeta((float)mStateTimer*0.3f, 0.4f, 2);
		mGraphics3D.setPerspectiveProjection(100);
		mGraphics3D.setShaderProgram(mShader1);
		
		mLightProperties.mDirection.setAlphaBeta((float)mStateTimer,0.5f);
		mGraphics.bindTexture(mGrassTex);
		mGraphics3D.drawSphere(32, 16, -0.5f, 0, 0, 0.65f, 2, 1);
		mGraphics.bindTexture(mCubeTex);
		mGraphics3D.drawCubeCentered(0.5f, 0, 0.5f, 0.5f);
	}

	
	
}
