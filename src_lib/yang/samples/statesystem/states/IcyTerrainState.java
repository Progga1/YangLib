package yang.samples.statesystem.states;

import yang.graphics.buffers.DrawBatch;
import yang.graphics.camera.Camera3D;
import yang.graphics.defaults.geometrycreators.grids.TerrainCreator;
import yang.graphics.defaults.kernels.SqrtKernel;
import yang.graphics.defaults.particles.DefaultParticles3D;
import yang.graphics.defaults.programs.BillboardProgram;
import yang.graphics.defaults.programs.DepthProgram;
import yang.graphics.defaults.programs.LightProgram;
import yang.graphics.defaults.programs.LightmapCreatorProgram;
import yang.graphics.defaults.programs.LightmapProgram;
import yang.graphics.defaults.programs.ShadowProgram;
import yang.graphics.defaults.programs.SpecularLightmapProgram;
import yang.graphics.defaults.programs.WaterProgram;
import yang.graphics.defaults.programs.helpers.PlanarLightmapHelper;
import yang.graphics.defaults.programs.helpers.ShadowHelper;
import yang.graphics.particles.EffectParticleProperties;
import yang.graphics.particles.Weather3D;
import yang.graphics.textures.TextureProperties;
import yang.graphics.textures.TextureRenderTarget;
import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.textures.enums.TextureWrap;
import yang.graphics.translator.Texture;
import yang.graphics.translator.glconsts.GLMasks;
import yang.math.objects.Vector3f;
import yang.math.objects.YangMatrix;
import yang.model.Boundaries3D;
import yang.samples.statesystem.SampleState;

public class IcyTerrainState extends SampleState {

	private static int PATCHES_X = 64;
	private static int PATCHES_Y = 64;
	private static int WATER_PATCHES_X = 8;
	private static int WATER_PATCHES_Y = 8;
	private static final int COAST_KERNELSIZE = 3;
	private static final float PI = 3.141592653589f;
	private static final float GLOBAL_SHIFT = 1f;
	private static boolean STATIC_SHADOWS = false;
	private static boolean USE_LIGHTMAPS = true;
	private static boolean ENVIRONMENT_MAPPING = true;
	private static TextureProperties TEXTURE_PROPERTIES = new TextureProperties(TextureFilter.LINEAR_MIP_LINEAR);

	private Texture grass;
	private Texture ice;
	private Texture sky;
	private Texture spark;
	private Texture waterTex;
	private Texture waterNormal;
	private Texture mCubeTex;
	private Texture screenShotTex;
	private YangMatrix transfMatrix;
	private YangMatrix cullMatrix;
	private float[][] heights;

	private TerrainCreator mTerrain;
	private Weather3D<DefaultParticles3D> mWeather;

	private LightProgram mLightProgram = new LightProgram();
	private BillboardProgram mBillboardProgram = new BillboardProgram();
	private DepthProgram mDepthProgram = new DepthProgram();
	private ShadowProgram mShadowProgram = new ShadowProgram();
	private LightmapCreatorProgram mLightmapCreatorProgram = new LightmapCreatorProgram();
	private LightmapProgram mLightmapProgram = new LightmapProgram();
	private WaterProgram mWaterProgram = new WaterProgram();
	private SpecularLightmapProgram mSpecularProgram = new SpecularLightmapProgram();

	private ShadowHelper mShadowHelper = new ShadowHelper();
	private PlanarLightmapHelper mLightmapHelper = new PlanarLightmapHelper();
	private Texture mHeightTexture;
	private TextureRenderTarget mEnvironmentMap;

	private DrawBatch mTerrainBatch = null;
	private DrawBatch mTerrainBatchLowPoly = null;
	private DrawBatch mWaterBatch = null;
	private DrawBatch mSkyBoxBatch = null;

	private Camera3D mCamera = new Camera3D();
	private float mWorldAngle = 0;
	private Vector3f mWorldShift = new Vector3f();
	private float mCubeTop = 1;
	private float mCubeShiftX = 0;

	float terrainDimX = 3.5f;
	float terrainDimY = 4.7f;

	public IcyTerrainState() {
		heights = new float[PATCHES_Y][PATCHES_X];
		for(int i=0;i<PATCHES_Y;i++) {
			for(int j=0;j<PATCHES_X;j++) {
				float x = (float)j/PATCHES_X;
				float y = (float)i/PATCHES_Y;
				float r = (float)Math.sqrt((x-0.5f)*(x-0.5f)+(y-0.5f)*(y-0.5f));
				float h=(float)Math.pow(10, -r)*1.5f;
				heights[i][j] = (float)(Math.cos(14*x+5*y)*Math.sin(15f*y+PI/2)*h)-0.1f;
				if(heights[i][j]<0)
					heights[i][j] *=2.5f;
				heights[i][j] *= 1.9f;
			}
		}
	}

	@Override
	public void initGraphics() {
		grass = mGraphics.mGFXLoader.getImage("grass",TEXTURE_PROPERTIES);
		waterNormal = mGraphics.mGFXLoader.getImage("water_normal",TEXTURE_PROPERTIES);
		mCubeTex = mGraphics.mGFXLoader.getImage("cube",TEXTURE_PROPERTIES);
		spark = mGraphics.mGFXLoader.getAlphaMap("light_alpha",TEXTURE_PROPERTIES);
		ice = mGraphics.mGFXLoader.getImage("ice1",TEXTURE_PROPERTIES);
		waterTex = mGraphics.mGFXLoader.getImage("sky",TEXTURE_PROPERTIES);
		sky = mGraphics.mGFXLoader.getImage("sky",TEXTURE_PROPERTIES);
		mGraphics3D.setOrthogonalProjection(-1, 10);
		transfMatrix = mGraphics.createTransformationMatrix();
		cullMatrix = mGraphics.createTransformationMatrix();
		cullMatrix.loadIdentity();
		cullMatrix.rotateX(PI/2);
		cullMatrix.scale(terrainDimX,terrainDimY);
		cullMatrix.translate(-0.5f, -0.5f);

		mGraphics.addProgram(mLightProgram);
		mGraphics.addProgram(mLightmapCreatorProgram);
		mGraphics.addProgram(mLightmapProgram);
		mGraphics.addProgram(mSpecularProgram);
		mGraphics.addProgram(mBillboardProgram);
		mGraphics.addProgram(mDepthProgram);
		mGraphics.addProgram(mShadowProgram);
		mGraphics.addProgram(mWaterProgram);

		mTerrain = new TerrainCreator(mGraphics3D);
		mWeather = new Weather3D<DefaultParticles3D>(new Boundaries3D(-2,2, 0,2, -2,2));
		EffectParticleProperties particleProperties = new EffectParticleProperties(0.06f,0.1f, 0.96f,1.2f, 6,9);
		particleProperties.setVelocityDirection(0, -1, 0, true);
		DefaultParticles3D particles3D = new DefaultParticles3D();
		particles3D.init(mGraphics3D,320);
		particles3D.mTexture = spark;
		mWeather.init(particles3D,particleProperties);
		mWeather.createRandomParticles(50);
	}

	private void drawShadCube() {
		mGraphics.bindTexture(mCubeTex);
		float r = 0.5f;
		double t = STATIC_SHADOWS?0:mStateTimer;
		mGraphics.switchCulling(true);
		mGraphics.setCullMode(true);
		mGraphics3D.setColor(1, 1, 1, 0.3f);
		mGraphics3D.drawCubeCentered((float)Math.sin(t)*r+mCubeShiftX, mCubeTop, (float)Math.sin(2*t)*r, 0.5f);
		mGraphics3D.fillNormals();
		mGraphics3D.flush();
		mGraphics.setCullMode(false);
		mGraphics3D.setColor(1, 1, 1, 0.5f);
		mGraphics3D.drawCubeCentered((float)Math.sin(t)*r+mCubeShiftX, mCubeTop, (float)Math.sin(2*t)*r, 0.5f);
		mGraphics3D.fillNormals();
		mGraphics3D.flush();
		mGraphics.setCullMode(false);
		mGraphics.switchCulling(true);
	}

	private void renderDepthImage() {
		mShadowHelper.beginDepthRendering();
		mGraphics.switchZBuffer(true);
		mGraphics3D.setOrthogonalProjection(0, 7, 3.5f);
		mShadowHelper.setLightSource(-1.5f*(float)Math.sin(mStateTimer*0.1f),2.0f-(float)Math.sin(mStateTimer*0.1f),1.5f*(float)Math.cos(mStateTimer*0.1f), 0,0.5f,0, true);
		if(terrainEnabled())
			mTerrainBatchLowPoly.draw();
		drawShadCube();
		mShadowHelper.endDepthRendering();
	}

	private void createLightmap() {
		mLightmapHelper.beginRender();
		if(terrainEnabled())
			mTerrainBatch.draw();
		mWaterBatch.draw();
		mLightmapHelper.finishRender();
	}

	private void drawSky() {
		mGraphics.switchZBuffer(false);
		mGraphics.switchCulling(false);
		mGraphics3D.setDefaultProgram();
		mGraphics3D.setColorFactor(1);
		mGraphics.bindTexture(sky);
		mSkyBoxBatch.draw();
		mGraphics.switchZBuffer(true);
		mGraphics.switchCulling(true);
	}

	private boolean terrainEnabled() {
		return mWorldAngle==0 && mWorldShift.isZero();
	}

	private void drawTerrain() {
		if(!terrainEnabled())
			return;
		mGraphics3D.setShaderProgram(mShadowProgram);
		mShadowHelper.setShadowShaderProperties(mShadowProgram);
		mGraphics.bindTexture(grass,ShadowProgram.COLOR_TEXTURE_LEVEL);
		mGraphics3D.setColorFactor(1);
		mTerrainBatch.draw();
	}

	private void drawTerrainLightmap() {
		if(!terrainEnabled())
			return;
		mGraphics3D.setShaderProgram(mLightmapProgram);
		mGraphics.bindTexture(grass,LightmapProgram.COLOR_TEXTURE_LEVEL);
		mGraphics.bindTexture(mLightmapHelper.mLightMap.mTargetTexture, LightmapProgram.LIGHT_TEXTURE_LEVEL);
		mGraphics3D.setColorFactor(1);
		mTerrainBatch.draw();
	}

	private void drawTerrainSpecular() {
		if(!terrainEnabled())
			return;
		mGraphics3D.setShaderProgram(mSpecularProgram);
		mSpecularProgram.setCamera(mCamera);
		mShadowHelper.setLightShaderProperties(mSpecularProgram);
		mGraphics.bindTexture(ice,LightmapProgram.COLOR_TEXTURE_LEVEL);
		mGraphics.bindTexture(mLightmapHelper.mLightMap.mTargetTexture, LightmapProgram.LIGHT_TEXTURE_LEVEL);
		mGraphics3D.setColorFactor(1);
		mTerrainBatch.draw();
	}

	private void drawWater(float pX,float pY,float pZ) {
		mGraphics.checkErrorInst("Pre set water program");
		mGraphics3D.setShaderProgram(mWaterProgram);
		mGraphics.checkErrorInst("Begin draw water");
		mGraphics.bindTexture(waterTex,WaterProgram.COLOR_TEXTURE_LEVEL);
		mGraphics.bindTexture(waterNormal, WaterProgram.NORMAL_TEXTURE_LEVEL);
		mGraphics.bindTexture(mShadowHelper.getDepthMap(),WaterProgram.DEPTH_TEXTURE_LEVEL);
		mGraphics.bindTexture(terrainEnabled()?mHeightTexture:mGraphics.mBlackTexture, WaterProgram.HEIGHT_TEXTURE_LEVEL);
		mWaterProgram.setTime(mGraphics.mShaderTimer);
		mGraphics.checkErrorInst("Set water textures");
		mWaterProgram.setDepthMapProjection(mShadowHelper.mDepthTransformation.mValues);
		mGraphics.checkErrorInst("Set depth map projection");
		mWaterProgram.setLightDirection(mShadowHelper.mLightDirection);
		mGraphics.checkErrorInst("Set light direction");
		mWaterProgram.setLightProperties(0.4f, 1.2f, 0.2f, 1.0f);
		mGraphics.checkErrorInst("Set light properties");
		mWaterProgram.setCameraVector(-pX, -pY, -pZ, true);
		mGraphics.checkErrorInst("Set camera vector");
		mGraphics3D.mWorldTransform.loadIdentity();
		mGraphics3D.mWorldTransform.translate(mWorldShift);
		mGraphics3D.mWorldTransform.rotateZ(mWorldAngle);
		mGraphics3D.setGlobalTransformEnabled(true);
		mWaterBatch.draw();
		mGraphics3D.mWorldTransform.loadIdentity();
	}

	private void drawWeather() {
		mGraphics3D.setDefaultProgram();
		mGraphics.switchZWriting(false);
		mGraphics.switchCulling(false);
		mWeather.draw();
		mGraphics.flush();
		mGraphics.switchZWriting(true);
	}

	@Override
	public void draw() {

		mGraphics3D.activate();

		mGraphics3D.setWhite();
		mGraphics3D.setColorFactor(1);

		mGraphics.switchZBuffer(true);
		mGraphics.switchCulling(true);

		if(mTerrainBatch==null) {

			transfMatrix.loadIdentity();
			transfMatrix.rotateX(-PI/2);
			transfMatrix.scaleZ(0.3f);
			mTerrain.initBatch(PATCHES_X, PATCHES_Y,terrainDimX,terrainDimY);
			mTerrain.beginDraw();
			mTerrain.putAllIndices();
			mTerrain.putGridNeutralColors();
			if(USE_LIGHTMAPS)
				mTerrain.putGridTextureNormalRect(false);
			else
				mTerrain.putGridTextureCoordinates(1.0f);
			mTerrain.putTerrainPositionRect(heights,transfMatrix);
			mTerrainBatch = mGraphics3D.finishBatchRecording().setName("Terrain");

			mTerrain.initBatch(PATCHES_X, PATCHES_Y,terrainDimX,terrainDimY);
			mTerrain.beginDraw();
			mTerrain.putAllIndices();
			mTerrain.putGridNeutralColors();
			mTerrain.putGridTextureCoordinates(1.5f);
			mTerrain.putTerrainPositionRect(heights,transfMatrix);
			mGraphics3D.fillNormals();
			mTerrainBatchLowPoly = mGraphics3D.finishBatchRecording().setName("Terrain_lowPoly");

			mTerrain.initBatch(WATER_PATCHES_X, WATER_PATCHES_Y,terrainDimX,terrainDimY);
			mTerrain.beginDraw();
			mTerrain.putAllIndices();
			mTerrain.putTerrainPositionRect(new float[WATER_PATCHES_X][WATER_PATCHES_Y],transfMatrix);
			mTerrain.putGridTextureNormalRect(false);
			mGraphics3D.fillBuffers();
			mWaterBatch = mGraphics3D.finishBatchRecording().setName("Water");

			mGraphics3D.startBatchRecording(12*12*6);
			mGraphics3D.drawSphere(12, 12, 0,0,0, -10, 2,-2);
			mSkyBoxBatch = mGraphics3D.finishBatchRecording().setName("SkyBox");
		}

		if(mFirstFrame) {
			mShadowHelper.init(mGraphics3D,1024);
			mLightmapHelper.init(mShadowHelper,512,terrainDimX,terrainDimY,STATIC_SHADOWS);
			mEnvironmentMap = mGraphics.createRenderTarget(512, 512, new TextureProperties(TextureWrap.CLAMP,TextureFilter.LINEAR),true);
			mHeightTexture = mTerrain.createCoastTexture(heights, 0, new SqrtKernel().init(COAST_KERNELSIZE), new TextureProperties(TextureFilter.LINEAR_MIP_LINEAR,4),1,1.5f);
		}

		if(mFirstFrame || !STATIC_SHADOWS) {
			renderDepthImage();
			if(mShadowHelper.mRenderToScreen)
				return;
			if(USE_LIGHTMAPS) {
				createLightmap();
				if(mLightmapHelper.mRenderToScreen)
					return;
			}
		}

		float fac = 0.2f;
		float rad = 1.6f;
		float pX = -(float)Math.sin(mStateTimer*fac)*rad;
		float pY = 1.5f+(float)Math.sin(mStateTimer*fac*6) + GLOBAL_SHIFT;
		float pZ = (float)Math.cos(mStateTimer*fac)*rad;

		mCamera.setPerspectiveProjection(0.6f,0.1f,100.5f);

		if(ENVIRONMENT_MAPPING) {
			mGraphics.checkErrorInst("Pre environment mapping");
			mGraphics.setTextureRenderTarget(mEnvironmentMap);

			mGraphics.clear(0,0,0,1,GLMasks.DEPTH_BUFFER_BIT);

			mGraphics3D.setCamera(mCamera);
			mGraphics3D.mViewProjectionTransform.mirrorAtPlane(-(float)Math.sin(mWorldAngle),(float)Math.cos(mWorldAngle),0, mWorldShift);
			//mGraphics3D.refreshCameraTransform();

			drawSky();

			mGraphics3D.setShaderProgram(mShadowProgram);
			mShadowHelper.setShadowShaderProperties(mShadowProgram);
			mGraphics.bindTexture(ice);
			mGraphics.switchCulling(true);
			mGraphics.switchZBuffer(true);

			if(terrainEnabled())
				mTerrainBatch.draw();

			mGraphics3D.setDefaultProgram();
			drawShadCube();

			drawWeather();

			mGraphics.leaveTextureRenderTarget();

			waterTex = mEnvironmentMap.mTargetTexture;
			mGraphics.checkErrorInst("Post environment mapping");

		}

		//Begin real drawing
		mGraphics.clear(0,0,0.25f,1,GLMasks.DEPTH_BUFFER_BIT);

		mCamera.setLookAt(pX,pY,pZ, 0,0,0, 0,1,0);
		mGraphics3D.setCamera(mCamera);

//		if(true) {
//			mGraphics3D.setDefaultProgram();
//			mGraphics.bindTexture(grass);
//			mGraphics.bindTexture(mGraphics.mGFXLoader.getImage("sky"));
//			mGraphics3D.drawCubeCentered(0, 0, 0, 2);
//			return;
//		}

		mGraphics.checkErrorInst("Pre draw sky");
		drawSky();
		mGraphics.checkErrorInst("Draw sky");

		drawWater(pX,pY,pZ);

		if(USE_LIGHTMAPS)
			//drawTerrainLightmap();
			drawTerrainSpecular();
		else
			drawTerrain();

		drawWeather();

		mGraphics3D.setDefaultProgram();
		drawShadCube();

		mGraphics2D.activate();
		mGraphics.switchZBuffer(false);
		mGraphics2D.switchGameCoordinates(false);
		mGraphics2D.setWhite();
		if(screenShotTex!=null) {
			mGraphics.bindTexture(screenShotTex);
			mGraphics2D.drawRectCentered(0, 0, 1);
		}

	}

	@Override
	protected void restartGraphics() {
		mHeightTexture = mTerrain.createCoastTexture(heights, 0, new SqrtKernel().init(COAST_KERNELSIZE), new TextureProperties(TextureFilter.LINEAR_MIP_LINEAR,4),1,1.5f);
	}

	@Override
	protected void step(float deltaTime) {
		mWeather.step(deltaTime);
	}

	@Override
	public void stop() {
		mGraphics3D.resetGlobalTransform();
		mGraphics3D.resetCamera();
		mGraphics3D.resetProjection();

		mShadowHelper.free();
	}

	@Override
	public void keyDown(int code) {
		if(code=='a')
			mWorldAngle += 0.05f;
		if(code=='r')
			mWorldShift.mY -= 0.01f;
		if(code=='t')
			mWorldShift.mY += 0.01f;
		if(code=='f')
			mWorldShift.mX -= 0.01f;
		if(code=='g')
			mWorldShift.mX += 0.01f;
		if(code=='u')
			mCubeTop -= 0.01f;
		if(code=='i')
			mCubeTop += 0.01f;
		if(code=='j')
			mCubeShiftX -= 0.01f;
		if(code=='k')
			mCubeShiftX += 0.01f;
		if(code=='x') {
			mWorldAngle = 0;
			mWorldShift.setZero();
		}
	}

}
