package yang.graphics.translator;

import java.nio.ByteBuffer;

import yang.graphics.model.FloatColor;
import yang.graphics.textures.TextureData;

public abstract class AbstractTexture {

	public static final int STATUS_UNINITIALIZED = 0;
	public static final int STATUS_INITIALIZED = 1;
	public static final int STATUS_GENERATED = 2;
	public static final int STATUS_FINISHED = 3;
	public static final int STATUS_FREED = 4;
	
	public int mWidth;
	public int mHeight;
	public int mStatus = STATUS_UNINITIALIZED;
	
	public abstract void update(ByteBuffer source,int width,int height);
	public abstract void updateRect(int x, int y, int width, int height, ByteBuffer data);
	
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
		if(mStatus>STATUS_GENERATED)
			mStatus = STATUS_GENERATED;
	}
	
	public int getWidth() {
		return mWidth;
	}

	public int getHeight() {
		return mHeight;
	}
	
	public void finish() {
		if(mStatus<STATUS_FINISHED)
			mStatus = STATUS_FINISHED;
	}
	
}
