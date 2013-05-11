package yang.gdx.graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import yang.gdx.fileio.GdxGfxLoader;
import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.programs.GLProgram;
import yang.graphics.textures.TextureRenderTarget;
import yang.graphics.textures.TextureSettings;
import yang.graphics.translator.GraphicsTranslator;
import yang.graphics.translator.Texture;
import yang.math.TransformationMatrix;
import yang.model.enums.ByteFormat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;


public class GdxGraphicsTranslator extends GraphicsTranslator {
	
	public GdxGraphicsTranslator() {
		super();
		mGFXLoader = new GdxGfxLoader(this);
	}
	
	public static final void clearError() {
		Gdx.gl20.glGetError();
	}
	
	public static final boolean checkError(String message) {
		int error = Gdx.gl20.glGetError();
		if (error != 0) {
			System.err.println("----!GLES-ERROR!---- " + message + ": CODE " + error + " / 0x" + Integer.toHexString(error));
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void derivedInit() {
		Gdx.gl20.glEnable(GL20.GL_BLEND);
		Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
//		Gdx.gl20.glBlendFunc(GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE);
	}

	public GLProgram createProgram() {
		return new GdxGLProgram();
	}

	@Override
	public void setClearColor(float r, float g, float b,float a) {
		Gdx.gl20.glClearColor(r,g,b,a);
	}
	
	@Override
	public void clear(int mask) {
		Gdx.gl20.glClear(mask);
	}

	@Override
	protected void derivedInitTexture(Texture texture, ByteBuffer buffer, TextureSettings textureSettings) {
		ByteBuffer buf = ByteBuffer.allocateDirect(4);
		buf.order(ByteOrder.nativeOrder());
		Gdx.gl20.glGenTextures(1, buf.asIntBuffer());
		int id = buf.get(0);
		texture.setId(id);
		Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE0);
		Gdx.gl20.glBindTexture(GL20.GL_TEXTURE_2D, id);
		checkError("Bind new texture");
		Gdx.gl20.glPixelStorei(GL20.GL_UNPACK_ALIGNMENT, GL20.GL_TRUE);
		switch(textureSettings.mWrapX) {
		case CLAMP: Gdx.gl20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_S, GL20.GL_CLAMP_TO_EDGE); break;
		case REPEAT: Gdx.gl20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_S, GL20.GL_REPEAT); break;
		case MIRROR: Gdx.gl20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_S, GL20.GL_MIRRORED_REPEAT); break;
		}
		switch(textureSettings.mWrapY) {
		case CLAMP: Gdx.gl20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_T, GL20.GL_CLAMP_TO_EDGE); break;
		case REPEAT: Gdx.gl20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_T, GL20.GL_REPEAT); break;
		case MIRROR: Gdx.gl20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_WRAP_T, GL20.GL_MIRRORED_REPEAT); break;
		}
		switch(textureSettings.mFilter) {
		case NEAREST: 
			Gdx.gl20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_NEAREST);
			Gdx.gl20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_NEAREST);
			break;
		case LINEAR_MIP_LINEAR: 
			Gdx.gl20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_LINEAR);
			Gdx.gl20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_LINEAR_MIPMAP_LINEAR);
			break;
		case NEAREST_MIP_LINEAR: 
			Gdx.gl20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_LINEAR);
			Gdx.gl20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_NEAREST_MIPMAP_LINEAR);
			break;
		default:
			Gdx.gl20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MAG_FILTER, GL20.GL_LINEAR);
			Gdx.gl20.glTexParameteri(GL20.GL_TEXTURE_2D, GL20.GL_TEXTURE_MIN_FILTER, GL20.GL_LINEAR);
			break;
		}
		checkError("TexFilter");
		int format = textureSettings.mChannels==4?GL20.GL_RGBA:GL20.GL_RGB;
		Gdx.gl20.glTexImage2D(GL20.GL_TEXTURE_2D, 0, format, texture.getWidth(), texture.getHeight(), 0, format, GL20.GL_UNSIGNED_BYTE, buffer);
		checkError("TexImage2D");
	}

	@Override
	protected void drawDefaultVertices(int bufferStart, int drawVertexCount,boolean wireFrames,IndexedVertexBuffer vertexBuffer) {
		if(wireFrames)
			Gdx.gl20.glDrawArrays(GL20.GL_LINES, bufferStart, drawVertexCount);
		else{
			vertexBuffer.mIndexBuffer.limit(bufferStart+drawVertexCount);
			Gdx.gl20.glDrawElements(GL20.GL_TRIANGLES, drawVertexCount, GL20.GL_UNSIGNED_SHORT, vertexBuffer.mIndexBuffer);
			vertexBuffer.mIndexBuffer.limit(vertexBuffer.mIndexBuffer.capacity());
		}
	}
	
	@Override
	public void derivedSetAttributeBuffer(int handle, int bufferIndex,IndexedVertexBuffer vertexBuffer) {
		Gdx.gl20.glVertexAttribPointer(handle, vertexBuffer.mFloatBufferElementSizes[bufferIndex], GL20.GL_FLOAT, false, 0, vertexBuffer.getByteBuffer(bufferIndex));
	}
	
	@Override
	public void enableAttributePointer(int handle) {
		Gdx.gl20.glEnableVertexAttribArray(handle);
	}

	@Override
	public void disableAttributePointer(int handle) {
		Gdx.gl20.glDisableVertexAttribArray(handle);
	}

	@Override
	protected void setViewPort(int width, int height) {
		Gdx.gl20.glViewport(0, 0, width, height);
	}
	
	@Override
	public void bindTexture(int texId,int level) {
		if(level==1) {
			Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE1);
		}else{
			Gdx.gl20.glActiveTexture(GL20.GL_TEXTURE0);
		}
		Gdx.gl20.glBindTexture(GL20.GL_TEXTURE_2D, texId);
	}
	
	@Override
	public void setCullMode(boolean drawClockwise) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteTextures(int[] ids) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void derivedSetScreenRenderTarget() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected TextureRenderTarget derivedCreateRenderTarget(Texture texture) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void derivedSetTextureRenderTarget(TextureRenderTarget renderTarget) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDepthFunction(boolean less) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void generateMipMap() {
		Gdx.gl20.glGenerateMipmap(GL20.GL_TEXTURE_2D);
	}

	@Override
	public void readPixels(int x, int y, int width, int height, int channels,ByteFormat byteFormat,ByteBuffer pixels) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean checkErrorInst(String message,boolean pre) {
		return checkError(message);
	}

	@Override
	public void enable(int glConstant) {
		Gdx.gl20.glEnable(glConstant);
	}
	
	@Override
	public void disable(int glConstant) {
		Gdx.gl20.glDisable(glConstant);
	}

	@Override
	public void setBlendFunction(int sourceFactor, int destFactor) {
		Gdx.gl20.glBlendFunc(sourceFactor, destFactor);
	}

	@Override
	public void setStencilFunction(int function, int ref, int mask) {
		Gdx.gl20.glStencilFunc(function, ref, mask);
	}

	@Override
	public void setStencilOperation(int fail, int zFail, int zPass) {
		Gdx.gl20.glStencilOp(fail,zFail,zPass);
	}

	@Override
	public void setScissorRectI(int x, int y, int width, int height) {
		Gdx.gl20.glScissor(x, y, width, height);
	}
}
