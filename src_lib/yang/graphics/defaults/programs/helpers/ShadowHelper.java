package yang.graphics.defaults.programs.helpers;

import yang.graphics.camera.YangCamera;
import yang.graphics.defaults.Default3DGraphics;
import yang.graphics.defaults.programs.DepthProgram;
import yang.graphics.defaults.programs.LightInterface;
import yang.graphics.defaults.programs.ShadowInterface;
import yang.graphics.defaults.programs.ShadowProgram;
import yang.graphics.defaults.programs.subshaders.realistic.ShadowSubShader;
import yang.graphics.textures.TextureProperties;
import yang.graphics.textures.TextureRenderTarget;
import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.textures.enums.TextureWrap;
import yang.graphics.translator.GraphicsTranslator;
import yang.graphics.translator.Texture;
import yang.graphics.translator.glconsts.GLMasks;
import yang.math.Geometry;
import yang.math.objects.YangMatrix;

public class ShadowHelper {

	private final DepthProgram mDepthProgram = new DepthProgram();
	public static TextureProperties defaultTextureSettings = createTextureSettings();
	public static float DEFAULT_BIAS = 0.01f;

	private GraphicsTranslator mGraphics;
	public Default3DGraphics mGraphics3D;
	public TextureRenderTarget mDepthMap;
	public YangMatrix mDepthTransformation = new YangMatrix();
	private YangMatrix mDepthTrafoCorrection;
	public float[] mLightDirection;
	public int mResX,mResY;
	public float mMinLight = 0.3f;
	public float mMaxLight = 1.0f;
	public float mAddLight = 0.1f;
	public float mLightFactor = 1f;
	public boolean mRenderToScreen = false;
	protected float mBias = DEFAULT_BIAS;

	private static TextureProperties createTextureSettings() {
		final TextureProperties result = new TextureProperties(TextureWrap.CLAMP,TextureWrap.CLAMP,TextureFilter.LINEAR);
		result.mChannels = 4;
		return result;
	}

	public ShadowHelper() {
		mLightDirection = new float[4];
	}

	public synchronized DepthProgram getDepthProgram() {
		if(!mDepthProgram.mInitialized) {
			mGraphics.addProgram(mDepthProgram);
		}
		return mDepthProgram;
	}

	public synchronized YangMatrix refreshTransformation() {
		if(mDepthTrafoCorrection==null) {
			mDepthTrafoCorrection = new YangMatrix();
		}
		mDepthTrafoCorrection.loadIdentity();
		mDepthTrafoCorrection.translate(0.5f, 0.5f, 0.5f+mBias);
		mDepthTrafoCorrection.scale(0.5f, 0.5f, -0.5f);
		mDepthTransformation.loadIdentity();
		mDepthTransformation.setTranslation(0,0,-50);
		mDepthTransformation.multiplyLeft(mDepthTrafoCorrection);
		return mDepthTrafoCorrection;
	}

	public void setLightSource(float eyeX,float eyeY,float eyeZ, float lookAtX,float lookAtY,float lookAtZ,boolean directional) {
		mGraphics3D.setCameraLookAt(eyeX, eyeY, eyeZ, lookAtX, lookAtY, lookAtZ);
		final float dX = lookAtX-eyeX;
		final float dY = lookAtY-eyeY;
		final float dZ = lookAtZ-eyeZ;
		final float dist = Geometry.getDistance(dX, dY, dZ);
		mLightDirection[0] = dX / dist;
		mLightDirection[1] = dY / dist;
		mLightDirection[2] = dZ / dist;
		mLightDirection[3] = directional?0:1;
	}

	public void setLightByCamera(YangCamera camera,boolean directional) {
		mGraphics3D.setCamera(camera);
		camera.getForwardVector(mLightDirection);
		mLightDirection[3] = directional?0:1;
	}

	public void setLightShaderProperties(LightInterface lightShader) {
		lightShader.setLightDirection(mLightDirection);
		lightShader.setLightProperties(mMinLight, mMaxLight, mAddLight, mLightFactor);
	}

	public void setShadowShaderProperties(ShadowInterface shadowShader) {
		setLightShaderProperties(shadowShader);
		shadowShader.setDepthMapProjection(mDepthTransformation.mValues);
		mGraphics.bindTexture(getDepthMap(),ShadowProgram.DEPTH_TEXTURE_LEVEL);
	}

	public void setPropertiesTo(ShadowSubShader shadowSubShader) {
		mGraphics.bindTexture(mDepthMap.mTargetTexture,shadowSubShader.mTextureLevel);
		shadowSubShader.mMainShader.mProgram.setUniformMatrix4f(shadowSubShader.mDepthMapTransformHandle, mDepthTransformation.mValues);
	}

	public void init(Default3DGraphics graphics3D,int resX,int resY) {
		mGraphics3D = graphics3D;
		mGraphics = graphics3D.mTranslator;
		mResX = resX;
		mResY = resY;
		mDepthMap = mGraphics.createRenderTarget(mResX, mResY, defaultTextureSettings);
		getDepthProgram();
		refreshTransformation();
	}

	public void init(Default3DGraphics graphics3D,int size) {
		init(graphics3D,size,size);
	}

	public void beginDepthRendering() {
		if(!mRenderToScreen)
			mGraphics.setTextureRenderTarget(mDepthMap);
		mGraphics3D.setShaderProgram(mDepthProgram);
		mGraphics.clear(0,0,0,1,GLMasks.DEPTH_BUFFER_BIT);
		mGraphics3D.setColorFactor(1);
	}

	public void endDepthRendering() {
		if(!mRenderToScreen)
			mGraphics.leaveTextureRenderTarget();
		mDepthTransformation.set(mGraphics3D.mCameraProjectionMatrix);
		mDepthTransformation.multiplyLeft(mDepthTrafoCorrection);
	}

	public Texture getDepthMap() {
		return mDepthMap.mTargetTexture;
	}

	public void free() {
		mGraphics.deleteRenderTarget(mDepthMap);
	}

	public void setBias(float bias) {
		if(mBias==bias)
			return;
		mBias = bias;
		refreshTransformation();
	}

	public float getBias() {
		return mBias;
	}

}
