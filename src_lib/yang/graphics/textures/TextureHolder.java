package yang.graphics.textures;

import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.translator.AbstractGFXLoader;
import yang.graphics.translator.Texture;

public class TextureHolder {

	private Texture mTexture;
	private String mFilename;
	private TextureSettings mSettings;
	
	public TextureHolder(String filename,TextureSettings settings) {
		mFilename = filename;
		mSettings = settings;
	}
	
	public TextureHolder(String filename,TextureFilter filter) {
		this(filename,new TextureSettings(filter));
	}
	
	public Texture getTexture(AbstractGFXLoader gfxLoader) {
		if(mTexture==null) {
			mTexture = gfxLoader.getImage(mFilename,mSettings);
		}
		return mTexture;
	}
	
}
