package yang.graphics.translator;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import yang.graphics.font.BitmapFont;
import yang.graphics.model.material.YangMaterial;
import yang.graphics.model.material.YangMaterialSet;
import yang.graphics.textures.TextureData;
import yang.graphics.textures.TextureProperties;
import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.textures.enums.TextureWrap;
import yang.systemdependent.AbstractResourceManager;



public abstract class AbstractGFXLoader {
	
	public static final String IMAGE_EXT	= ".png";
	public static final String SHADER_EXT	= ".txt";

	protected String SHADER_PATH	= "shaders" + File.separatorChar;
	protected String IMAGE_PATH		= "textures" + File.separatorChar;
	protected String MATERIAL_PATH  = "materials" + File.separatorChar;
	
	protected HashMap<String, Texture> mTextures;
	protected HashMap<String, String> mShaders;
	protected HashMap<String, YangMaterialSet> mMaterials;
	protected GraphicsTranslator mGraphics;
	
	public AbstractResourceManager mResources;

	public abstract TextureData loadImageData(String filename,boolean forceRGBA);
	
	public TextureData loadImageData(String filename) {
		return loadImageData(filename,false);
	}
	
	public AbstractGFXLoader(GraphicsTranslator graphics,AbstractResourceManager resources) {
		mTextures = new HashMap<String, Texture>();
		mShaders = new HashMap<String, String>();
		mMaterials = new HashMap<String, YangMaterialSet>();
		mGraphics = graphics;
		mResources = resources;
	}
	
	public YangMaterialSet getMaterial(String name) {
		YangMaterialSet result = mMaterials.get(name);
		if(result!=null)
			return result;
		result = new YangMaterialSet();

		try {
			result.loadFromStream(mResources.getInputStream(MATERIAL_PATH+name));
		} catch (IOException e) {
			return null;
		}
		mMaterials.put(name, result);
		return result;
		
	}
	
	protected Texture loadImage(String name,TextureProperties textureSettings,boolean redToAlpha) {
		TextureData data = loadImageData(name,redToAlpha);
		if(redToAlpha)
			data.redToAlpha();
		Texture result = mGraphics.createTexture(data,textureSettings).finish();
		result.mIsAlphaMap = redToAlpha;
		return result;
	}
	
	protected synchronized Texture getImage(String name,TextureProperties textureSettings, boolean redToAlpha) {
		Texture texture = mTextures.get(name);
		
		if (texture != null && texture.mSettings.equals(textureSettings))
			return texture;
		if(textureSettings==null)
			textureSettings = new TextureProperties();
		texture = loadImage(name, textureSettings, redToAlpha);
		mTextures.put(name, texture);
		mGraphics.rebindTexture(0);
		
		return texture;
	}
	
	public Texture getImage(String name,TextureProperties textureSettings){
		return getImage(name,textureSettings,false);
	}
	
	public Texture getImage(String name, TextureWrap wrapX, TextureWrap wrapY) {
		return getImage(name,new TextureProperties(wrapX,wrapY));
	}
	
	public Texture getImage(String name, TextureFilter textureFilter) {
		return getImage(name,new TextureProperties(textureFilter));
	}
	
	public Texture getImage(String name) {
		Texture texture = mTextures.get(name);
		if (texture != null)
			return texture;
		else
			return getImage(name,new TextureProperties());
	}
	
	public Texture getAlphaMap(String name,TextureProperties textureSettings) {
		return getImage(name,textureSettings,true);
	}
	
	public Texture getAlphaMap(String name) {
		return getImage(name,new TextureProperties(),true);
	}
	
	public Texture getAlphaMap(String name, TextureWrap wrapX, TextureWrap wrapY) {
		return getAlphaMap(name,new TextureProperties(wrapX,wrapY));
	}
	
	public Texture getAlphaMap(String name, TextureWrap wrapX, TextureWrap wrapY, TextureFilter textureFilter) {
		return getAlphaMap(name,new TextureProperties(wrapX,wrapY,textureFilter));
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

	//TODO handling same filenames with different texture settings
	public void reloadTextures() {
		for(Entry<String,Texture> entry:mTextures.entrySet()) {
			TextureData data = loadImageData(entry.getKey());
			Texture tex = entry.getValue();
			if(tex.mIsAlphaMap)
				data.redToAlpha();
			tex.update(data.mData);
		}
	}
	
	public void deleteTextures() {
		for(Entry<String,Texture> entry:mTextures.entrySet()) {
			entry.getValue().free();
		}
	}
	
}
