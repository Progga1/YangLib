package yang.graphics.translator;

import java.nio.ByteBuffer;

import yang.graphics.model.FloatColor;
import yang.graphics.textures.TextureData;

public abstract class AbstractTexture {

	public int mWidth;
	public int mHeight;
	
	public abstract void update(ByteBuffer source,int width,int height);
	public abstract void updateRect(int x, int y, int width, int height, ByteBuffer data);
	public abstract Texture finish();
	
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
	
	public void setEmpty() {
		update(null,mWidth,mHeight);
	}
	
	public int getWidth() {
		return mWidth;
	}

	public int getHeight() {
		return mHeight;
	}
	
}
