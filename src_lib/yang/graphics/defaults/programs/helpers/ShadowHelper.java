package yang.graphics.defaults.programs.helpers;

import yang.graphics.defaults.Default3DGraphics;
import yang.graphics.defaults.programs.DepthProgram;
import yang.graphics.defaults.programs.LightInterface;
import yang.graphics.defaults.programs.ShadowInterface;
import yang.graphics.defaults.programs.ShadowProgram;
import yang.graphics.textures.TextureRenderTarget;
import yang.graphics.textures.TextureSettings;
import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.textures.enums.TextureWrap;
import yang.graphics.translator.GraphicsTranslator;
import yang.graphics.translator.Texture;
import yang.graphics.translator.glconsts.GLMasks;
import yang.math.Geometry;
import yang.math.objects.matrix.YangMatrix;

public class ShadowHelper {

	private DepthProgram depthProgram = new DepthProgram();
	private static YangMatrix depthTrafoCorrection;
	public static TextureSettings defaultTextureSettings = createTextureSettings();
	
	private GraphicsTranslator mGraphics;
	public Default3DGraphics mGraphics3D;
	public TextureRenderTarget mDepthMap;
	public YangMatrix mDepthTransformation;
	public float[] mLightDirection;
	public int mSize;
	public float mMinLight = 0.3f;
	public float mMaxLight = 1.0f;
	public float mAddLight = 0.1f;
	public float mLightFactor = 1f;
	public boolean mRenderToScreen = false;
	
	private static TextureSettings createTextureSettings() {
		TextureSettings result = new TextureSettings(TextureWrap.CLAMP,TextureWrap.CLAMP,TextureFilter.LINEAR);
		result.mChannels = 4;
		return result;
	}
	
	public ShadowHelper() {
		mLightDirection = new float[4];
	}
	
	public synchronized DepthProgram getDepthProgram() {
		if(!depthProgram.mInitialized) {
			mGraphics.addProgram(depthProgram);
		}
		return depthProgram;
	}
	
	public synchronized YangMatrix getDepthTrafoCorrection() {
		if(depthTrafoCorrection==null) {
			depthTrafoCorrection = mGraphics.createTransformationMatrix();
			depthTrafoCorrection.loadIdentity();
			depthTrafoCorrection.translate(0.5f, 0.5f, 0.5f+0.008f);
			depthTrafoCorrection.scale(0.5f, 0.5f, -0.5f);
		}
		return depthTrafoCorrection;
	}
	
	public void setLightSource(float eyeX,float eyeY,float eyeZ, float lookAtX,float lookAtY,float lookAtZ,boolean directional) {
		mGraphics3D.setCameraLookAt(eyeX, eyeY, eyeZ, lookAtX, lookAtY, lookAtZ);
		float dX = lookAtX-eyeX;
		float dY = lookAtY-eyeY;
		float dZ = lookAtZ-eyeZ;
		float dist = Geometry.getDistance(dX, dY, dZ);
		mLightDirection[0] = dX / dist;
		mLightDirection[1] = dY / dist;
		mLightDirection[2] = dZ / dist;
		mLightDirection[3] = directional?0:1;
	}
	
	public void setLightShaderProperties(LightInterface lightShader) {
		lightShader.setLightDirection(mLightDirection);
		lightShader.setLightProperties(mMinLight, mMaxLight, mAddLight, mLightFactor);
	}
	
	public void setShadowShaderProperties(ShadowInterface shadowShader) {
		setLightShaderProperties(shadowShader);
		shadowShader.setDepthMapProjection(mDepthTransformation.mMatrix);
		mGraphics3D.bindTexture(getDepthMap(),ShadowProgram.DEPTH_TEXTURE_LEVEL);
	}
	
	public void init(Default3DGraphics graphics3D,int size) {
		mGraphics3D = graphics3D;
		mGraphics = graphics3D.mTranslator;
		mSize = size;
		mDepthMap = mGraphics.createRenderTarget(mSize, mSize, defaultTextureSettings);
		getDepthProgram();
		getDepthTrafoCorrection();
		mDepthTransformation = mGraphics.createTransformationMatrix();
	}
	
	public void beginDepthRendering() {
		if(!mRenderToScreen)
			mGraphics.setTextureRenderTarget(mDepthMap);
		mGraphics3D.setShaderProgram(depthProgram);
		mGraphics.clear(0,0,0,1,GLMasks.DEPTH_BUFFER_BIT);
		mGraphics3D.setAmbientColor(1);
	}
	
	public void endDepthRendering() {
		if(!mRenderToScreen)
			mGraphics.setScreenRenderTarget();
		mDepthTransformation.set(mGraphics3D.mCameraProjectionMatrix);
		mDepthTransformation.multiplyLeft(depthTrafoCorrection);
	}

	public Texture getDepthMap() {
		return mDepthMap.mTargetTexture;
	}
	
}
