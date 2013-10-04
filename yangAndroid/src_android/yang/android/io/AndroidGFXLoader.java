package yang.android.io;


import java.io.InputStream;
import java.nio.ByteBuffer;

import yang.android.graphics.AndroidGraphics;
import yang.graphics.textures.TextureData;
import yang.graphics.translator.AbstractGFXLoader;
import yang.math.objects.Dimensions2i;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class AndroidGFXLoader extends AbstractGFXLoader {
	
	private Context mContext;
	private ByteBuffer mTempBuf;
	
	public AndroidGFXLoader(AndroidGraphics graphics,Context context) {
		super(graphics,new AndroidResourceManager(context));
		mContext = context;
	}
	
	@Override
	protected void getImageDimensions(String filename,Dimensions2i result) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap bmp = BitmapFactory.decodeFile(filename);
		result.set(bmp.getWidth(),bmp.getHeight());
	}
	
	@Override
	protected TextureData derivedLoadImageData(String filename,boolean forceRGBA) {
		AssetManager mgr = mContext.getAssets();
		InputStream is = null;

		try {
			is = mgr.open(filename);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		Bitmap bmp = BitmapFactory.decodeStream(is);

		try { 
			is.close();
			is = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		
//		if(mTempBuf==null)
//			mTempBuf = ByteBuffer.allocateDirect(2048*1024*4);
//		if((width>1024 || height>1024) && mTempBuf.capacity()<(2048*2048*4))
//			 mTempBuf = ByteBuffer.allocateDirect(2048*2048*4);
		
		
		int channels = bmp.hasAlpha()?4:4;
		mTempBuf = ByteBuffer.allocateDirect(width*height*channels);
		bmp.copyPixelsToBuffer(mTempBuf);
		bmp.recycle();
		bmp = null;
		mTempBuf.rewind();
		TextureData data =  new TextureData(mTempBuf,width,height,channels);
		mTempBuf = null;
		return data;
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
