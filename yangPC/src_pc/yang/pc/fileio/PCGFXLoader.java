package yang.pc.fileio;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import yang.graphics.textures.TextureData;
import yang.graphics.translator.AbstractGFXLoader;
import yang.graphics.translator.GraphicsTranslator;
import yang.model.PathSpecs;

public class PCGFXLoader extends AbstractGFXLoader {
	
	public PCGFXLoader(GraphicsTranslator graphics) {
		super(graphics,new PCResourceManager());
	}

	@Override
	public TextureData loadImageData(String filename,boolean forceRGBA) {
		ByteBuffer buffer = null;
		int width = 0;
		int height = 0;
		BufferedImage image = null;

		String path = PathSpecs.ASSET_PATH + IMAGE_PATH + filename + IMAGE_EXT;
		try {
			image = ImageIO.read(new File(path));
		} catch (IOException e) {
			throw new RuntimeException("Image not found: "+path);
		}
		
		width = image.getWidth();
		height = image.getHeight();

		WritableRaster alphaBuffer = image.getAlphaRaster();
		int channels = alphaBuffer!=null || forceRGBA?4:3;
		buffer = ByteBuffer.allocate(width * height * channels);

		if(alphaBuffer==null) {
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int rgb = image.getRGB(x, y);
					buffer.put((byte)(((rgb>>16)&0xFF)));
					buffer.put((byte)(((rgb>>8)&0xFF)));
					buffer.put((byte)(((rgb)&0xFF)));
					if(forceRGBA)
						buffer.put((byte)255);
				}
			}
		}else{
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int rgb = image.getRGB(x, y);
					int alpha = alphaBuffer.getSample(x, y, 0);
					if(alpha<255 && TextureData.USE_PREMULTIPLICATION) {
						float fac = alpha/255f;
						buffer.put((byte)(((rgb>>16)&0xFF)*fac));
						buffer.put((byte)(((rgb>>8)&0xFF)*fac));
						buffer.put((byte)(((rgb)&0xFF)*fac));
					}else{
						buffer.put((byte)(((rgb>>16)&0xFF)));
						buffer.put((byte)(((rgb>>8)&0xFF)));
						buffer.put((byte)(((rgb)&0xFF)));
					}
					
					buffer.put((byte)alpha);
				}
			}
		}
		buffer.rewind();

		return new TextureData(buffer,width,height,channels);
	}
	
}
