package yang.android.io;


import java.io.InputStream;
import java.nio.ByteBuffer;

import yang.android.graphics.AndroidGraphics;
import yang.graphics.AbstractGFXLoader;
import yang.graphics.textures.TextureData;
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
	public TextureData loadImageData(String name) {
		Bitmap bmp = null;
		
		AssetManager mgr = mContext.getAssets();
		InputStream is = null;
		
		try {
			is = mgr.open(IMAGE_PATH+name+IMAGE_EXT);
			bmp = BitmapFactory.decodeStream(is);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try { 
				is.close();
				is = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		int width = bmp.getWidth();
		int height = bmp.getHeight();
		
		int channels = bmp.hasAlpha()?4:4;
		ByteBuffer buf = ByteBuffer.allocateDirect(width*height*channels);
		bmp.copyPixelsToBuffer(buf);
		buf.rewind();
		
		return new TextureData(buf,width,height,channels);
	}

}
