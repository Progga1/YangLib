package yang.graphics.translator;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import yang.graphics.font.BitmapFont;
import yang.graphics.model.material.YangMaterialProvider;
import yang.graphics.model.material.YangMaterialSet;
import yang.graphics.textures.TextureData;
import yang.graphics.textures.TextureProperties;
import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.textures.enums.TextureWrap;
import yang.systemdependent.AbstractResourceManager;



public abstract class AbstractGFXLoader implements YangMaterialProvider{
	
	public static final String[] IMAGE_EXT	= new String[]{".png",".jpg"};
	public static final String SHADER_EXT	= ".txt";

	protected String SHADER_PATH	= "shaders" + File.separatorChar;
	protected String[] IMAGE_PATH		= new String[]{"","textures"+File.separatorChar,"models"+File.separatorChar};
	protected String MATERIAL_PATH  = "models" + File.separatorChar;
	
	protected HashMap<String, Texture> mTextures;
	protected HashMap<String, String> mShaders;
	protected HashMap<String, YangMaterialSet> mMaterials;
	protected GraphicsTranslator mGraphics;
	
	public AbstractResourceManager mResources;

	protected abstract TextureData derivedLoadImageData(String filename,boolean forceRGBA);
	
	public AbstractGFXLoader(GraphicsTranslator graphics,AbstractResourceManager resources) {
		mTextures = new HashMap<String, Texture>();
		mShaders = new HashMap<String, String>();
		mMaterials = new HashMap<String, YangMaterialSet>();
		mGraphics = graphics;
		mResources = resources;
	}
	
	public YangMaterialSet getMaterialSet(String name) {
		if(name.startsWith("./"))
			name = name.substring(2);
		YangMaterialSet result = mMaterials.get(name);
		if(result!=null)
			return result;
		result = new YangMaterialSet(this);
		String filename = MATERIAL_PATH+name;
		if(!mResources.fileExists(filename))
			return null;
		try {
			result.loadFromStream(mResources.getInputStream(filename));
		} catch (IOException e) {
			return null;
		}
		mMaterials.put(name, result);
		return result;
	}
	
	public TextureData loadImageData(String path,String name,String ext,boolean forceRGBA) {
		String filename = path + File.separatorChar+name+"."+ext;
		if(!mResources.fileExists(filename))
			return null;
		else
			return derivedLoadImageData(filename,forceRGBA);
	}
	
	public String createExistingFilename(String name) {
		String filename = null;
		boolean hasExt = false;
		for(int i=0;i<IMAGE_EXT.length;i++) {
			if(name.endsWith(IMAGE_EXT[i])) {
				hasExt = true;
				break;
			}
		}
		
		for(String path:IMAGE_PATH) {
			if(mResources.fileExists(path+name))
				return path+name;
			for(String ext:IMAGE_EXT) {
				if(mResources.fileExists(path+name+ext))
					return path+name+ext;
			}
		}

		return filename;
	}
	
	protected TextureData loadImageData(String name,boolean forceRGBA) {
		String filename = createExistingFilename(name);
		if(filename==null)
			return null;
		return derivedLoadImageData(filename,forceRGBA);
	}
	
	public TextureData loadImageData(String name) {
		return loadImageData(name,false);
	}
	
	private Texture loadTexture(String filename,TextureProperties textureSettings,boolean redToAlpha) {
		TextureData data = derivedLoadImageData(filename,redToAlpha);
		if(redToAlpha)
			data.redToAlpha();
		Texture result = mGraphics.createTexture(data,textureSettings).finish();
		result.mIsAlphaMap = redToAlpha;
		return result;
	}
	
	protected synchronized Texture getImage(String name,TextureProperties textureSettings, boolean redToAlpha) {
//		if(name.length()>4 && name.charAt(name.length()-4)=='.')
//			name = name.substring(0,name.length()-4);
		String filename = createExistingFilename(name);
		if(filename==null)
			throw new RuntimeException("Image not found: "+name);
		Texture texture = mTextures.get(filename);
		
		if (texture != null && texture.mSettings.equals(textureSettings))
			return texture;
		if(textureSettings==null)
			textureSettings = new TextureProperties();
		texture = loadTexture(filename, textureSettings, redToAlpha);
		mTextures.put(filename, texture);
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
		for(Entry<String,Texture> entry:mTextures.entrySet()) {
			entry.getValue().free();
		}
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
