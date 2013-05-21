package yang.graphics.textures;

import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.textures.enums.TextureWrap;


public class TextureSettings {

	public final static TextureWrap DEFAULT_WRAP = TextureWrap.REPEAT;
	public final static TextureFilter DEFAULT_FILTER = TextureFilter.NEAREST;
	
	public TextureWrap mWrapX;
	public TextureWrap mWrapY;
	public TextureFilter mFilter;
	public int mChannels;
	
	public TextureSettings(TextureWrap wrapX,TextureWrap wrapY,TextureFilter filter,int channels) {
		mWrapX = wrapX;
		mWrapY = wrapY;
		mFilter = filter;
		mChannels = channels;
	}
	
	public TextureSettings(TextureWrap wrapX,TextureWrap wrapY,TextureFilter filter) {
		this(wrapX,wrapY,filter,4);
	}
	
	public TextureSettings(TextureWrap wrap,TextureFilter filter) {
		this(wrap,wrap,filter);
	}
	
	public TextureSettings(TextureWrap wrapX,TextureWrap wrapY) {
		this(wrapX,wrapY,DEFAULT_FILTER);
	}
	
	public TextureSettings(TextureFilter filter) {
		this(DEFAULT_WRAP,DEFAULT_WRAP,filter);
	}
	
	public TextureSettings() {
		this(DEFAULT_WRAP,DEFAULT_WRAP);
	}
	
	public TextureSettings(TextureFilter filter,int channels) {
		this(DEFAULT_WRAP,DEFAULT_WRAP,filter,channels);
	}
	
	public TextureSettings(int channels) {
		this(DEFAULT_WRAP,DEFAULT_WRAP,DEFAULT_FILTER,channels);
	}
	
	public boolean equals(Object object) {
		if(!(object instanceof TextureSettings))
			return false;
		TextureSettings settings = (TextureSettings)object;
		return settings.mWrapX==mWrapX && settings.mWrapY==mWrapY && settings.mFilter==mFilter;
		
	}
	
}
