package yang.graphics.translator;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map.Entry;

import yang.graphics.font.BitmapFont;
import yang.graphics.model.material.YangMaterialProvider;
import yang.graphics.model.material.YangMaterialSet;
import yang.graphics.textures.TextureCoordinatesQuad;
import yang.graphics.textures.TextureData;
import yang.graphics.textures.TextureProperties;
import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.textures.enums.TextureWrap;
import yang.math.objects.Dimensions2i;
import yang.systemdependent.AbstractResourceManager;
import yang.util.YangList;

public abstract class AbstractGFXLoader implements YangMaterialProvider{

	public static boolean REUSE_BUFFER = true;
	public static int MAX_TEXTURES = 1024;

	public static final String[] IMAGE_EXT	= {".png",".jpg",".bmp"};
	public static final String SHADER_EXT	= ".txt";

	protected String SHADER_PATH	= "shaders" + File.separatorChar;
	protected String[] IMAGE_PATH		= new String[]{"","textures"+File.separatorChar,"models"+File.separatorChar};
	protected String MATERIAL_PATH  = "models" + File.separatorChar;

	private class ResourceEntry {

		public YangList<Texture> mTextures = new YangList<Texture>();
		public YangList<SubTexture> mSubTextures = new YangList<SubTexture>();

		public void clear() {
			for(final Texture tex:mTextures)
				tex.free();
			mTextures.clear();
			mSubTextures.clear();
		}

	}

	public HashMap<String, ResourceEntry> mTextures;
	protected HashMap<String, String> mShaders;
	protected HashMap<String, YangMaterialSet> mMaterials;
	protected YangList<Texture> mAtlasses;
	protected GraphicsTranslator mGraphics;
	protected ByteBuffer mTempBuffer;
	protected int mMaxTexBytes = 0;

	public String[] mTexKeyQueue;
	public AbstractTexture[] mTexQueue;
	protected int mTexQueueId = 0;
	public boolean mEnqueueMode = false;
	protected int mQueueBytes = 0;
	public int mMaxQueueLoadingBytes = -1;
	private final Dimensions2i mTempDim = new Dimensions2i();

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
		mAtlasses = new YangList<Texture>();
		mGraphics = graphics;
		mResources = resources;
	}

	@Override
	public YangMaterialSet getMaterialSet(String name) {
		if(name.startsWith("./"))
			name = name.substring(2);
		YangMaterialSet result = mMaterials.get(name);
		if(result!=null)
			return result;
		result = new YangMaterialSet(this);
		final String filename = MATERIAL_PATH+name;
		if(!mResources.assetExists(filename))
			return null;
		try {
			result.loadFromStream(mResources.getAssetInputStream(filename));
		} catch (final IOException e) {
			return null;
		}
		mMaterials.put(name, result);
		return result;
	}

	public TextureData loadImageData(String path,String name,String ext,boolean forceRGBA) {
		final String filename = path + File.separatorChar+name+"."+ext;
		if(!mResources.assetExists(filename))
			return null;
		else
			return derivedLoadImageData(filename,forceRGBA);
	}

	public String getImageAssetFilename(String name) {
		return mResources.getAssetFilename(name,IMAGE_PATH,IMAGE_EXT);
	}

	protected TextureData loadImageData(String name,boolean forceRGBA) {
		final String filename = getImageAssetFilename(name);
		if(filename==null)
			return null;
		return derivedLoadImageData(filename,forceRGBA);
	}

	public TextureData loadImageData(String name) {
		return loadImageData(name,false);
	}

	protected ByteBuffer getOrCreateTempBuffer(int width,int height,int channels) {
		if(mTempBuffer!=null)
			return mTempBuffer;
		else
			return ByteBuffer.allocateDirect(width*height*channels);
	}

	public SubTexture loadIntoTexture(Texture target,String name,int x,int y) {
		final SubTexture result = new SubTexture(target);
		result.setPosition(x,y);
		final String filename = getImageAssetFilename(name);
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
			final TextureData imageData = loadImageData(filename,target.mProperties.mChannels>3);
			result.update(imageData);
		}
		resource.mSubTextures.add(result);
		return result;
	}

	public TextureCoordinatesQuad loadIntoTextureGetCoords(Texture target,String name,int x,int y, float biasX, float biasY) {
		SubTexture sub = loadIntoTexture(target, name, x, y);
		return new TextureCoordinatesQuad().initBiased(sub.mLeft, sub.mTop, sub.mLeft+sub.mWidth, sub.mTop+sub.mHeight, sub.mTexture.mWidth, sub.mTexture.mHeight, biasX, biasY);
	}

	private void enqueue(String key,AbstractTexture tex) {
		int size = tex.getWidth()*tex.getHeight()*4;
		if(size>mMaxTexBytes) {
			mMaxTexBytes = size;
		}
		mTexQueue[mTexQueueId] = tex;
		mTexKeyQueue[mTexQueueId++] = key;
		mQueueBytes += tex.getByteCount();
	}

	private String pollQueue() {
		if(mTexQueueId<=0)
			return null;
		final String tex = mTexKeyQueue[--mTexQueueId];
		mQueueBytes -= mTexQueue[mTexQueueId].getByteCount();
		return tex;
	}

	public void loadEnqueuedTextures() {
		if(REUSE_BUFFER && mTempBuffer==null && mMaxTexBytes>0) {
			mTempBuffer = ByteBuffer.allocateDirect(mMaxTexBytes).order(ByteOrder.nativeOrder());
		}
		mEnqueueMode = false;
		String texKey;
		final int minBytes = mMaxQueueLoadingBytes>0?mQueueBytes - mMaxQueueLoadingBytes:-1;
		while(mQueueBytes>minBytes && (texKey=pollQueue())!=null) {
			final AbstractTexture tex = mTexQueue[mTexQueueId];
			if(tex.isFinished())
				continue;
			TextureData data = loadImageData(texKey,tex.getChannels()>3);
			if(data==null)
				System.err.println("Image not found: "+texKey);
			if(tex.mIsAlphaMap)
				data.redToAlpha();

			tex.update(data);
			data = null;
		}
	}

	public void finishLoading() {
		for(final Texture atlas:mAtlasses) {
			atlas.finish();
		}
		mTempBuffer = null;
	}

	private Texture loadTexture(String filename,TextureProperties textureProperties,boolean alphaMap) {
		if(mEnqueueMode) {
			final Texture result = mGraphics.createTexture(textureProperties.clone()).generate();
			this.getImageDimensions(filename, mTempDim);
			//mTempDim.set(512, 512);
			result.mWidth = mTempDim.mWidth;
			result.mHeight = mTempDim.mHeight;
			if(alphaMap) {
				result.mIsAlphaMap = true;
				result.mProperties.mChannels = 4;
			}else{
				result.mProperties.mChannels = 0;
			}
			enqueue(filename,result);
			return result;
		}else{
			final TextureData data = derivedLoadImageData(filename,alphaMap);
			if(alphaMap)
				data.redToAlpha();
			textureProperties.mChannels = data.mChannels;
			final Texture result = mGraphics.createAndInitTexture(data,textureProperties);
			result.mIsAlphaMap = alphaMap;
			result.finish();
			return result;
		}
	}

	public synchronized Texture getImage(String name,TextureProperties textureProperties,boolean alphaMap) {
		final String filename = getImageAssetFilename(name);
		ResourceEntry entry = mTextures.get(filename);
		if(entry==null) {
			entry = new ResourceEntry();
			mTextures.put(filename, entry);
		}

		for(final Texture tex:entry.mTextures) {
			if((textureProperties==null || tex.mProperties.equals(textureProperties)) && alphaMap==tex.mIsAlphaMap) {
				return tex;
			}
		}

		if(textureProperties==null)
			textureProperties = new TextureProperties();

		final Texture texture = loadTexture(filename, textureProperties,alphaMap);
		entry.mTextures.add(texture);
//		mGraphics.rebindTexture(0);
		mGraphics.bindTextureNoFlush(mGraphics.mNoTexture,0);

		return texture;
	}

	public synchronized Texture getImage(String name,TextureProperties textureProperties) {
		return getImage(name,textureProperties,false);
	}

	public void freeTexture(String name) {
		final String filename = getImageAssetFilename(name);
		final ResourceEntry entry = mTextures.get(filename);
		if(entry==null)
			return;
		for(final Texture tex:entry.mTextures) {
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
		final ResourceEntry entry = mTextures.get(name);
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
		final Texture result = mGraphics.createEmptyTexture(width, height, properties);
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
		for(final Entry<String,ResourceEntry> entry:mTextures.entrySet()) {
			entry.getValue().clear();
		}
		for(final Texture atlas:mAtlasses) {
			atlas.free();
		}
		mTextures.clear();
		mAtlasses.clear();
		mQueueBytes = 0;
	}

	public BitmapFont loadFont(String texAndDatafilename) {
		final BitmapFont result = new BitmapFont();
		return result.init(texAndDatafilename,this);
	}

	public BitmapFont loadFont(Texture texture,String fontDataFilename) {
		final BitmapFont result = new BitmapFont();
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
		for(final Entry<String,ResourceEntry> entry:mTextures.entrySet()) {
			for(final Texture tex:entry.getValue().mTextures) {
				if(!tex.isFinished())
					enqueue(entry.getKey(),tex);
			}
			for(final SubTexture tex:entry.getValue().mSubTextures) {
				if(!tex.isFinished())
					enqueue(entry.getKey(),tex);
			}
		}
	}

	public String texturesToString() {
		final StringBuilder result = new StringBuilder(320);
		for(final Entry<String,ResourceEntry> entry:mTextures.entrySet()) {
			final ResourceEntry res = entry.getValue();
			result.append(entry.getKey()+": ");
			boolean fst = true;
			for(final Texture tex:res.mTextures) {
				if(!fst)
					result.append("; ");
				result.append(tex.toString());
				fst = false;
			}

			if(res.mSubTextures.size()>0) {
				result.append(" SUB TEXTURES: ");
				fst = true;
				for(final SubTexture tex:res.mSubTextures) {
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
