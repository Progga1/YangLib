package yang.graphics.translator;

import java.nio.ByteBuffer;

import yang.graphics.textures.TextureCoordBounds;

public class SubTexture extends AbstractTexture {

	public int mLeft,mTop;
	public TextureCoordBounds mCoordBounds;
	public TextureAtlas mAtlas;
	public Texture mTexture;
	
	public SubTexture(TextureAtlas atlas) {
		mAtlas = atlas;
		mTexture = atlas.mMainTexture;
		mCoordBounds = new TextureCoordBounds();
	}
	
	public void setPosition(int left,int top) {
		mLeft = left;
		mTop = top;
		refreshBoundaries();
	}
	
	public void setExtents(int width,int height) {
		mWidth = width;
		mHeight = height;
		refreshBoundaries();
	}
	
	public void refreshBoundaries() {
		mCoordBounds.set(mTexture,mLeft,mTop,mWidth,mHeight);
	}
	
	@Override
	public void update(ByteBuffer source, int width, int height) {
		mTexture.updateRect(mLeft, mTop, width, height, source);
	}

	@Override
	public void updateRect(int x, int y, int width, int height, ByteBuffer data) {
		mTexture.updateRect(x+mLeft, y+mTop, width,height, data);
	}

	@Override
	public Texture finish() {
		refreshBoundaries();
		return mTexture.finish();
	}

	
	
}
