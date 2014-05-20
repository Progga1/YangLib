package yang.pc.fileio;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import yang.graphics.textures.TextureData;
import yang.graphics.translator.AbstractGFXLoader;
import yang.graphics.translator.GraphicsTranslator;
import yang.math.objects.Dimensions2i;

public class PCGFXLoader extends AbstractGFXLoader {

	public PCGFXLoader(GraphicsTranslator graphics) {
		super(graphics,new PCResourceManager());
	}

	@Override
	public void getImageDimensions(String filename,Dimensions2i result) {

		final File file = ((PCResourceManager)mResources).getAssetFile(filename);
		if(file==null) {
			throw new RuntimeException("Image not found: "+filename);
		}
		final String path = file.getAbsolutePath();
		try {
			final BufferedImage image = ImageIO.read(new File(path));
			result.set(image.getWidth(),image.getHeight());
		} catch (final IOException e) {
			throw new RuntimeException("Image not found: "+path);
		}
	}

	@Override
	public TextureData loadImageData(InputStream stream,boolean forceRGBA) {
		BufferedImage image = null;

//		final String path =((PCResourceManager)mResources).getAssetFile(filename).getAbsolutePath();
		try {
			image = ImageIO.read(stream);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}

		final int width = image.getWidth();
		final int height = image.getHeight();

		final WritableRaster alphaBuffer = image.getAlphaRaster();
		final int channels = alphaBuffer!=null || forceRGBA?4:3;
		ByteBuffer buffer = getOrCreateTempBuffer(width,height,channels);

		if(alphaBuffer==null) {
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					final int rgb = image.getRGB(x, y);
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
					final int rgb = image.getRGB(x, y);
					final int alpha = alphaBuffer.getSample(x, y, 0);
					if(alpha<255 && TextureData.USE_PREMULTIPLICATION) {
						final float fac = alpha/255f;
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
