package yang.graphics.translator;

import java.nio.ByteBuffer;

import yang.graphics.textures.TextureData;

public abstract class AbstractTexture {
	
	public int mWidth;
	public int mHeight;
	public boolean mIsAlphaMap = false;
	
	public abstract void update(ByteBuffer source,int width,int height);
	public abstract void updateRect(int x, int y, int width, int height, ByteBuffer data);
	public abstract int getChannels();
	public abstract boolean isFinished();
	public abstract boolean isFreed();
	
	public void update(ByteBuffer source) {
		update(source,mWidth,mHeight);
	}
	
	public void update(TextureData data) {
		update(data.mData, data.mWidth,data.mHeight);
	}
	
	/**
	 * No mipmaps are generated
	 * @param x
	 * @param y
	 * @param data
	 */
	public void updateRect(int x, int y, TextureData data) {
		updateRect(x,y,data.mWidth,data.mHeight,data.mData);
	}
	
	public void resetData() {
		update(null,mWidth,mHeight);
	}
	
	public int getWidth() {
		return mWidth;
	}

	public int getHeight() {
		return mHeight;
	}
	
	public int getByteCount() {
		return mWidth*mHeight*getChannels();
	}
	
}
