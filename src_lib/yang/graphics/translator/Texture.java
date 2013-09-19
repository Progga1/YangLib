package yang.graphics.translator;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import yang.graphics.textures.TextureData;
import yang.graphics.textures.TextureProperties;
import yang.graphics.textures.enums.TextureFilter;



public class Texture {

	protected GraphicsTranslator mGraphics;
	public int mWidth;
	public int mHeight;
	public int mId;
	public TextureProperties mProperties;
	public boolean mIsAlphaMap;
	public boolean mFreed = false;
	
	public Texture(GraphicsTranslator graphics) {
		mGraphics = graphics;
		mIsAlphaMap = false;
		
	}
	
	public Texture(GraphicsTranslator graphics,TextureProperties properties) {
		this(graphics);
		mProperties = properties;
	}
	
	public Texture(GraphicsTranslator graphics, ByteBuffer source, int width, int height, TextureProperties properties) {
		this(graphics);
		initCompletely(source, width, height, properties);
	}
	
	public Texture generate() {
		mId = mGraphics.genTexture();
		return this;
	}

	public void initCompletely(ByteBuffer source, int width, int height, TextureProperties settings) {
		mWidth = width;
		mHeight = height;
		mProperties = settings;
		if(settings==null)
			settings = new TextureProperties();
		generate();
		if(source!=null) {
			source.order(ByteOrder.nativeOrder());
			source.rewind();
		}
		mGraphics.setTextureData(mId, mWidth,mHeight, source,mProperties);
		if(source!=null)
			finish();
	}
	
//	public void updateRegion(ByteBuffer source, int left,int top, int width,int height) {
//		mGraphics.updateTexture(this, source, left,top, width,height);
//	}

	public int getWidth() {
		return mWidth;
	}

	public int getHeight() {
		return mHeight;
	}
	
	public int getId() {
		return mId;
	}
	
	public void setId(int id) {
		this.mId = id;
	}

	public void free() {
		mFreed = true;
		mGraphics.deleteTexture(mId);
	}
	
//	public void update(ByteBuffer source) {
//		mGraphics.deleteTexture(mId);
//		if(source!=null)
//			source.rewind();
//		mGraphics.initTexture(this, source, mSettings);
//	}

	public void update(ByteBuffer source,int width,int height) {
		if(source!=null)
			source.rewind();
		mGraphics.setTextureData(mId, width,height, source, mProperties);
		mWidth = width;
		mHeight = height;
		if(source!=null)
			finish();
	}
	
	public void update(ByteBuffer source) {
		update(source,mWidth,mHeight);
	}
	
	public void update(TextureData data) {
		update(data.mData, data.mWidth,data.mHeight);
	}
	
	public void setEmpty() {
		update(null,mWidth,mHeight);
	}
	
	public Texture finish() {
		if(mProperties.mFilter == TextureFilter.LINEAR_MIP_LINEAR || mProperties.mFilter == TextureFilter.NEAREST_MIP_LINEAR) {
			mGraphics.generateMipMap();
		}
		return this;
	}
	
	public void finalize() {
		assert mFreed;
	}

	public int getByteCount() {
		return mWidth*mHeight*mProperties.mChannels;
	}
	
}
