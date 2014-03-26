package yang.graphics.textures;

import yang.graphics.translator.Texture;
import yang.math.objects.YangMatrix;
import yang.model.SurfaceParameters;

public class TextureRenderTarget implements SurfaceParameters {

	public boolean mStereoEnabled = false;
	public SurfaceParameters mSurfaceParameters;
	public boolean mKeepSurfaceParameters = false;
	public Texture mTargetTexture;
	public int mFrameBufferId;
	public int mDepthBufferId;
	public int mWidth;
	public int mHeight;
	public float mRatioX;
	public float mRatioY;
	public YangMatrix mPostCameraTransform = null;

	public TextureRenderTarget(Texture targetTexture,int frameBufferId,int depthBufferId) {
		mTargetTexture = targetTexture;
		mFrameBufferId = frameBufferId;
		mDepthBufferId = depthBufferId;
		fakeDimensions(targetTexture.mWidth,targetTexture.mHeight);
	}

	public void setUseScreenParameters(boolean useScreenParameters) {
		if(useScreenParameters)
			mSurfaceParameters = mTargetTexture.mGraphics;
		else
			mSurfaceParameters = this;
		mKeepSurfaceParameters = useScreenParameters;
	}

	public TextureRenderTarget(Texture targetTexture) {
		this(targetTexture,0,0);
	}

	@Override
	public int getSurfaceWidth() {
		return mWidth;
	}

	@Override
	public int getSurfaceHeight() {
		return mHeight;
	}

	@Override
	public float getSurfaceRatioX() {
		return mRatioX;
	}

	@Override
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
		fakeDimensions(width,height,(float)width/height,1);
	}

	public void fakeDimensions(SurfaceParameters surface) {
		fakeDimensions(surface.getSurfaceWidth(),surface.getSurfaceHeight(),surface.getSurfaceRatioX(),surface.getSurfaceRatioY());
	}

	@Override
	public YangMatrix getViewPostTransform() {
		return mPostCameraTransform;
	}

}
