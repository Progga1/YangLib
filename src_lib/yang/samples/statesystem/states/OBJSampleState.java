package yang.samples.statesystem.states;

import java.io.IOException;

import yang.events.Keys;
import yang.events.eventtypes.YangPointerEvent;
import yang.graphics.defaults.meshcreators.loaders.OBJLoader;
import yang.graphics.defaults.meshcreators.loaders.ObjHandles;
import yang.graphics.defaults.programs.DefaultObjShader;
import yang.graphics.defaults.programs.LightProgram;
import yang.graphics.defaults.programs.subshaders.CameraPerVertexVectorSubShader;
import yang.graphics.defaults.programs.subshaders.SpecularLightSubShader;
import yang.graphics.defaults.programs.subshaders.ToonDiffuseSubShader;
import yang.graphics.defaults.programs.subshaders.ToonOutlineSubShader;
import yang.graphics.defaults.programs.subshaders.properties.LightProperties;
import yang.graphics.model.FloatColor;
import yang.graphics.programs.permutations.SubShader;
import yang.graphics.textures.TextureProperties;
import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.textures.enums.TextureWrap;
import yang.graphics.translator.glconsts.GLMasks;
import yang.graphics.util.Camera3D;
import yang.math.objects.matrix.YangMatrix;
import yang.model.wrappers.FloatWrapper;
import yang.samples.statesystem.SampleState;

public class OBJSampleState extends SampleState {

	private OBJLoader[] mObj = new OBJLoader[8];
	private LightProgram mLightProgram;
	private DefaultObjShader mActiveShader;
	private DefaultObjShader mObjProgram;
	private DefaultObjShader mToonObjProgram;
	private LightProperties mLightProperties;
	private FloatColor mAmbientColor;
	private int mCurObjIndex = 0;
	private Camera3D mCamera = new Camera3D();
	private int mObjCount = 0;
	
	@Override
	protected void initGraphics() {
		mLightProgram = mGraphics.addProgram(LightProgram.class);
		
		mAmbientColor = new FloatColor(0.3f,0.3f,0.3f);
		mLightProperties = new LightProperties();
		mObjProgram = mGraphics.addProgram(new DefaultObjShader(mGraphics3D,mCamera,mAmbientColor,mLightProperties));
		SubShader toonShader = new ToonDiffuseSubShader(mGFXLoader.getImage("toon_ramp1",new TextureProperties(TextureWrap.CLAMP,TextureFilter.LINEAR_MIP_LINEAR)));
		SubShader[] additionalShaders = new SubShader[]{toonShader,new CameraPerVertexVectorSubShader(mCamera),new SpecularLightSubShader(null),new ToonOutlineSubShader(new FloatWrapper(0.3f))};
		mToonObjProgram = mGraphics.addProgram(new DefaultObjShader(mGraphics3D,mAmbientColor,mLightProperties,additionalShaders));
		mActiveShader = mObjProgram;
		
		try {
			YangMatrix transform = new YangMatrix();
			
			ObjHandles handles = new ObjHandles(mObjProgram);
			mObjCount = -1;
			
			transform.loadIdentity();
			transform.scale(0.1f);
			mObj[++mObjCount] = new OBJLoader(mGraphics3D,handles);
			mObj[mObjCount].loadOBJ(mResources.getInputStream("models/cessna.obj"),mGFXLoader,transform,true,true);
			
			transform.loadIdentity();
			transform.scale(0.5f);
			mObj[++mObjCount] = new OBJLoader(mGraphics3D,handles,new TextureProperties(TextureWrap.REPEAT,TextureFilter.LINEAR_MIP_LINEAR));
			mObj[mObjCount].loadOBJ(mResources.getInputStream("models/peapodboat.obj"),mGFXLoader,transform,true,true);
			
			transform.loadIdentity();
			transform.translate(0, 0.3f);
			transform.rotateY((float)Math.PI/2);
			transform.rotateX(-0.3f);
			transform.scale(0.2f);
			mObj[++mObjCount] = new OBJLoader(mGraphics3D,handles);
			mObj[mObjCount].loadOBJ(mResources.getInputStream("models/supermario.obj"),mGFXLoader,transform,true,true);
			
			transform.loadIdentity();
			transform.scale(0.42f);	
			transform.translate(0, -0.85f);
			mObj[++mObjCount] = new OBJLoader(mGraphics3D,handles);
			mObj[mObjCount].loadOBJ(mResources.getInputStream("models/cutedog.obj"),mGFXLoader,transform,true,false);
			
			transform.loadIdentity();
			transform.scale(1.2f);
			transform.translate(0, -0.0f);
			mObj[++mObjCount] = new OBJLoader(mGraphics3D,handles);
			mObj[mObjCount].loadOBJ(mResources.getInputStream("models/scifi_hero.obj"),mGFXLoader,transform,true,true);
			
//			transform.loadIdentity();
//			transform.scale(0.5f);
//			transform.translate(-0.5f, -0.5f);
//			mObj[0] = new OBJLoader(mGraphics3D,handles);
//			mObj[0].loadOBJ(mResources.getInputStream("models/cubetest.obj"),mGFXLoader,transform);

			mObjCount++;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void start() {
		
	}
	@Override
	protected void step(float deltaTime) {
		
	}

	@Override
	protected void draw() {
		mGraphics.clear(0,0,0.1f,GLMasks.DEPTH_BUFFER_BIT);
		//mGraphics.clear(0.4f,0.4f,0.9f,GLMasks.DEPTH_BUFFER_BIT);
		mGraphics3D.activate();
		mGraphics3D.setWhite();
		mGraphics3D.setPerspectiveProjection(0.6f, 0.1f, 100);
		mGraphics3D.setCamera(mCamera.setAlphaBeta((float)(0*0.05f),0.4f,2));
		if(false) {
			mGraphics3D.setShaderProgram(mLightProgram);
			mLightProgram.setLightDirectionNormalized(0.407f, -0.207f, -0.407f);
			mLightProgram.setLightProperties(0.1f, 1, 0, 1);
		}else{
			mLightProperties.mDirection.setAlphaBeta((float)mStateTimer*2.6f, 0.5f);
			mGraphics3D.setShaderProgram(mActiveShader);
		}
		mGraphics.switchCulling(false);
		mGraphics.switchZBuffer(true);
		mGraphics3D.resetGlobalTransform();
		mGraphics3D.setGlobalTransformEnabled(true);
		mGraphics3D.mWorldTransform.rotateX(-(float)mStateTimer*0.004f);
		mGraphics3D.mWorldTransform.rotateY(-(float)mStateTimer*0.6f);
		mObj[mCurObjIndex].draw();
	}

	@Override
	public void pointerUp(float x,float y,YangPointerEvent event) {
		mCurObjIndex = (mCurObjIndex+1)%mObjCount;
	}
	
	@Override
	public void keyUp(int code) {
		super.keyUp(code);
		if(code==Keys.LEFT)
			mCurObjIndex--;
		if(code==Keys.RIGHT)
			mCurObjIndex = (mCurObjIndex+1)%mObjCount;
		if(mCurObjIndex<0)
			mCurObjIndex = mObjCount-1;
		if(code=='s') {
			if(mActiveShader==mObjProgram)
				mActiveShader = mToonObjProgram;
			else
				mActiveShader = mObjProgram;
		}
	}
	
	@Override
	public void stop() {
		mGraphics3D.resetGlobalTransform();
		mGraphics3D.setGlobalTransformEnabled(false);
		mGraphics3D.setAmbientColor(1);
	}
	
}
