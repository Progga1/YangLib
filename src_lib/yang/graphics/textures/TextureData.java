package yang.graphics.textures;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import yang.graphics.model.FloatColor;
import yang.graphics.textures.enums.TextureWrap;

public class TextureData {

	public static boolean USE_PREMULTIPLICATION = true;
	public final static float d255 = 1/255f;

	public ByteBuffer mData;
	private ByteBuffer mTemp;
	public int mWidth;
	public int mHeight;
	public int mChannels;
	private int mCapacity;
	private static byte[] tempArray4b = new byte[4];
	private static byte[] tempArray3b = new byte[3];

	public TextureData(ByteBuffer data,int width,int height,int channels) {
		mData = data;
		mWidth = width;
		mHeight = height;
		mChannels = channels;
		mCapacity = width*height*channels;
	}

	public TextureData(int width,int height,int channels) {
		this(ByteBuffer.allocateDirect(width*height*channels),width,height,channels);
	}

	public TextureData(int width,int height) {
		this(width,height,4);
	}

	private void initTemp() {
		if(mTemp==null)
			mTemp = mData.duplicate();
	}

	public void copyRect(int left,int top,int width,int height, ByteBuffer source, int sourceChannels,int sourceLeft,int sourceTop,int sourceBufferWidth, int downScale) {

		if(downScale==1 && sourceChannels==mChannels) {
			//Fast version for matching formats
			for(int i=0;i<height;i++) {
				mData.position(((top+i)*mWidth+left)*sourceChannels);
				source.position((sourceTop+i)*sourceBufferWidth*sourceChannels);
				for(int j=0;j<width;j++) {
					for(int c=0;c<sourceChannels;c++) {
						mData.put(source.get());
					}
				}
			}
		}else{
			//Copy converted
			tempArray4b[0] = 0;
			tempArray4b[1] = 0;
			tempArray4b[2] = 0;
			tempArray4b[3] = 0;
			width/=downScale;
			height/=downScale;

			for(int i=0;i<height;i++) {
				mData.position(((top+i)*mWidth+left)*mChannels);
				source.position((sourceTop+i*downScale)*sourceBufferWidth*sourceChannels);
				for(int j=0;j<width;j++) {
					for(int d=0;d<downScale;d++)
						for(int c=0;c<sourceChannels;c++) {
							tempArray4b[c] = source.get();
						}
					for(int c=0;c<mChannels;c++) {
						mData.put(tempArray4b[c]);
					}
				}
			}
		}
	}

	public int getIndex(int x,int y) {
		return ((y*mWidth+x)*mChannels);
	}

	public void setPosition(int x,int y) {
		mData.position(getIndex(x,y));
	}

	private void copyFromWorkingBuffer(int pixels) {
		mTemp.limit(mTemp.position()+pixels*mChannels);
		mData.put(mTemp);
		mTemp.limit(mCapacity);
	}

	public void copyRect(int left,int top, TextureData source, int downScale) {
		copyRect(left,top,source.mWidth,source.mHeight,source.mData,source.mChannels,0,0,source.mWidth,downScale);
	}

	public void copyRect(int left,int top, int targetWidth, TextureData source) {
		copyRect(left,top,source,source.mWidth/targetWidth);
	}

	public TextureData redToAlpha() {
		mData.rewind();
		if(USE_PREMULTIPLICATION) {
			for(int i=0;i<mWidth*mHeight;i++) {
				int alpha = mData.get();
				alpha = alpha<0?alpha+255:alpha;
				mData.position(i*4);
				final byte b = (byte)(alpha);
				mData.put(b);
				mData.put(b);
				mData.put(b);
				mData.put(b);
			}
		}else{
			for(int i=0;i<mWidth*mHeight;i++) {
				int alpha = mData.get();
				alpha = alpha<0?alpha+255:alpha;
				mData.position(i*4);
				mData.put((byte)(255));
				mData.put((byte)(255));
				mData.put((byte)(255));
				mData.put((byte)(alpha));
			}
		}
		mData.rewind();
		return this;
	}

	public TextureCoordBounds createBiasBorder(int left,int top,int width,int height, int borderX, int borderY, TextureWrap wrapX,TextureWrap wrapY) {
		initTemp();
		left += borderX;
		top += borderY;
		width -= borderX*2;
		height -= borderY*2;
		final int right = left+width-1;
		final int bottom = top+height-1;
		if(wrapX==TextureWrap.MIRROR)
			wrapX = TextureWrap.CLAMP;
		if(wrapY==TextureWrap.MIRROR)
			wrapY = TextureWrap.CLAMP;
		for(int i=0;i<borderY;i++) {
			//TOP
			mData.position(getIndex(left,top-i-1));
			if(wrapY==TextureWrap.CLAMP)
				mTemp.position(getIndex(left,top));
			if(wrapY==TextureWrap.REPEAT)
				mTemp.position(getIndex(left,bottom-i));
			if(wrapY==TextureWrap.MIRROR)
				mTemp.position(getIndex(left,top+i+1));
			copyFromWorkingBuffer(width);
			//BOTTOM
			mData.position(getIndex(left,bottom+i+1));
			if(wrapY==TextureWrap.CLAMP)
				mTemp.position(getIndex(left,bottom));
			if(wrapY==TextureWrap.REPEAT)
				mTemp.position(getIndex(left,top+i));
			if(wrapY==TextureWrap.MIRROR)
				mTemp.position(getIndex(left,bottom-i-1));
			copyFromWorkingBuffer(width);
		}

		//LEFT
		for(int i=-borderY;i<height+borderY;i++) {
			final int y = top+i;
			if(wrapX==TextureWrap.REPEAT) {
				//left
				mData.position(getIndex(left-borderX,y));
				mTemp.position(getIndex(right-borderX,y));
				copyFromWorkingBuffer(borderX);
				//right
				mData.position(getIndex(right+1,y));
				mTemp.position(getIndex(left,y));
				copyFromWorkingBuffer(borderX);
			}
			if(wrapX==TextureWrap.CLAMP) {
				//left
				mData.position(getIndex(left-borderX,y));
				mTemp.position(getIndex(left,y));
				for(int c=0;c<mChannels;c++)
					tempArray4b[c] = mTemp.get();
				for(int j=0;j<borderX;j++)
					mData.put(tempArray4b,0,mChannels);
				//right
				mData.position(getIndex(right+1,y));
				mTemp.position(getIndex(right,y));
				for(int c=0;c<mChannels;c++)
					tempArray4b[c] = mTemp.get();
				for(int j=0;j<borderX;j++)
					mData.put(tempArray4b,0,mChannels);
			}
		}

		return new TextureCoordBounds().setBiased(left, top, width, height, mWidth, mHeight, 0);
	}

	public TextureCoordBounds createBiasBorder(int left,int top,int width,int height, int border, TextureWrap wrapX,TextureWrap wrapY) {
		return createBiasBorder(left,top,width,height,border,border,wrapX,wrapY);
	}

	public TextureCoordBounds createBiasRepeatBorder(int left,int top,int width,int height, int border) {
		return createBiasBorder(left,top,width,height,border,TextureWrap.REPEAT,TextureWrap.REPEAT);
	}

	public TextureCoordBounds copyWithMargin(int left,int top,int destWidth,int destHeight,TextureData source,int downScale,TextureWrap wrapX,TextureWrap wrapY) {
		final int sourceW = source.mWidth/downScale;
		final int sourceH = source.mHeight/downScale;
		final int borderX = (destWidth-sourceW)/2;
		final int borderY = (destHeight-sourceH)/2;
		copyRect(left+borderX,top+borderY,source,downScale);
		return createBiasBorder(left,top,destWidth,destHeight,borderX,borderY,wrapX,wrapY);
	}

	public TextureCoordBounds copyWithRepeatMargin(int left,int top,int destWidth,int destHeight,TextureData source,int downScale) {
		return copyWithMargin(left,top,destWidth,destHeight,source,downScale,TextureWrap.REPEAT,TextureWrap.REPEAT);
	}

	public static ByteBuffer createSingleColorBuffer(int width,int height,TextureProperties texProperties,FloatColor color) {
		final int channels = texProperties.mChannels;
		final int bytes = width*height;
		final ByteBuffer buf = ByteBuffer.allocateDirect(bytes*channels).order(ByteOrder.nativeOrder());
		for(int i=0;i<bytes;i++) {
			for(int j=0;j<channels;j++)
				buf.put((byte)(color.mValues[j]*255));
		}
		return buf;
	}

	public void multAlpha(TextureData alphaData) {
		final ByteBuffer buf = alphaData.mData;
		buf.rewind();
		mData.rewind();
		for(int i=0;i<mWidth*mHeight;i++) {
			buf.position(i*alphaData.mChannels);
			int srcAlpha = buf.get();
			srcAlpha = srcAlpha<0?srcAlpha+255:srcAlpha;
			final float srcAlphaNorm = srcAlpha*d255;

			if(USE_PREMULTIPLICATION) {
				mData.position(i*4);
				int red = mData.get();
				if(red<0)
					red += 255;
				int green = mData.get();
				if(green<0)
					green += 255;
				int blue = mData.get();
				if(blue<0)
					blue += 255;
				int alpha = mData.get();
				if(alpha<0)
					alpha += 255;
				mData.position(i*4);
				mData.put((byte)(red*srcAlphaNorm));
				mData.put((byte)(green*srcAlphaNorm));
				mData.put((byte)(blue*srcAlphaNorm));
				mData.put((byte)(alpha*srcAlphaNorm));
			}else{
				mData.position(i*4+3);
				final float alpha = mData.get()*d255;
				mData.position(i*4+3);
				mData.put((byte)(srcAlpha*alpha));
			}
		}
	}

	public static void rgbaToBGRA(ByteBuffer buffer) {
		int c = buffer.capacity();
		buffer.position(0);
		for(int i=0;i<c;i+=4) {
			buffer.get(tempArray4b);
			buffer.position(i);
			buffer.put(tempArray4b[2]);
			buffer.put(tempArray4b[1]);
			buffer.put(tempArray4b[0]);
			buffer.put(tempArray4b[3]);
		}
		buffer.position(0);
	}

	public static void removeAlpha(ByteBuffer buffer,byte[] colors) {
		int c = buffer.capacity();
		buffer.position(0);
		for(int i=0;i<c;i+=4) {
			buffer.put(colors[i]);
			buffer.put(colors[i+1]);
			buffer.put(colors[i+2]);
		}
		buffer.position(0);
	}

	public static void bgraToRGBA(ByteBuffer buffer,byte[] colors) {
		int c = buffer.capacity();
		buffer.position(0);
		for(int i=0;i<c;i+=4) {
			buffer.put(colors[i+2]);
			buffer.put(colors[i+1]);
			buffer.put(colors[i]);
			buffer.put(colors[i+3]);
		}
		buffer.position(0);
	}

	public static void bgraToRGB(ByteBuffer buffer,byte[] colors) {
		int c = buffer.capacity();
		buffer.position(0);
		for(int i=0;i<c;i+=4) {
			buffer.put(colors[i+2]);
			buffer.put(colors[i+1]);
			buffer.put(colors[i]);
		}
		buffer.position(0);
	}

	public static void bgrToRGB(ByteBuffer buffer,byte[] colors) {
		int c = buffer.capacity();
		buffer.position(0);
		for(int i=0;i<c;i+=3) {
			buffer.put(colors[i+2]);
			buffer.put(colors[i+1]);
			buffer.put(colors[i]);
		}
		buffer.position(0);
	}

	public static void swapRGB(ByteBuffer buffer) {
		int c = buffer.capacity();
		buffer.position(0);
		for(int i=0;i<c;i+=3) {
			buffer.get(tempArray3b);
			buffer.position(i);
			buffer.put(tempArray3b[2]);
			buffer.put(tempArray3b[1]);
			buffer.put(tempArray3b[0]);
		}
		buffer.position(0);
	}

	public void resize(int newWidth, int newHeight,boolean forceRecreation) {
		mCapacity = newWidth*newHeight*4;
		if(forceRecreation || newWidth>=mWidth || newHeight>=mHeight)
			mData = ByteBuffer.allocateDirect(newWidth*newHeight*4);
		mData.limit(mCapacity);
		mWidth = newWidth;
		mHeight = newHeight;
	}

}
