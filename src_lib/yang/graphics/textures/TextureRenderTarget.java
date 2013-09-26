package yang.graphics.textures;

import yang.graphics.translator.Texture;
import yang.model.ScreenInfo;

public class TextureRenderTarget implements ScreenInfo{

	public Texture mTargetTexture;
	public int mFrameBufferId;
	public int mDepthBufferId;
	public int mWidth;
	public int mHeight;
	public float mRatioX;
	public float mRatioY;
	
	public TextureRenderTarget(Texture targetTexture,int frameBufferId,int depthBufferId) {
		mTargetTexture = targetTexture;
		mFrameBufferId = frameBufferId;
		mDepthBufferId = depthBufferId;
		fakeDimensions(targetTexture.mWidth,targetTexture.mHeight);
	}

	public TextureRenderTarget(Texture targetTexture) {
		this(targetTexture,0,0);
	}

	public int getSurfaceWidth() {
		return mWidth;
	}

	public int getSurfaceHeight() {
		return mHeight;
	}

	public float getSurfaceRatioX() {
		return mRatioX;
	}

	public float getSurfaceRatioY() {
		return mRatioY;
	}

	public void fakeDimensions(int width, int height,float ratioX,float ratioY) {
		mWidth = width;
		mHeight = height;
		mRatioX = ratioX;
		mRatioY = ratioY;
	}
	
	public void fakeDimensions(int width, int height) {
		fakeDimensions(width,height,width/height,1);
	}
	
	public void fakeDimensions(ScreenInfo surface) {
		fakeDimensions(surface.getSurfaceWidth(),surface.getSurfaceHeight(),surface.getSurfaceRatioX(),surface.getSurfaceRatioY());
	}
	
}
