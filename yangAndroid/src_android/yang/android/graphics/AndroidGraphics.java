package yang.android.graphics;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import yang.android.io.AndroidGFXLoader;
import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.programs.GLProgram;
import yang.graphics.textures.TextureProperties;
import yang.graphics.textures.TextureRenderTarget;
import yang.graphics.translator.GraphicsTranslator;
import yang.model.DebugYang;
import yang.model.enums.ByteFormat;
import android.content.Context;
import android.opengl.ETC1;
import android.opengl.GLES20;

public class AndroidGraphics extends GraphicsTranslator {

	public static boolean ALWAYS_RGBA = false;
	public static boolean USE_TEXTURE_COMPRESSION = false;

	public Context mContext;

	public AndroidGraphics(Context context) {
		super();
		mDriverKey = "ANDROID_GLES20";
		mContext = context;

		mGFXLoader = new AndroidGFXLoader(this,mContext);
	}

	public static final boolean clearError() {
		GLES20.glGetError();
		return true;
	}

	public static final boolean checkError(String message,boolean pre) {
		int error = GLES20.glGetError();
		if (error != 0) {
			if(pre)
				System.err.println("----!GLES-ERROR!---- [PRE] "+message);
			else
				System.err.println("----!GLES-ERROR!---- " + message + ": "+errorCodeToString(error));
			return false;
		} else {
			return true;
		}
	}

	public static final boolean checkError(String message) {
		return checkError(message,false);
	}

	public static int channelsToConst(int channels) {
		switch(channels) {
		case 3:
			return GLES20.GL_RGB;
		case 4:
			return GLES20.GL_RGBA;
			default: throw new RuntimeException(channels + " channels not supported.");
		}
	}

	public static int byteFormatToConst(ByteFormat byteFormat) {
		switch(byteFormat) {
		case BYTE: return GLES20.GL_BYTE;
		case UNSIGNED_BYTE: return GLES20.GL_UNSIGNED_BYTE;
		case SHORT: return GLES20.GL_SHORT;
		case INT: return GLES20.GL_INT;
		case FLOAT: return GLES20.GL_FLOAT;
		default: throw new RuntimeException(byteFormat+" not supported");
		}
	}

	@Override
	public void postInit() {
		GLES20.glDepthFunc(GLES20.GL_LEQUAL);
	}

	@Override
	public void setMaxFPS(int fps) {
		if(fps>=60)
			return;
		else
			super.setMaxFPS(fps);
	}

	@Override
	public int getDefaultMetaBaseKey() {
		return '1';
	}

	@Override
	public GLProgram createProgram() {
		return new AndroidGLProgram();
	}

	@Override
	public void setClearColor(float r, float g, float b,float a) {
		GLES20.glClearColor(r,g,b,a);
	}

	@Override
	public void clear(int mask) {
		assert preCheck("Clear");
		GLES20.glClear(mask);
		assert checkError("Clear");
	}

	@Override
	public void genTextures(int[] target,int count) {
		assert preCheck("Generate texture");
		GLES20.glGenTextures(count, target, 0);
		assert checkError("Generate texture");
	}

	@Override
	public void setTextureParameter(int pName, int param) {
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, pName, param);
	}

	public int getUnsigned(ByteBuffer buffer) {
		int res = buffer.get();
		if(res<0)
			return res+256;
		else
			return res;
	}

	@Override
	public void derivedSetTextureData(int texId,int width,int height,ByteBuffer buffer,TextureProperties properties) {

//		int[] results = new int[20];
//        GLES20.glGetIntegerv(GLES20.GL_NUM_COMPRESSED_TEXTURE_FORMATS, results, 0);
//        int numFormats = results[0];
//        if (numFormats > results.length) {
//            results = new int[numFormats];
//        }
//        GLES20.glGetIntegerv(GLES20.GL_COMPRESSED_TEXTURE_FORMATS, results, 0);
//        for (int i = 0; i < numFormats; i++) {
//            if (results[i] == ETC1.ETC1_RGB8_OES) {
//            }
//        }

		int channels = properties.mChannels;
		assert preCheck("Set texture data");
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
		assert checkError("Bind new texture");
		GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, GLES20.GL_TRUE);

		int format;
		int outFormat;
		switch(channels) {
//		case 1:
//			format = GLES20.GL_LUMINANCE;
//			outFormat = GLES20.GL_RGB;
//			break;
		case 3:
			format = GLES20.GL_RGB;
			outFormat = GLES20.GL_RGB;
			break;
		case 4:
			format = GLES20.GL_RGBA;
			outFormat = GLES20.GL_RGBA;
			break;
			default: throw new RuntimeException(channels + " channels not supported.");
		}

		if(USE_TEXTURE_COMPRESSION && properties.mCompressIfPossible && channels==3 && buffer!=null) {

			ByteBuffer tempBuf = ByteBuffer.allocateDirect(width*height*3);
			int minSize = ETC1.getEncodedDataSize(4,4);
			int size = minSize;
			int i=0;
			DebugYang.println("COMPRESS: "+width+"x"+height,1);
			do {
				if(i>0 && width>=2) {
					buffer.rewind();

					int stride = width*3;
					for(int y=0;y<height;y++) {
						for(int x=0;x<width;x++) {
							int idX = x*2*3;
							int idY = y*2*stride*2;
							buffer.position(idX + idY);
							int r = getUnsigned(buffer);
							int g = getUnsigned(buffer);
							int b = getUnsigned(buffer);
							buffer.position(idX+3 + idY);
							r += getUnsigned(buffer);
							g += getUnsigned(buffer);
							b += getUnsigned(buffer);
							buffer.position(idX + idY+stride*2);
							r += getUnsigned(buffer);
							g += getUnsigned(buffer);
							b += getUnsigned(buffer);
							buffer.position(idX+3 + idY+stride*2);
							r += getUnsigned(buffer);
							g += getUnsigned(buffer);
							b += getUnsigned(buffer);
							buffer.position(x*3+y*stride);
							buffer.put((byte)(r/4));
							buffer.put((byte)(g/4));
							buffer.put((byte)(b/4));
						}
					}
				}
				if(width>=4)
					size = ETC1.getEncodedDataSize(width, height);
				else
					size = minSize;
				tempBuf.rewind();
				buffer.rewind();
				ETC1.encodeImage(buffer, width, height, 3, width*3, tempBuf);
				GLES20.glCompressedTexImage2D(GLES20.GL_TEXTURE_2D, i, ETC1.ETC1_RGB8_OES, width, height, 0, size, tempBuf);
				//GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, i, format, width, height, 0, outFormat, GLES20.GL_UNSIGNED_BYTE, buffer);
				width /= 2;
				height /= 2;
				i++;
			}while(width>=1 && properties.isMipMap());

		}else
			GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, format, width, height, 0, outFormat, GLES20.GL_UNSIGNED_BYTE, buffer);
		assert checkError("Pass texture data");

	}

	@Override
	public void generateMipMap() {
		assert preCheck("Generate mip map");
		GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
		assert checkError("Generate mip map");
	}

	@Override
	public void setTextureRectData(int texId,int level,int offsetX,int offsetY,int width,int height,int channels, ByteBuffer data) {
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
		GLES20.glTexSubImage2D(GLES20.GL_TEXTURE_2D, level, offsetX,offsetY,width,height, channelsToConst(channels), GLES20.GL_UNSIGNED_BYTE, data);
	}

	@Override
	protected void drawDefaultVertices(int bufferStart, int drawVertexCount,int mode,ShortBuffer indexBuffer) {
		int lim = indexBuffer.capacity();
		indexBuffer.position(bufferStart);
		indexBuffer.limit(bufferStart+drawVertexCount);
		GLES20.glDrawElements(mode, drawVertexCount, GLES20.GL_UNSIGNED_SHORT, indexBuffer);
		indexBuffer.limit(lim);
	}

	@Override
	public void derivedSetAttributeBuffer(int handle, int bufferIndex,IndexedVertexBuffer vertexBuffer) {
		GLES20.glVertexAttribPointer(handle, vertexBuffer.mFloatBufferElementSizes[bufferIndex], GLES20.GL_FLOAT, false, 0, vertexBuffer.getFloatBuffer(bufferIndex));
	}

	@Override
	public void enableAttributePointer(int handle) {
		GLES20.glEnableVertexAttribArray(handle);
	}

	@Override
	public void disableAttributePointer(int handle) {
		GLES20.glDisableVertexAttribArray(handle);
	}

	@Override
	protected void setViewPort(int width, int height) {
		GLES20.glViewport(0, 0, width, height);
	}

	@Override
	public void setCullMode(boolean drawClockwise) {
		if(drawClockwise)
			GLES20.glCullFace(GLES20.GL_FRONT);
		else
			GLES20.glCullFace(GLES20.GL_BACK);
	}

	@Override
	public void deleteTextures(int[] ids) {
		assert preCheck("Delete textures");
		GLES20.glDeleteTextures(ids.length, ids, 0);
		assert checkError("Delete textures");
	}

	@Override
	protected void deleteBuffers(int[] bufIds) {
		GLES20.glDeleteBuffers(bufIds.length,bufIds,0);
	}

	@Override
	protected void deleteFrameBuffers(int[] bufIds) {
		GLES20.glDeleteFramebuffers(bufIds.length,bufIds,0);
	}

	@Override
	public void derivedSetScreenRenderTarget() {
		assert preCheck("Set screen render target");
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
		assert checkError("Set screen render target");
	}

	@Override
	public void initRenderTarget(TextureRenderTarget target) {
		assert preCheck("Create render target");
		GLES20.glGenFramebuffers(1, mTempInt, 0);
		GLES20.glGenRenderbuffers(1, mTempInt2, 0);
		assert checkError("Create render target buffers");
		int frameId = mTempInt[0];
		int depthId = mTempInt2[0];
		GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, depthId);
		GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, target.getWidth(), target.getHeight());
		target.set(frameId,depthId);
		assert checkError("Create render target");
	}

	@Override
	public void derivedSetTextureRenderTarget(TextureRenderTarget renderTarget) {
		assert preCheck("Set texture render target");
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, renderTarget.mFrameBufferId);
		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, renderTarget.mTargetTexture.mId, 0);
		GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, renderTarget.mDepthBufferId);
		assert checkError("Set texture render target");
	}

	@Override
	public void setDepthFunction(boolean less,boolean equal) {
		if(less)
			GLES20.glDepthFunc(equal?GLES20.GL_LEQUAL:GLES20.GL_LESS);
		else
			GLES20.glDepthFunc(equal?GLES20.GL_GEQUAL:GLES20.GL_GREATER);
		assert checkError("Set depth function");
	}

	@Override
	public void bindTexture(int texId,int level) {
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0+level);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);
		assert checkError("Bind texture");
	}

	@Override
	public void readPixels(int x,int y,int width,int height,int channels,ByteFormat byteFormat,ByteBuffer target) {
		if(byteFormat!=ByteFormat.UNSIGNED_BYTE)
			throw new RuntimeException("Android read pixels only supports unsigned byte");
		assert preCheck("Read pixels");
		GLES20.glReadPixels(x, y, width, height, channelsToConst(channels), GLES20.GL_UNSIGNED_BYTE, target);
		assert checkError("Read pixels");
	}

	@Override
	public boolean checkErrorInst(String message,boolean pre) {
		return checkError(message,pre);
	}

	@Override
	public void enable(int glConstant) {
		GLES20.glEnable(glConstant);
	}

	@Override
	public void disable(int glConstant) {
		GLES20.glDisable(glConstant);
	}

	@Override
	public void setBlendFunction(int sourceFactor,int destFactor) {
		GLES20.glBlendFunc(sourceFactor, destFactor);
	}

	@Override
	public void setStencilFunction(int function, int ref, int mask) {
		GLES20.glStencilFunc(function, ref, mask);
	}

	@Override
	public void setStencilOperation(int fail, int zFail, int zPass) {
		GLES20.glStencilOp(fail,zFail,zPass);
	}

	@Override
	public void setScissorRectI(int x, int y, int width, int height) {
		GLES20.glScissor(x, y, width, height);
	}

	@Override
	public void switchZWriting(boolean enabled) {
		GLES20.glDepthMask(enabled);
	}

	@Override
	public void polygonOffset(float factor, float units) {
		GLES20.glPolygonOffset(factor, units);
	}

	@Override
	public void depthRange(float zNear, float zFar) {
		GLES20.glDepthRangef(zNear, zFar);
	}

}
