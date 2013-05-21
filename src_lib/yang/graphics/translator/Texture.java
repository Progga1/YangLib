package yang.graphics.translator;

import java.nio.ByteBuffer;

import yang.graphics.textures.TextureSettings;
import yang.graphics.textures.enums.TextureFilter;



public class Texture {

	protected GraphicsTranslator mGraphics;
	public int mWidth;
	public int mHeight;
	public int mId;
	public TextureSettings mSettings;
	public boolean mIsAlphaMap;
	
	public Texture(GraphicsTranslator graphics) {
		mGraphics = graphics;
		mIsAlphaMap = false;
	}
	
	public Texture(GraphicsTranslator graphics, ByteBuffer source, int width, int height, TextureSettings settings) {
		this(graphics);
		set(source, width, height, settings);
	}

	public void set(ByteBuffer source, int width, int height, TextureSettings settings) {
		mWidth = width;
		mHeight = height;
		mSettings = settings;
		if(settings==null)
			settings = new TextureSettings();
		mGraphics.initTexture(this, source);
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
		mGraphics.deleteTexture(mId);
	}
	
//	public void update(ByteBuffer source) {
//		mGraphics.deleteTexture(mId);
//		if(source!=null)
//			source.rewind();
//		mGraphics.initTexture(this, source, mSettings);
//	}

	public void update(ByteBuffer source) {
		if(source!=null)
			source.rewind();
		mGraphics.setTextureData(this, source);
		if(source!=null)
			finish();
	}
	
	public Texture finish() {
		if(mSettings.mFilter == TextureFilter.LINEAR_MIP_LINEAR || mSettings.mFilter == TextureFilter.NEAREST_MIP_LINEAR) {
			mGraphics.generateMipMap();
		}
		return this;
	}
	
}
