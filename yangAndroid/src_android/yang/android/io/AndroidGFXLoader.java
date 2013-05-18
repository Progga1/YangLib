package yang.android.io;


import java.io.InputStream;
import java.nio.ByteBuffer;

import yang.android.graphics.AndroidGraphics;
import yang.graphics.textures.TextureData;
import yang.graphics.textures.TextureSettings;
import yang.graphics.translator.AbstractGFXLoader;
import yang.graphics.translator.Texture;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class AndroidGFXLoader extends AbstractGFXLoader {
	
	private Context mContext;
	private ByteBuffer tempBuf = ByteBuffer.allocateDirect(2048*2048*4);
	
	public AndroidGFXLoader(AndroidGraphics graphics,Context context) {
		super(graphics,new AndroidResourceManager(context));
		mContext = context;
	}
	
	@Override
	public TextureData loadImageData(String name,boolean forceRGBA) {
		AssetManager mgr = mContext.getAssets();
		InputStream is = null;

		try {
			is = mgr.open(IMAGE_PATH+name+IMAGE_EXT);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		final Bitmap bmp = BitmapFactory.decodeStream(is);

		try { 
			is.close();
			is = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		
		int channels = bmp.hasAlpha() || forceRGBA?4:4;
		//ByteBuffer buf = ByteBuffer.allocateDirect(width*height*channels);	//TODO  !!!!! use one buffer!
		bmp.copyPixelsToBuffer(tempBuf);
		bmp.recycle();
		tempBuf.rewind();
		return new TextureData(tempBuf,width,height,channels);
	}
	
//	@Override
//	public Texture loadImage(String name,TextureSettings textureSettings,boolean redToAlpha) {
//		if(redToAlpha)
//			return super.loadImage(name, textureSettings, redToAlpha);
//		AssetManager mgr = mContext.getAssets();
//		InputStream is = null;
//
//		try {
//			is = mgr.open(IMAGE_PATH+name+IMAGE_EXT);
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
//		
//		final Bitmap bmp = BitmapFactory.decodeStream(is);
//
//		try { 
//			is.close();
//			is = null;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		int width = bmp.getWidth();
//		int height = bmp.getHeight();
//		
//		bmp.copyPixelsToBuffer(tempBuf);
//		bmp.recycle();
//		tempBuf.rewind();
//		return mGraphics.createTexture(tempBuf,width,height,textureSettings);
//	}

}
