package yang.gdx.fileio;

import java.nio.ByteBuffer;

import yang.gdx.graphics.GdxGraphicsTranslator;
import yang.graphics.AbstractGFXLoader;
import yang.graphics.textures.TextureData;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap.Format;


public class GdxGfxLoader extends AbstractGFXLoader {
	
	public GdxGfxLoader(GdxGraphicsTranslator gfx) {
		super(gfx,new GdxResourceManager());
	}
	
	@Override
	public TextureData loadImageData(String name,boolean forceRGBA) {		
		FileHandle handle = Gdx.files.internal(IMAGE_PATH+name+IMAGE_EXT);
		com.badlogic.gdx.graphics.Texture gdxTex = new com.badlogic.gdx.graphics.Texture(handle);
		gdxTex.getTextureData().prepare();
		
		int width = gdxTex.getWidth();
		int height = gdxTex.getHeight();
		
		Format format = gdxTex.getTextureData().getFormat();
		int channels = (format==Format.RGBA8888 || format==Format.RGBA4444)?4:3;
		ByteBuffer buf = ByteBuffer.allocateDirect(width * height * channels);

		buf.put(gdxTex.getTextureData().consumePixmap().getPixels());
		buf.rewind();
		
		return new TextureData(buf, gdxTex.getWidth(), gdxTex.getHeight(), channels);
	}

}
