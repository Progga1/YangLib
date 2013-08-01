package yang.samples.statesystem.states;

import yang.graphics.defaults.programs.subshaders.AmbientSubShader;
import yang.graphics.defaults.programs.subshaders.CameraPerVertexVectorSubShader;
import yang.graphics.defaults.programs.subshaders.DiffuseLightSubShader;
import yang.graphics.defaults.programs.subshaders.MtDiffuseSubShader;
import yang.graphics.defaults.programs.subshaders.NormalSubShader;
import yang.graphics.defaults.programs.subshaders.SpecularLightBasicSubShader;
import yang.graphics.defaults.programs.subshaders.properties.LightProperties;
import yang.graphics.defaults.programs.subshaders.properties.SpecularMatProperties;
import yang.graphics.defaults.programs.subshaders.realistic.LightSubShader;
import yang.graphics.model.FloatColor;
import yang.graphics.programs.permutations.BasicSubShader;
import yang.graphics.programs.permutations.ShaderPermutations;
import yang.graphics.programs.permutations.SubShader;
import yang.graphics.skeletons.Skeleton3D;
import yang.graphics.translator.glconsts.GLMasks;
import yang.graphics.util.Camera3D;
import yang.samples.statesystem.SampleState;

public class Skeleton3DSampleState extends SampleState {

	public Skeleton3D mSkeleton;
	public Camera3D mCamera;
	public ShaderPermutations mShader;
	private LightProperties mLight;
	
	@Override
	public void initGraphics() {
		mSkeleton = new Skeleton3D(mGraphics3D).initLines();
		mCamera = new Camera3D();
		mLight = new LightProperties();
		SubShader[] subShaders = new SubShader[]{
				new BasicSubShader(true,true,true),new NormalSubShader(true,true),
				new MtDiffuseSubShader(FloatColor.WHITE),
				new LightSubShader(mLight),new DiffuseLightSubShader(),
				new CameraPerVertexVectorSubShader(mCamera),new SpecularLightBasicSubShader(new SpecularMatProperties()),
				new AmbientSubShader(new FloatColor(0.3f))
				};
		//mShader = mGraphics.addProgram(new DefaultObjShader(mGraphics3D,mCamera,mLight,new FloatColor(0.3f)));
		mShader = mGraphics.addProgram(new ShaderPermutations(mGraphics,subShaders));
	}
	
	@Override
	protected void step(float deltaTime) {
		
	}

	@Override
	protected void draw() {
		mGraphics3D.activate();
		mGraphics3D.setPerspectiveProjection(10);
		mGraphics.clear(0f,0f,0.1f, GLMasks.DEPTH_BUFFER_BIT);
		mGraphics.switchZBuffer(true);
		
		mGraphics3D.setWhite();
		mGraphics3D.setAmbientColor(1);
		mGraphics.bindTexture(null);
		mGraphics3D.setShaderProgram(mShader);
		mLight.mDiffuse.set(1);
		mLight.mDirection.setAlphaBeta(0.4f, 0.4f);
		mCamera.setAlphaBeta((float)mStateTimer, 0.4f,2);
		mGraphics3D.setCamera(mCamera);
		mSkeleton.draw();

	}

}
