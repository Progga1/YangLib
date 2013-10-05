package yang.graphics.translator;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import yang.graphics.model.FloatColor;
import yang.graphics.textures.TextureData;
import yang.graphics.textures.TextureProperties;
import yang.graphics.textures.enums.TextureFilter;

public class Texture extends AbstractTexture {

	protected GraphicsTranslator mGraphics;
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

	@Override
	public void update(ByteBuffer source,int width,int height) {
		if(source!=null)
			source.rewind();
		mGraphics.setTextureData(mId, width,height, source, mProperties);
		mWidth = width;
		mHeight = height;
		if(source!=null)
			finish();
	}
	
	/**
	 * No mipmaps are generated
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param data
	 */
	public void updateRect(int x, int y, int width, int height, ByteBuffer data) {
		assert mGraphics.preCheck("Update rect");
		if(x<0 || y<0 || x+width>mWidth || y+height>mHeight)
			throw new RuntimeException("Texture rect values out of bounds: Rect x,y,w,h="+x+","+y+","+width+","+height+"; Texture w,h="+mWidth+","+mHeight);
		mGraphics.setTextureRectData(this.mId, 0, x,y, width,height, mProperties.mChannels, data);
		assert mGraphics.checkErrorInst("Update rect");
	}
	
	public void fillWithColor(FloatColor color) {
		this.update(TextureData.createSingleColorBuffer(mWidth,mHeight, mProperties, color));
	}
	
	@Override
	public Texture finish() {
		if(mProperties.mFilter == TextureFilter.LINEAR_MIP_LINEAR || mProperties.mFilter == TextureFilter.NEAREST_MIP_LINEAR) {
			mGraphics.generateMipMap();
		}
		return this;
	}
	
	public void finalize() {
		assert mFreed:"Texture garbage collected, but still in video memory";
	}

	public int getByteCount() {
		return mWidth*mHeight*mProperties.mChannels;
	}
	
}
