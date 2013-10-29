package yang.graphics.translator;

import yang.graphics.textures.TextureProperties;
import yang.util.YangList;

public class TextureAtlas {

	public GraphicsTranslator mGraphics;
	public YangList<AbstractTexture> mSubTextures = new YangList<AbstractTexture>();
	public Texture mMainTexture;
	
	public TextureAtlas init(Texture mainTexture) {
		mMainTexture = mainTexture;
		return this;
	}
	
	public TextureAtlas init(int width,int height,TextureProperties properties) {
		mMainTexture = mGraphics.createEmptyTexture(width, height, properties);
		return this;
	}
	
	public void addSubTexture(AbstractTexture texture) {
		mSubTextures.add(texture);
	}
	
	
	
}
