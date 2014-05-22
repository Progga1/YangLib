package yang.util;

import yang.graphics.textures.TextureData;
import yang.graphics.textures.TextureRenderTarget;
import yang.graphics.translator.GraphicsTranslator;

public class ImageCaptureData {

	public GraphicsTranslator mGraphics;
	public TextureRenderTarget mRenderTarget;
	public TextureData mImage;

	public ImageCaptureData(GraphicsTranslator graphics) {
		mGraphics = graphics;
	}

	public ImageCaptureData init(int width,int height) {
		mRenderTarget = mGraphics.createRenderTarget(width, height);
		mImage = new TextureData(width,height);
		return this;
	}

	public void resize(int width,int height,boolean forceRecreation) {
		mImage.resize(width,height,forceRecreation);
		mGraphics.resizeRenderTarget(mRenderTarget,width,height);
	}

	public void free() {
		mGraphics.deleteRenderTarget(mRenderTarget);
		mImage = null;
	}

	public int getWidth() {
		return mImage.mWidth;
	}

	public int getHeight() {
		return mImage.mHeight;
	}

	public void setMinRatioX(float minRatio) {
		mRenderTarget.mMinRatioX = minRatio;
	}

}
