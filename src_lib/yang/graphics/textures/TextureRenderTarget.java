package yang.graphics.textures;

import yang.graphics.translator.Texture;
import yang.model.ScreenInfo;

public class TextureRenderTarget implements ScreenInfo{

	public Texture mTargetTexture;
	public int mFrameBufferId;
	public int mDepthBufferId;
	public float mRatioX;
	public float mRatioY;
	
	public TextureRenderTarget(Texture targetTexture,int frameBufferId,int depthBufferId) {
		mTargetTexture = targetTexture;
		mFrameBufferId = frameBufferId;
		mDepthBufferId = depthBufferId;
		mRatioX = mTargetTexture.mWidth/mTargetTexture.mHeight;
		mRatioY = 1;
	}

	public TextureRenderTarget(Texture targetTexture) {
		this(targetTexture,0,0);
	}

	public int getSurfaceWidth() {
		return mTargetTexture.mWidth;
	}

	public int getSurfaceHeight() {
		return mTargetTexture.mHeight;
	}

	public float getSurfaceRatioX() {
		return mRatioX;
	}

	public float getSurfaceRatioY() {
		return mRatioY;
	}
	
}
