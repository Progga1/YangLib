package yang.samples.statesystem.states;

import java.io.IOException;

import yang.events.Keys;
import yang.events.eventtypes.YangPointerEvent;
import yang.graphics.defaults.meshcreators.loaders.OBJLoader;
import yang.graphics.defaults.meshcreators.loaders.ObjHandles;
import yang.graphics.defaults.programs.DefaultObjShader;
import yang.graphics.defaults.programs.LightProgram;
import yang.graphics.defaults.programs.subshaders.DiffuseLightSubShader;
import yang.graphics.defaults.programs.subshaders.ToonDiffuseSubShader;
import yang.graphics.defaults.programs.subshaders.properties.LightProperties;
import yang.graphics.model.FloatColor;
import yang.graphics.programs.permutations.SubShader;
import yang.graphics.textures.TextureProperties;
import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.textures.enums.TextureWrap;
import yang.graphics.translator.glconsts.GLMasks;
import yang.graphics.util.Camera3D;
import yang.math.objects.matrix.YangMatrix;
import yang.samples.statesystem.SampleState;

public class OBJSampleState extends SampleState {

	private OBJLoader[] mObj = new OBJLoader[4];
	private LightProgram mLightProgram;
	private DefaultObjShader mActiveShader;
	private DefaultObjShader mObjProgram;
	private DefaultObjShader mToonObjProgram;
	private LightProperties mLightProperties;
	private FloatColor mAmbientColor;
	private int mCurObjIndex = 0;
	private Camera3D mCamera = new Camera3D();
	
	@Override
	protected void initGraphics() {
		mLightProgram = mGraphics.addProgram(LightProgram.class);
		
		mAmbientColor = new FloatColor(0.2f,0.2f,0.2f);
		mLightProperties = new LightProperties();
		mObjProgram = mGraphics.addProgram(new DefaultObjShader(mGraphics3D,mCamera,mAmbientColor,mLightProperties,new DiffuseLightSubShader()));
		SubShader toonShader = new ToonDiffuseSubShader(mGraphics,mGFXLoader.getImage("toon_ramp1",new TextureProperties(TextureWrap.CLAMP,TextureFilter.LINEAR_MIP_LINEAR)));
		mToonObjProgram = mGraphics.addProgram(new DefaultObjShader(mGraphics3D,mCamera,mAmbientColor,mLightProperties,toonShader));
		mActiveShader = mObjProgram;
		
		try {
			YangMatrix transform = new YangMatrix();
			
			transform.loadIdentity();
			transform.scale(0.1f);
			ObjHandles handles = new ObjHandles(mObjProgram);
			mObj[0] = new OBJLoader(mGraphics3D,handles);
			mObj[0].loadOBJ(mResources.getInputStream("models/cessna.obj"),mGFXLoader,transform);
			
			transform.loadIdentity();
			transform.scale(0.5f);
			mObj[1] = new OBJLoader(mGraphics3D,handles);
			mObj[1].loadOBJ(mResources.getInputStream("models/peapodboat.obj"),mGFXLoader,transform);
			
			transform.loadIdentity();
			transform.translate(0, 0.3f);
			transform.rotateY((float)Math.PI/2);
			transform.rotateX(-0.3f);
			transform.scale(0.2f);
			mObj[2] = new OBJLoader(mGraphics3D,handles);
			mObj[2].loadOBJ(mResources.getInputStream("models/supermario.obj"),mGFXLoader,transform);
			
			mObj[3] = new OBJLoader(mGraphics3D,handles);
			mObj[3].loadOBJ(mResources.getInputStream("models/cube.obj"),mGFXLoader,null);
			
			for(OBJLoader obj:mObj) {
				obj.computeStaticNormals();
			}
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
		mGraphics3D.setCamera(mCamera.setAlphaBeta((float)(0*0.05f),0.45f,2));
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
		mCurObjIndex = (mCurObjIndex+1)%mObj.length;
	}
	
	@Override
	public void keyUp(int code) {
		super.keyUp(code);
		if(code==Keys.LEFT)
			mCurObjIndex--;
		if(code==Keys.RIGHT)
			mCurObjIndex = (mCurObjIndex+1)%mObj.length;
		if(mCurObjIndex<0)
			mCurObjIndex = mObj.length-1;
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
