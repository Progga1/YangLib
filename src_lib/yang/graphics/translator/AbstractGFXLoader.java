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
import yang.math.objects.Dimensions2i;
import yang.model.Pair;
import yang.systemdependent.AbstractResourceManager;
import yang.util.NonConcurrentList;



public abstract class AbstractGFXLoader implements YangMaterialProvider{
	
	private class ResourceEntry {
		
		public NonConcurrentList<Texture> mTextures = new NonConcurrentList<Texture>();
		public NonConcurrentList<SubTexture> mSubTextures = new NonConcurrentList<SubTexture>();
		
		public void clear() {
			for(Texture tex:mTextures)
				tex.free();
			mTextures.clear();
			mSubTextures.clear();
		}
		
	}
	
	public static int MAX_TEXTURES = 512;
	public static final String[] IMAGE_EXT	= new String[]{".png",".jpg"};
	public static final String SHADER_EXT	= ".txt";

	protected String SHADER_PATH	= "shaders" + File.separatorChar;
	protected String[] IMAGE_PATH		= new String[]{"","textures"+File.separatorChar,"models"+File.separatorChar};
	protected String MATERIAL_PATH  = "models" + File.separatorChar;
	
	public HashMap<String, ResourceEntry> mTextures;
	protected HashMap<String, String> mShaders;
	protected HashMap<String, YangMaterialSet> mMaterials;
	protected NonConcurrentList<Texture> mAtlasses;
	protected GraphicsTranslator mGraphics;
	
	public String[] mTexKeyQueue;
	public AbstractTexture[] mTexQueue;
	protected int mTexQueueId = 0;
	public boolean mEnqueueMode = false;
	protected int mQueueBytes = 0;
	public int mMaxQueueLoadingBytes = -1;
	private Dimensions2i mTempDim = new Dimensions2i();
	
	public int mDefaultApproxTextureSize = 256;
	
	public AbstractResourceManager mResources;

	protected abstract TextureData derivedLoadImageData(String filename,boolean forceRGBA);
	protected abstract void getImageDimensions(String filename,Dimensions2i result);
	
	public AbstractGFXLoader(GraphicsTranslator graphics,AbstractResourceManager resources) {
		mTexKeyQueue = new String[MAX_TEXTURES];
		mTexQueue = new AbstractTexture[MAX_TEXTURES];
		mTexQueueId = 0;
		mTextures = new HashMap<String, ResourceEntry>();
		mShaders = new HashMap<String, String>();
		mMaterials = new HashMap<String, YangMaterialSet>();
		mAtlasses = new NonConcurrentList<Texture>();
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
		for(String path:IMAGE_PATH) {
			if(mResources.fileExists(path+name))
				return path+name;
			for(String ext:IMAGE_EXT) {
				if(mResources.fileExists(path+name+ext))
					return path+name+ext;
			}
		}

		throw new RuntimeException("Image not found: "+name);
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
	
	public SubTexture loadIntoTexture(Texture target,String name,int x,int y) {
		SubTexture result = new SubTexture(target);
		result.setPosition(x,y);
		String filename = createExistingFilename(name);
		ResourceEntry resource = mTextures.get(filename);
		if(resource==null) {
			resource = new ResourceEntry();
			mTextures.put(filename, resource);
		}
		if(mEnqueueMode) {
			getImageDimensions(filename, mTempDim);
			result.setExtents(mTempDim.mWidth,mTempDim.mHeight);
			enqueue(filename,result);
		}else{
			TextureData imageData = loadImageData(filename,target.mProperties.mChannels>3);
			result.update(imageData);
		}
		resource.mSubTextures.add(result);
		return result;
	}

	private void enqueue(String key,AbstractTexture tex) {
		mTexQueue[mTexQueueId] = tex;
		mTexKeyQueue[mTexQueueId++] = key;
		mQueueBytes += tex.getByteCount();
	}
	
	private String pollQueue() {
		if(mTexQueueId<=0)
			return null;
		String tex = mTexKeyQueue[--mTexQueueId];
		mQueueBytes -= mTexQueue[mTexQueueId].getByteCount();
		return tex;
	}
	
	public void loadEnqueuedTextures() {
		mEnqueueMode = false;
		String texKey;
		int minBytes = mMaxQueueLoadingBytes>0?mQueueBytes - mMaxQueueLoadingBytes:-1;
		while(mQueueBytes>minBytes && (texKey=pollQueue())!=null) {
			AbstractTexture tex = mTexQueue[mTexQueueId];
			if(tex.isFinished())
				continue;
			TextureData data = loadImageData(texKey,tex.getChannels()>3);
			if(data==null)
				System.err.println("Image not found: "+texKey);
			if(tex.mIsAlphaMap)
				data.redToAlpha();
			tex.update(data);
		}
	}
	
	public void finishLoading() {
		for(Texture atlas:mAtlasses) {
			atlas.finish();
		}
	}
	
	private Texture loadTexture(String filename,TextureProperties textureProperties,boolean alphaMap) {
		if(mEnqueueMode) {
			Texture result = mGraphics.createTexture(textureProperties).generate();
			this.getImageDimensions(filename, mTempDim);
			//mTempDim.set(512, 512);
			result.mWidth = mTempDim.mWidth;
			result.mHeight = mTempDim.mHeight;
			if(alphaMap)
				result.mIsAlphaMap = true;
			enqueue(filename,result);
			return result;
		}else{
			TextureData data = derivedLoadImageData(filename,alphaMap);
			if(alphaMap)
				data.redToAlpha();
			Texture result = mGraphics.createAndInitTexture(data,textureProperties);
			result.mIsAlphaMap = alphaMap;
			result.finish();
			return result;
		}
	}
	
	public synchronized Texture getImage(String name,TextureProperties textureProperties,boolean alphaMap) {
		String filename = createExistingFilename(name);
		ResourceEntry entry = mTextures.get(filename);
		if(entry==null) {
			entry = new ResourceEntry();
			mTextures.put(filename, entry);
		}
		
		for(Texture tex:entry.mTextures) {
			if((textureProperties==null || tex.mProperties.equals(textureProperties)) && alphaMap==tex.mIsAlphaMap) {
				return tex;
			}
		}
		
		if(textureProperties==null)
			textureProperties = new TextureProperties();

		Texture texture = loadTexture(filename, textureProperties,alphaMap);
		entry.mTextures.add(texture);
		mGraphics.rebindTexture(0);
		
		return texture;
	}
	
	public synchronized Texture getImage(String name,TextureProperties textureProperties) {
		return getImage(name,textureProperties,false);
	}
	
	public void freeTexture(String name) {
		String filename = createExistingFilename(name);
		ResourceEntry entry = mTextures.get(filename);
		if(entry==null)
			return;
		for(Texture tex:entry.mTextures) {
			if(tex!=null)
				tex.free();
		}
		mTextures.remove(filename);
	}
	
	public Texture getImage(String name, TextureWrap wrapX, TextureWrap wrapY) {
		return getImage(name,new TextureProperties(wrapX,wrapY));
	}
	
	public Texture getImage(String name, TextureFilter textureFilter) {
		return getImage(name,new TextureProperties(textureFilter));
	}
	
	public Texture getImage(String name) {
		ResourceEntry entry = mTextures.get(name);
		if (entry != null)
			return entry.mTextures.get(0);
		else
			return getImage(name,new TextureProperties());
	}
	
	public Texture getAlphaMap(String name,TextureProperties textureProperties) {
		return getImage(name,textureProperties,true);
	}
	
	public Texture getAlphaMap(String name) {
		return getAlphaMap(name,new TextureProperties());
	}
	
	public Texture getAlphaMap(String name, TextureWrap wrapX, TextureWrap wrapY) {
		return getAlphaMap(name,new TextureProperties(wrapX,wrapY));
	}
	
	public Texture getAlphaMap(String name, TextureWrap wrapX, TextureWrap wrapY, TextureFilter textureFilter) {
		return getAlphaMap(name,new TextureProperties(wrapX,wrapY,textureFilter));
	}
	
//	public Texture createTextureAtlas(int width,int height,TextureProperties properties) {
//		TextureAtlas result = new TextureAtlas();
//		result.init(width,height,properties);
//		return result;
//	}
	
	public Texture createTextureAtlas(int width,int height,TextureProperties properties) {
		Texture result = mGraphics.createEmptyTexture(width, height, properties);
		mAtlasses.add(result);
		return result;
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
		for(Entry<String,ResourceEntry> entry:mTextures.entrySet()) {
			entry.getValue().clear();
		}
		for(Texture atlas:mAtlasses) {
			atlas.free();
		}
		mTextures.clear();
		mAtlasses.clear();
		mQueueBytes = 0;
	}

	public BitmapFont loadFont(String texAndDatafilename) {
		BitmapFont result = new BitmapFont();
		return result.init(texAndDatafilename,this);
	}
	
	public BitmapFont loadFont(Texture texture,String fontDataFilename) {
		BitmapFont result = new BitmapFont();
		return result.init(texture,fontDataFilename,mResources);
	}

	public void startEnqueuing() {
		mEnqueueMode = true;
	}

	public void divideQueueLoading(int steps) {
		if(steps<=0 || mQueueBytes<=0)
			mMaxQueueLoadingBytes = -1;
		else
			mMaxQueueLoadingBytes = mQueueBytes/steps;
	}
	
	public void clearQueue() {
		mQueueBytes = 0;
		mTexQueueId = 0;
	}

	public void reenqueueTextures() {
		clearQueue();
		for(Entry<String,ResourceEntry> entry:mTextures.entrySet()) {
			for(Texture tex:entry.getValue().mTextures) {
				if(!tex.isFinished())
					enqueue(entry.getKey(),tex);
			}
			for(SubTexture tex:entry.getValue().mSubTextures) {
				if(!tex.isFinished())
					enqueue(entry.getKey(),tex);
			}
		}
	}
	
	public String texturesToString() {
		StringBuilder result = new StringBuilder(320);
		for(Entry<String,ResourceEntry> entry:mTextures.entrySet()) {
			ResourceEntry res = entry.getValue();
			result.append(entry.getKey()+": ");
			boolean fst = true;
			for(Texture tex:res.mTextures) {
				if(!fst)
					result.append("; ");
				result.append(tex.toString());
				fst = false;
			}
			
			if(res.mSubTextures.size()>0) {
				result.append(" SUB TEXTURES: ");
				fst = true;
				for(SubTexture tex:res.mSubTextures) {
					if(!fst)
						result.append("; ");
					result.append(tex.toString());
					fst = false;
				}
			}
			result.append("\n");
		}
	
		return result.toString();
	}
	
	@Override
	public String toString() {
		return texturesToString();
	}
	
}
