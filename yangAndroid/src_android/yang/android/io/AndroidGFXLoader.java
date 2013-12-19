package yang.android.io;


import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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

	public AndroidGFXLoader(AndroidGraphics graphics,Context context) {
		super(graphics,new AndroidResourceManager(context));
		mContext = context;
	}

	@Override
	protected void getImageDimensions(String filename,Dimensions2i result) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		AssetManager mgr = mContext.getAssets();
		InputStream is = null;

		try {
			is = mgr.open(filename);
		} catch(Exception e) {
			e.printStackTrace();
		}
		BitmapFactory.decodeStream(is,null,options);
		result.set(options.outWidth,options.outHeight);
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

//		BitmapFactory.Options opt = new BitmapFactory.Options();
//		opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
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


		boolean alpha = bmp.hasAlpha() || forceRGBA || AndroidGraphics.ALWAYS_RGBA;
		int channels = alpha?4:3;
		ByteBuffer tempBuf = getOrCreateTempBuffer(width,height,4);
		bmp.copyPixelsToBuffer(tempBuf);

		ByteBuffer uBuf = tempBuf;
		if(channels==3) {
			ByteBuffer rgbBuffer = ByteBuffer.allocateDirect(width*height*3).order(ByteOrder.nativeOrder());
			tempBuf.rewind();
			for(int i=0;i<rgbBuffer.capacity();i+=3) {
				rgbBuffer.put(tempBuf.get());
				rgbBuffer.put(tempBuf.get());
				rgbBuffer.put(tempBuf.get());
				tempBuf.get();
			}
			tempBuf.rewind();
			uBuf = rgbBuffer;
		}
		//System.out.println(filename+" "+channels+" "+bmp.hasAlpha()+" "+forceRGBA+" "+AndroidGraphics.ALWAYS_RGBA);
		bmp.recycle();
		bmp = null;
		uBuf.rewind();
		TextureData data = new TextureData(uBuf,width,height,channels);

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
