package yang.graphics.textures;

import java.nio.ByteBuffer;

public class TextureData {

	public static boolean USE_PREMULTIPLICATION = true;
	
	public ByteBuffer mData;
	public int mWidth;
	public int mHeight;
	public int mChannels;
	private byte mInterColors[] = new byte[4];
	
	public TextureData(ByteBuffer data,int width,int height,int channels) {
		mData = data;
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
