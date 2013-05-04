package yang.graphics;

import java.io.File;
import java.util.HashMap;

import yang.graphics.font.BitmapFont;
import yang.graphics.textures.TextureData;
import yang.graphics.textures.TextureSettings;
import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.textures.enums.TextureWrap;
import yang.graphics.translator.GraphicsTranslator;
import yang.graphics.translator.Texture;
import yang.systemdependent.AbstractResourceManager;



public abstract class AbstractGFXLoader {
	
	public static final String IMAGE_EXT	= ".png";
	public static final String SHADER_EXT	= ".txt";

	protected String SHADER_PATH	= "shaders" + File.separatorChar;
	protected String IMAGE_PATH		= "textures" + File.separatorChar;
	
	protected HashMap<String, Texture> mTextures;
	protected HashMap<String, String> mShaders;
	protected GraphicsTranslator mGraphics;
	
	public AbstractResourceManager mResources;

	public abstract TextureData loadImageData(String filename);
	
	public AbstractGFXLoader(GraphicsTranslator graphics,AbstractResourceManager resources) {
		mTextures = new HashMap<String, Texture>();
		mShaders = new HashMap<String, String>();
		mGraphics = graphics;
		mResources = resources;
	}
	
	protected Texture loadImage(String name,TextureSettings textureSettings,boolean redToAlpha) {
		TextureData data = loadImageData(name);
		if(redToAlpha)
			data.redToAlpha();
		return mGraphics.createTexture(data,textureSettings).finish();
	}
	
	public synchronized Texture getImage(String name,TextureSettings textureSettings, boolean redAsAlpha) {
		Texture texture = mTextures.get(name);
		
		if (texture != null && texture.mSettings.equals(textureSettings))
			return texture;
		if(textureSettings==null)
			textureSettings = new TextureSettings();
		texture = loadImage(name, textureSettings, redAsAlpha);
		mTextures.put(name, texture);
		mGraphics.rebindTexture();
		
		return texture;
	}
	
	public Texture getImage(String name,TextureSettings textureSettings){
		return getImage(name,textureSettings,false);
	}
	
	public Texture getImage(String name, TextureWrap wrapX, TextureWrap wrapY) {
		return getImage(name,new TextureSettings(wrapX,wrapY));
	}
	
	public Texture getImage(String name, TextureFilter textureFilter) {
		return getImage(name,new TextureSettings(textureFilter));
	}
	
	public Texture getImage(String name) {
		return getImage(name,new TextureSettings());
	}
	
	public Texture getAlphaMap(String name,TextureSettings textureSettings) {
		return getImage(name,textureSettings,true);
	}
	
	public Texture getAlphaMap(String name) {
		return getImage(name,new TextureSettings(),true);
	}
	
	public Texture getAlphaMap(String name, TextureWrap wrapX, TextureWrap wrapY) {
		return getAlphaMap(name,new TextureSettings(wrapX,wrapY));
	}
	
	public Texture getAlphaMap(String name, TextureWrap wrapX, TextureWrap wrapY, TextureFilter textureFilter) {
		return getAlphaMap(name,new TextureSettings(wrapX,wrapY,textureFilter));
	}

	public String getShader(String name) {
		String shader = mShaders.get(name);
		if (shader != null)
			return shader;
		if(!name.endsWith(SHADER_EXT))
			name += SHADER_EXT;
		shader = mResources.textFileToString(SHADER_PATH+name);
		mShaders.put(name, shader);
		return shader;
	}

	public void clear() {
		mTextures.clear();
	}

	public BitmapFont loadFont(String texAndDatafilename) {
		BitmapFont result = new BitmapFont();
		return result.init(texAndDatafilename,this);
	}
	
	public BitmapFont loadFont(Texture texture,String fontDataFilename) {
		BitmapFont result = new BitmapFont();
		return result.init(texture,fontDataFilename,mResources);
	}
	
}
