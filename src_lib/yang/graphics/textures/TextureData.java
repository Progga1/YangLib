package yang.graphics.textures;

import java.nio.ByteBuffer;

import yang.graphics.textures.enums.TextureWrap;

public class TextureData {

	public static boolean USE_PREMULTIPLICATION = true;
	
	public ByteBuffer mData;
	private ByteBuffer mTemp;
	public int mWidth;
	public int mHeight;
	public int mChannels;
	private byte mInterColors[] = new byte[4];
	
	public TextureData(ByteBuffer data,int width,int height,int channels) {
		mData = data;
		mTemp = data.duplicate();
		mWidth = width;
		mHeight = height;
		mChannels = channels;
	}
	
	public TextureData(int width,int height,int channels) {
		mData = ByteBuffer.allocateDirect(width*height*channels);
		mWidth = width;
		mHeight = height;
		mChannels = channels;
	}
	
	public TextureData(int width,int height) {
		this(width,height,4);
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
			mInterColors[0] = 0;
			mInterColors[1] = 0;
			mInterColors[2] = 0;
			mInterColors[3] = 0;
			width/=downScale;
			height/=downScale;
			for(int i=0;i<height;i++) {
				mData.position(((top+i)*mWidth+left)*mChannels);
				source.position((sourceTop+i*downScale)*sourceBufferWidth*sourceChannels);
				for(int j=0;j<width;j++) {
					for(int d=0;d<downScale;d++)
						for(int c=0;c<sourceChannels;c++) {
							mInterColors[c] = source.get();
						}
					for(int c=0;c<mChannels;c++) {
						mData.put(mInterColors[c]);
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
		mTemp.limit(mTemp.capacity());
	}
	
	private static byte[] tempArray = new byte[4];
	
	public void createBiasBorder(int left,int top,int width,int height, int border, TextureWrap wrapX,TextureWrap wrapY) {
		int right = left+width-1;
		int bottom = top+height-1;
		if(wrapX==TextureWrap.MIRROR)
			wrapX = TextureWrap.CLAMP;
		if(wrapY==TextureWrap.MIRROR)
			wrapY = TextureWrap.CLAMP;
		for(int i=0;i<border;i++) {
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
		for(int i=-border;i<height+border;i++) {
			int y = top+i;
			if(wrapX==TextureWrap.REPEAT) {
				//left
				mData.position(getIndex(left-border,y));
				mTemp.position(getIndex(right-border,y));
				copyFromWorkingBuffer(border);
				//right
				mData.position(getIndex(right+1,y));
				mTemp.position(getIndex(left,y));
				copyFromWorkingBuffer(border);
			}
			if(wrapX==TextureWrap.CLAMP) {
				//left
				mData.position(getIndex(left-border,y));
				mTemp.position(getIndex(left,y));
				for(int c=0;c<mChannels;c++)
					tempArray[c] = mTemp.get();
				for(int j=0;j<border;j++)
					mData.put(tempArray,0,mChannels);
				//right
				mData.position(getIndex(right+1,y));
				mTemp.position(getIndex(right,y));
				for(int c=0;c<mChannels;c++)
					tempArray[c] = mTemp.get();
				for(int j=0;j<border;j++)
					mData.put(tempArray,0,mChannels);
			}
		}
	}
	
	public void copyRect(int left,int top, TextureData source, int downScale) {
		copyRect(left,top,source.mWidth,source.mHeight,source.mData,source.mChannels,0,0,source.mWidth,downScale);
	}
	
	public void copyRect(int left,int top, int targetWidth, TextureData source) {
		copyRect(left,top,source,source.mWidth/targetWidth);
	}
	
	public TextureData redToAlpha() {//if(true)return this;
		mData.rewind();
		if(USE_PREMULTIPLICATION) {
			for(int i=0;i<mWidth*mHeight;i++) {
				int alpha = mData.get();
				alpha = alpha<0?alpha+255:alpha;
				mData.position(i*4);
				byte b = (byte)(alpha);
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
	
}
