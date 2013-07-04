package yang.graphics.defaults.programs.helpers;

import yang.graphics.defaults.Default3DGraphics;
import yang.graphics.defaults.programs.LightmapCreatorProgram;
import yang.graphics.textures.TextureRenderTarget;
import yang.graphics.textures.TextureProperties;
import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.textures.enums.TextureWrap;
import yang.graphics.translator.GraphicsTranslator;
import yang.graphics.translator.glconsts.GLMasks;
import yang.math.objects.matrix.YangMatrixCameraOps;

public class PlanarLightmapHelper {

	public float mObjectWidth,mObjectHeight;
	public float mNear,mFar;
	public Default3DGraphics mGraphics3D;
	public int mSize;
	public GraphicsTranslator mGraphics;
	public LightmapCreatorProgram mLightmapCreatorProgram = new LightmapCreatorProgram();
	public TextureRenderTarget mLightMap;
	public static TextureProperties defaultTextureSettings = createTextureSettings(false);
	public static TextureProperties defaultTextureSettingsMipMap = createTextureSettings(true);
	public boolean mRenderToScreen = false;
	public ShadowHelper mShadowHelper;
	public YangMatrixCameraOps mOrthoProjection,mCameraTransform;
	public float[] mInvOrthoProjection = new float[16];
	private TextureProperties mTextureSettings;
	private boolean mMipMapping;
	private boolean mFinished = false;
	
	private static TextureProperties createTextureSettings(boolean mipMapping) {
		TextureProperties result = new TextureProperties(TextureWrap.CLAMP,TextureWrap.CLAMP,mipMapping?TextureFilter.LINEAR_MIP_LINEAR:TextureFilter.LINEAR);
		result.mChannels = 4;
		return result;
	}
	
	public PlanarLightmapHelper() {

	}
	
	public void refreshProjections() {
		mCameraTransform.setLookAt(0,0,0, 0,-1,0, 0,0,-1);
		mOrthoProjection.setOrthogonalProjection(-mObjectWidth*0.5f,mObjectWidth*0.5f, mObjectHeight*0.5f, -mObjectHeight*0.5f, mNear, mFar);
		mOrthoProjection.asInverted(mInvOrthoProjection);
	}
	
	public void init(ShadowHelper shadowHelper,int textureWidthAndHeight,float objectWidth,float objectHeight,float topMost,float bottomMost,boolean mipMapping) {
		mShadowHelper = shadowHelper;
		mGraphics3D = shadowHelper.mGraphics3D;
		mGraphics = mGraphics3D.mTranslator;
		mObjectWidth = objectWidth;
		mObjectHeight = objectHeight;
		mMipMapping = mipMapping;
		if(mipMapping)
			mTextureSettings = defaultTextureSettingsMipMap;
		else
			mTextureSettings = defaultTextureSettings;
		mNear = -topMost;
		mFar = -bottomMost;
		mGraphics.addProgram(mLightmapCreatorProgram);
		mSize = textureWidthAndHeight;
		mCameraTransform = new YangMatrixCameraOps();
		mOrthoProjection = new YangMatrixCameraOps();
		refreshProjections();
		mLightMap = mGraphics.createRenderTarget(mSize, mSize, mTextureSettings);
	}
	
	public void init(ShadowHelper shadowHelper,int textureWidthAndHeight,float objectWidth,float objectHeight,boolean mipMapping) {
		init(shadowHelper,textureWidthAndHeight,objectWidth,objectHeight,16,-16,mipMapping);
	}
	
	public void beginRender() {
		if(!mRenderToScreen)
			mGraphics.setTextureRenderTarget(mLightMap);
		mFinished = false;
		mGraphics3D.setAmbientColor(1);
		mGraphics.clear(0, 0, 0, 1, GLMasks.DEPTH_BUFFER_BIT);
		//mGraphics3D.setCameraProjection(mCameraTransform,mOrthoProjection,mInvOrthoProjection);
		mGraphics3D.setCameraLookAt(0,0,0, 0,-1,0, 0,0,-1);
		mGraphics.setCullMode(true);
		mGraphics3D.setOrthogonalProjection(mObjectWidth,-mObjectHeight, mNear, mFar);
		mGraphics3D.setShaderProgram(mLightmapCreatorProgram);
		mShadowHelper.setShadowShaderProperties(mLightmapCreatorProgram);
	}
	
	public void finishRender() {
		if(!mRenderToScreen)
			mGraphics.setScreenRenderTarget();
		mFinished = true;
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		mGraphics.setCullMode(false);
		//mGraphics3D.restoreCameraProjection();
		generateMipMaps();
	}
	
	public void generateMipMaps() {
		if(mFinished && mMipMapping) {
			mGraphics3D.bindTexture(mLightMap.mTargetTexture);
			mGraphics.generateMipMap();
		}
	}
	
}
