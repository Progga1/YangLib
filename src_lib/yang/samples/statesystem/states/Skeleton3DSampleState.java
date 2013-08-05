package yang.samples.statesystem.states;

import yang.events.eventtypes.YangPointerEvent;
import yang.graphics.defaults.programs.subshaders.AmbientSubShader;
import yang.graphics.defaults.programs.subshaders.CameraPerVertexVectorSubShader;
import yang.graphics.defaults.programs.subshaders.DiffuseLightSubShader;
import yang.graphics.defaults.programs.subshaders.MtDiffuseSubShader;
import yang.graphics.defaults.programs.subshaders.NormalSubShader;
import yang.graphics.defaults.programs.subshaders.properties.LightProperties;
import yang.graphics.defaults.programs.subshaders.properties.SpecularMatProperties;
import yang.graphics.defaults.programs.subshaders.realistic.LightSubShader;
import yang.graphics.defaults.programs.subshaders.realistic.SpecularLightSubShader;
import yang.graphics.model.FloatColor;
import yang.graphics.programs.permutations.BasicSubShader;
import yang.graphics.programs.permutations.ShaderPermutations;
import yang.graphics.programs.permutations.SubShader;
import yang.graphics.skeletons.Skeleton3D;
import yang.graphics.translator.glconsts.GLMasks;
import yang.graphics.util.Camera3D;
import yang.samples.SampleSkeleton;
import yang.samples.statesystem.SampleState;

public class Skeleton3DSampleState extends SampleState {

	public Skeleton3D mSkeleton3D;
	public SampleSkeleton mSkeleton;
	public Camera3D mCamera;
	public ShaderPermutations mShader;
	private LightProperties mLight;
	private float mViewAlpha,mViewBeta;
	private float mPntX,mPntY;
	
	@Override
	public void initGraphics() {
		mSkeleton = new SampleSkeleton(mGraphics2D);
		mSkeleton3D = new Skeleton3D(mGraphics3D,mSkeleton).initLines();
		mCamera = new Camera3D();
		mLight = new LightProperties();
		SubShader[] subShaders = new SubShader[]{
				new BasicSubShader(true,true,true),new NormalSubShader(true,true),
				new MtDiffuseSubShader(FloatColor.WHITE),
				new LightSubShader(mLight),new DiffuseLightSubShader(),
				new CameraPerVertexVectorSubShader(mCamera),new SpecularLightSubShader(new SpecularMatProperties()),
				new AmbientSubShader(new FloatColor(0.3f))
				};
		//mShader = mGraphics.addProgram(new DefaultObjShader(mGraphics3D,mCamera,mLight,new FloatColor(0.3f)));
		mShader = mGraphics.addProgram(new ShaderPermutations(mGraphics,subShaders));
	}
	
	@Override
	protected void step(float deltaTime) {
		mSkeleton.mBreastJoint.mFixed = false;
		mSkeleton.recalculateConstraints();
		mSkeleton.applyConstraints(deltaTime);
	}

	@Override
	protected void draw() {
		mGraphics3D.activate();
		mGraphics3D.setPerspectiveProjection(10);
		mGraphics.clear(0f,0f,0.3f, GLMasks.DEPTH_BUFFER_BIT);
		mGraphics.switchZBuffer(true);
		
		mGraphics3D.setWhite();
		mGraphics3D.setAmbientColor(1);
		mGraphics.bindTexture(null);
		mGraphics3D.setShaderProgram(mShader);
		mLight.mDiffuse.set(0.5f);
		mLight.mDirection.setAlphaBeta((float)mStateTimer*0.1f, 0.4f);
		mCamera.setAlphaBeta(mViewAlpha,mViewBeta,2, 0,1,0);
		mGraphics3D.setCamera(mCamera);
		mSkeleton3D.draw();

		if(false) {
			mGraphics2D.activate();
			mGraphics.switchZBuffer(false);
			mGraphics2D.setCamera(0, 0.5f, 2);
			mSkeleton.refreshVisualVars();
			mSkeleton.draw();
		}
		
	}
	
	@Override
	public void keyDown(int code) {
		if(code=='p') {
			mSkeleton.mConstantForceY = -0.01f;
		}
	}
	
	@Override
	public void pointerDown(float x,float y,YangPointerEvent event) {
		mPntX = x;
		mPntY = y;
	}

	@Override
	public void pointerDragged(float x,float y,YangPointerEvent event) {
		float deltaX = x-mPntX;
		float deltaY = y-mPntY;
		if(event.mButton == YangPointerEvent.BUTTON_MIDDLE) {
			mViewAlpha -= deltaX*2;
			mViewBeta -= deltaY;
			final float MAX_BETA = PI/2-0.01f;
			if(mViewBeta<-MAX_BETA)
				mViewBeta = -MAX_BETA;
			if(mViewBeta>MAX_BETA)
				mViewBeta = MAX_BETA;
			
		}
		mPntX = x;
		mPntY = y;
	}
	
	@Override
	public void pointerUp(float x,float y,YangPointerEvent event) {
		
	}
	
}
