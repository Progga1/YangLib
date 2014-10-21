package yang.graphics.textures;

import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.textures.enums.TextureWrap;


public class TextureProperties {

	public final static TextureWrap DEFAULT_WRAP = TextureWrap.REPEAT;
	public final static TextureFilter DEFAULT_FILTER = TextureFilter.NEAREST;

	public TextureWrap mWrapX;
	public TextureWrap mWrapY;
	public TextureFilter mFilter;
	public int mChannels;
	public boolean mCompressIfPossible = true;

	public TextureProperties(TextureWrap wrapX,TextureWrap wrapY,TextureFilter filter,int channels) {
		mWrapX = wrapX;
		mWrapY = wrapY;
		mFilter = filter;
		mChannels = channels;
	}

	public TextureProperties(TextureWrap wrapX,TextureWrap wrapY,TextureFilter filter) {
		this(wrapX,wrapY,filter,4);
	}

	public TextureProperties(TextureWrap wrap,TextureFilter filter) {
		this(wrap,wrap,filter);
	}

	public TextureProperties(TextureWrap wrap) {
		this(wrap,DEFAULT_FILTER);
	}

	public TextureProperties(TextureWrap wrapX,TextureWrap wrapY) {
		this(wrapX,wrapY,DEFAULT_FILTER);
	}

	public TextureProperties(TextureFilter filter) {
		this(DEFAULT_WRAP,DEFAULT_WRAP,filter);
	}

	public TextureProperties() {
		this(DEFAULT_WRAP,DEFAULT_WRAP);
	}

	public TextureProperties(TextureFilter filter,int channels) {
		this(DEFAULT_WRAP,DEFAULT_WRAP,filter,channels);
	}

	public TextureProperties(int channels) {
		this(DEFAULT_WRAP,DEFAULT_WRAP,DEFAULT_FILTER,channels);
	}

	@Override
	public boolean equals(Object object) {
		if(!(object instanceof TextureProperties))
			return false;
		TextureProperties settings = (TextureProperties)object;
		return settings.mWrapX==mWrapX && settings.mWrapY==mWrapY && settings.mFilter==mFilter;

	}

	@Override
	public String toString() {
		return mWrapX+","+mWrapY+","+mFilter+","+mChannels;
	}

	@Override
	public TextureProperties clone() {
		TextureProperties result = new TextureProperties(mWrapX,mWrapY,mFilter,mChannels);
		result.mCompressIfPossible = mCompressIfPossible;
		return result;
	}

	public boolean isMipMap() {
		return mFilter==TextureFilter.LINEAR_MIP_LINEAR || mFilter==TextureFilter.NEAREST_MIP_LINEAR;
	}

	public TextureProperties setCompress(boolean compressIfPossible) {
		mCompressIfPossible = compressIfPossible;
		return this;
	}

}
