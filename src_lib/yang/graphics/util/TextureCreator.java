package yang.graphics.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import yang.graphics.FloatColor;
import yang.graphics.textures.TextureData;
import yang.model.map.DefaultIntMap;
import yang.model.map.IntMap;



public class TextureCreator {

	public static TextureData createGrayScaleTexture(float[][] values, float normValue, float offset, int channels) {
		int w = values[0].length;
		int h = values.length;
		float factor = 1/normValue;
		ByteBuffer source = ByteBuffer.allocateDirect(w*h*channels).order(ByteOrder.nativeOrder());
		source.rewind();
		for(int y=0;y<h;y++) {
			float[] valueLine = values[h-1-y];
			for(int x=0;x<w;x++) {
				float value = (valueLine[x]+offset)*factor;
				if(value>1)
					value=1;
				if(value<0)
					value=0;
				byte byteValue = (byte)(value*255);
				source.put(byteValue);
				if(channels>1) {
					source.put(byteValue);
					source.put(byteValue);
					if(channels>3)
						source.put((byte)255);
				}
			}
		}
		source.rewind();
		return new TextureData(source,w,h,channels);
	}
	
	public static TextureData createGrayScaleTexture(float[][] values,int channels) {
		return createGrayScaleTexture(values,1,0,channels);
	}
	
	public static TextureData createGrayScaleTexture(float[][] values) {
		return createGrayScaleTexture(values,1,0,3);
	}
	
	public static TextureData createTextureFromArray(IntMap map,FloatColor[] palette, boolean includeAlpha) {
		int w = map.getWidth();
		int h = map.getHeight();
		int channels = (includeAlpha?4:3);
		ByteBuffer source = ByteBuffer.allocateDirect(w*h*channels);
		source.rewind();
		for(int y=0;y<h;y++) {
			for(int x=0;x<w;x++) {
				int i = map.getValue(x, y);
				if(i<0)
					i=0;
				palette[i%palette.length].writeIntoBuffer(source,includeAlpha);
			}
		}
		source.rewind();
		return new TextureData(source,w,h,channels);
	}
	
	public static TextureData createTextureFromArray(int[][] map,FloatColor[] palette, boolean includeAlpha) {
		return createTextureFromArray(new DefaultIntMap(map),palette,includeAlpha);
	}
	
}
