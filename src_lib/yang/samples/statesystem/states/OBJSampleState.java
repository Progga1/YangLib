package yang.samples.statesystem.states;

import java.io.IOException;

import yang.events.Keys;
import yang.events.eventtypes.SurfacePointerEvent;
import yang.graphics.camera.Camera3D;
import yang.graphics.defaults.meshes.loaders.MeshMaterialHandles;
import yang.graphics.defaults.meshes.loaders.OBJLoader;
import yang.graphics.defaults.meshes.loaders.YangMesh;
import yang.graphics.defaults.programs.DefaultObjShader;
import yang.graphics.defaults.programs.LightProgram;
import yang.graphics.defaults.programs.subshaders.EmissiveSubShader;
import yang.graphics.defaults.programs.subshaders.dataproviders.CameraPerVertexVectorSubShader;
import yang.graphics.defaults.programs.subshaders.properties.LightProperties;
import yang.graphics.defaults.programs.subshaders.toon.ToonDiffuseSubShader;
import yang.graphics.defaults.programs.subshaders.toon.ToonOutlineSubShader;
import yang.graphics.defaults.programs.subshaders.toon.ToonRampSubShader;
import yang.graphics.defaults.programs.subshaders.toon.ToonSpecularLightSubShader;
import yang.graphics.model.FloatColor;
import yang.graphics.programs.permutations.SubShader;
import yang.graphics.textures.TextureProperties;
import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.textures.enums.TextureWrap;
import yang.graphics.translator.Texture;
import yang.graphics.translator.glconsts.GLMasks;
import yang.math.objects.YangMatrix;
import yang.model.wrappers.FloatWrapper;
import yang.samples.statesystem.SampleState;

public class OBJSampleState extends SampleState {

	private final YangMesh[] mObj = new YangMesh[8];
	private LightProgram mLightProgram;
	private DefaultObjShader mActiveShader;
	private DefaultObjShader mObjProgram;
	private DefaultObjShader mToonObjProgram;
	private LightProperties mLightProperties;
	private int mCurObjIndex = 0;
	private final Camera3D mCamera = new Camera3D();
	private int mObjCount = 0;
	private MeshMaterialHandles mMatHandles;
	private Texture mToonRamp1;
	private Texture mToonRamp2;
	private ToonRampSubShader mToonRampShader;

	@Override
	protected void initGraphics() {
		mLightProgram = mGraphics.addProgram(LightProgram.class);

		mLightProperties = new LightProperties();
		mObjProgram = mGraphics.addProgram(new DefaultObjShader(mGraphics3D,mLightProperties,new FloatColor(0.3f)));
		mToonRamp1 = mGFXLoader.getImage("toon_ramp1",new TextureProperties(TextureWrap.CLAMP,TextureFilter.LINEAR_MIP_LINEAR));
		mToonRamp2 = mGFXLoader.getImage("toon_ramp2",new TextureProperties(TextureWrap.CLAMP,TextureFilter.LINEAR_MIP_LINEAR));
		mToonRampShader = new ToonRampSubShader(mToonRamp1);
		final SubShader toonShader = new ToonDiffuseSubShader();
		final SubShader emisShader = new EmissiveSubShader(null);
		final SubShader[] additionalShaders = new SubShader[]{mToonRampShader,toonShader,new CameraPerVertexVectorSubShader(mGraphics3D),new ToonSpecularLightSubShader(null),emisShader,new ToonOutlineSubShader(new FloatWrapper(0.3f))};
		mToonObjProgram = mGraphics.addProgram(new DefaultObjShader(mGraphics3D,mLightProperties,new FloatColor(0.4f),false,additionalShaders));

		mActiveShader = mObjProgram;

		try {
			final YangMatrix transform = new YangMatrix();

			mMatHandles = new MeshMaterialHandles(mObjProgram);
			mObjCount = -1;

			OBJLoader loader = new OBJLoader(mGraphics3D,mMatHandles);

			transform.loadIdentity();
			transform.scale(0.1f);
			loader.loadOBJ(mResources.getAssetInputStream("models/cessna.obj"),transform,true,true);
			mObj[++mObjCount] = loader.getMesh();

			transform.loadIdentity();
			transform.scale(0.5f);
			loader.mTextureProperties = new TextureProperties(TextureWrap.REPEAT,TextureFilter.LINEAR_MIP_LINEAR);
			loader.loadOBJ(mResources.getAssetInputStream("models/PeaPodBoat.obj"),transform,true,true);
			mObj[++mObjCount] = loader.getMesh();
			loader.mTextureProperties = null;

			transform.loadIdentity();
			transform.translate(0, 0.3f);
			transform.rotateY((float)Math.PI/2);
			transform.rotateX(-0.3f);
			transform.scale(0.2f);
			loader.loadOBJ(mResources.getAssetInputStream("models/SuperMario.obj"),transform,true,true);
			mObj[++mObjCount] = loader.getMesh();

			transform.loadIdentity();
			transform.scale(0.42f);
			transform.translate(0, -0.85f);
			loader.loadOBJ(mResources.getAssetInputStream("models/cutedog.obj"),transform,true,false);
			mObj[++mObjCount] = loader.getMesh();

			transform.loadIdentity();
			transform.scale(1.2f);
			transform.translate(0, -0.0f);
			loader.loadOBJ(mResources.getAssetInputStream("models/scifi_hero.obj"),transform,true,true);
			mObj[++mObjCount] = loader.getMesh();

			mObjCount++;
		} catch (final IOException e) {
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

		mCamera.setLookAtAlphaBeta(0*0.05f,0.4f,2);
		mGraphics3D.setCamera(mCamera);
		mGraphics3D.setPerspectiveProjection(0.6f, 0.1f, 100);
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
	public void pointerUp(float x,float y,SurfacePointerEvent event) {
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
			mMatHandles.refreshHandles(mActiveShader);
		}
		if(code=='r') {
			if(mToonRampShader.mRampTex==mToonRamp1)
				mToonRampShader.mRampTex = mToonRamp2;
			else
				mToonRampShader.mRampTex = mToonRamp1;
		}
	}

	@Override
	public void stop() {
		mGraphics3D.resetGlobalTransform();
		mGraphics3D.setGlobalTransformEnabled(false);
		mGraphics3D.setColorFactor(1);
	}

}
