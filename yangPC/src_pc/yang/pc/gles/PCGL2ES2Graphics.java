package yang.pc.gles;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL2ES2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;

import yang.events.EventQueueHolder;
import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.programs.GLProgram;
import yang.graphics.textures.TextureProperties;
import yang.graphics.textures.TextureRenderTarget;
import yang.graphics.translator.GraphicsTranslator;
import yang.graphics.translator.Texture;
import yang.graphics.translator.TextureDisplay;
import yang.model.enums.ByteFormat;
import yang.pc.PCEventHandler;
import yang.pc.fileio.PCGFXLoader;
import yang.surface.YangSurface;


public class PCGL2ES2Graphics extends GraphicsTranslator {

	protected GL2ES2 mGles2;
	YangSurface mSurface;
	private PCGLPanel mPanel;
	public static boolean DEFAULT_USE_GLPANEL = false;
	public static boolean USE_DISPLAY = false;
	public boolean mStereo = false;
	private GLCapabilities mGlCapabilities;
	private boolean mClosed = false;

	public ArrayList<PCGLTextureDisplay> mTextureDisplays = new ArrayList<PCGLTextureDisplay>(128);

	public static final boolean clearError(GL2ES2 gl2) {
		gl2.glGetError();
		return true;
	}

	public static final boolean checkError(GL2ES2 gl2,String message,boolean pre) {
		final int error = gl2.glGetError();
		if (error != 0) {
			if(pre)
				System.err.println("----!GLES-ERROR!---- [PRE] " + message);
			else
				System.err.println("----!GLES-ERROR!---- " + message + ": " + errorCodeToString(error));
			return false;
		} else {
			return true;
		}
	}

	public static final boolean checkError(GL2ES2 gl2,String message) {
		return checkError(gl2,message,false);
	}

	@Override
	public boolean checkErrorInst(String message,boolean pre) {
		return checkError(mGles2,message,pre);
	}

	public PCGL2ES2Graphics(int resolutionX,int resolutionY,boolean useGLPanel) {
		super();
		mDriverKey = "PC_GLES20";
		mGFXLoader = new PCGFXLoader(this);

		final GLProfile glProfile = GLProfile.getDefault();

		mGlCapabilities = new GLCapabilities(glProfile);
//		glCapabilities.setDoubleBuffered(true);
		mGlCapabilities.setHardwareAccelerated(true);
		mGlCapabilities.setOnscreen(true);
		mGlCapabilities.setDepthBits(24);

		if(!USE_DISPLAY) {
			mPanel = new PCGLPanel(this,mGlCapabilities,useGLPanel,0);
			mPanel.mComponent.setPreferredSize(new Dimension(resolutionX,resolutionY));
		}else{

		}
	}

	protected void postInitMain(GLAutoDrawable drawable) {

	}

	public PCGL2ES2Graphics(int resolutionX,int resolutionY) {
		this(resolutionX,resolutionY,DEFAULT_USE_GLPANEL);
	}

	@Override
	public void setSystemCursorEnabled(boolean enabled) {
		mPanel.setCursorVisible(enabled);
	}

	public static int channelsToConst(int channels) {
		//int c = GL2ES2.GL_SCISSOR_TEST;
		switch(channels) {
		case 1:
			return GL2ES2.GL_RED;
		case 3:
			return GL2ES2.GL_RGB;
		case 4:
			return GL2ES2.GL_RGBA;
		default: throw new RuntimeException(channels + " channels not supported.");
		}
	}

	public static int byteFormatToConst(ByteFormat byteFormat) {
		switch(byteFormat) {
		case BYTE: return GL2ES2.GL_BYTE;
		case UNSIGNED_BYTE: return GL2ES2.GL_UNSIGNED_BYTE;
		case SHORT: return GL2ES2.GL_SHORT;
		case INT: return GL2ES2.GL_INT;
		case FLOAT: return GL2ES2.GL_FLOAT;
		default: throw new RuntimeException(byteFormat+" not supported");
		}
	}

	public void setSurface(YangSurface surface) {
		mSurface = surface;
		mSurface.mPlatformKey = "PC";
	}

	@Override
	public void genTextures(int[] target,int count) {
		assert checkErrorInst("PRE create texture");
		mGles2.glGenTextures(count, target, 0);
		assert checkErrorInst("Generate texture");
	}

	@Override
	public void setTextureParameter(int pName,int param) {
		mGles2.glTexParameteri(GL2ES2.GL_TEXTURE_2D, pName, param);
	}

	@Override
	public void derivedSetTextureData(int texId,int width,int height, ByteBuffer data,TextureProperties properties) {
		assert preCheck("Init texture");
		mGles2.glActiveTexture(GL2ES2.GL_TEXTURE0);
		mGles2.glBindTexture(GL2ES2.GL_TEXTURE_2D, texId);
		mGles2.glPixelStorei(GL2ES2.GL_UNPACK_ALIGNMENT, GL2ES2.GL_TRUE);
		assert checkErrorInst("Bind new texture");

		int channels = properties.mChannels;
		final int format = channelsToConst(channels);
		mGles2.glTexImage2D(GL2ES2.GL_TEXTURE_2D, 0, format, width, height, 0, format, properties.mSigned?GL2ES2.GL_BYTE:GL2ES2.GL_UNSIGNED_BYTE, data);
		assert checkErrorInst("Pass texture data with "+channels+" channels");
	}

	@Override
	public void setTextureRectData(int texId,int level,int offsetX,int offsetY,int width,int height,int channels, ByteBuffer data) {
		mGles2.glActiveTexture(GL2ES2.GL_TEXTURE0);
		mGles2.glBindTexture(GL2ES2.GL_TEXTURE_2D, texId);
		mGles2.glTexSubImage2D(GL2ES2.GL_TEXTURE_2D, level, offsetX,offsetY,width,height, channelsToConst(channels), GL2ES2.GL_UNSIGNED_BYTE, data);
	}

	//TODO proper modes
	@Override
	public void drawDefaultVertices(int bufferStart, int drawVertexCount, int mode, Buffer indexBuffer, boolean intIndices) {
//		if(mode==T_TRIANGLES)
//			gles2.glDrawElements(GL2ES2.GL_TRIANGLES, drawVertexCount, GL2ES2.GL_UNSIGNED_SHORT, indexBuffer);
//		else if(mode==T_LINELIST)
//			gles2.glDrawElements(GL2ES2.GL_LINES, drawVertexCount, GL2ES2.GL_UNSIGNED_SHORT, indexBuffer);
//		else{
//			gles2.glDrawElements(GL2ES2.GL_POINTS, drawVertexCount, GL2ES2.GL_UNSIGNED_SHORT, indexBuffer);
//		}
		mGles2.glDrawElements(mode, drawVertexCount, intIndices?GL2ES2.GL_UNSIGNED_INT:GL2ES2.GL_UNSIGNED_SHORT, indexBuffer);
	}

	@Override
	public void enableAttributePointer(int handle) {
		mGles2.glEnableVertexAttribArray(handle);
	}

	@Override
	public void disableAttributePointer(int handle) {
		mGles2.glDisableVertexAttribArray(handle);
	}

	@Override
	public void derivedSetAttributeBuffer(int handle,int bufferIndex,IndexedVertexBuffer vertexBuffer) {
		mGles2.glVertexAttribPointer(handle, vertexBuffer.mFloatBufferElementSizes[bufferIndex], GL2ES2.GL_FLOAT, false, 0, vertexBuffer.getByteBuffer(bufferIndex));
	}

	@Override
	public GLProgram createProgram() {
		return new PCGLProgram(mGles2);
	}

	@Override
	public void enable(int glConstant) {
		mGles2.glEnable(glConstant);
	}

	@Override
	public void disable(int glConstant) {
		mGles2.glDisable(glConstant);
	}

	@Override
	public void setClearColor(float r,float g,float b,float a) {
		mGles2.glClearColor(r,g,b,a);
	}

	@Override
	public void clear(int mask) {
		mGles2.glClear(mask);
	}

	@Override
	protected void setViewPort(int width, int height) {
		mGles2.glViewport(0, 0, width, height);
	}

	public void run() {
		mPanel.run();
	}

	public void stop() {
		mClosed = true;
		mPanel.stop();
		for(PCGLTextureDisplay texDisplay:mTextureDisplays) {
			texDisplay.stop();
		}
	}

	@Override
	public PCGLPanel getMainDisplay() {
		return mPanel;
	}

	public PCEventHandler setMouseEventListener(EventQueueHolder eventListener) {
		return mPanel.setMouseEventListener(eventListener);
	}

	@Override
	public void setCullMode(boolean drawClockwise) {
		if(drawClockwise)
			mGles2.glCullFace(GL2ES2.GL_FRONT);
		else
			mGles2.glCullFace(GL2ES2.GL_BACK);
	}

	@Override
	protected void updateTexture(Texture texture, ByteBuffer source, int left,int top, int width,int height) {
		bindTexture(texture);

//		mGles2.glTexSubImage2D(GL2ES2.GL_TEXTURE_2D, 0, 0, 0, left, top, width, height, GL2ES2.GL_RGBA, GL2ES2.GL_UNSIGNED_BYTE,source);
	}

	@Override
	public void deleteTextures(int[] ids) {
		mGles2.glDeleteTextures(ids.length, ids, 0);
	}

	@Override
	protected void deleteBuffers(int[] bufIds) {
		mGles2.glDeleteBuffers(bufIds.length,bufIds,0);
	}

	@Override
	protected void deleteFrameBuffers(int[] bufIds) {
		mGles2.glDeleteFramebuffers(bufIds.length,bufIds,0);
	}

	@Override
	public void derivedSetScreenRenderTarget() {
		mGles2.glBindFramebuffer(GL2ES2.GL_FRAMEBUFFER, 0);
	}

	@Override
	public void initRenderTarget(TextureRenderTarget target) {
		assert preCheck("Create render target");
		mGles2.glGenFramebuffers(1, mTempInt, 0);
		mGles2.glGenRenderbuffers(1, mTempInt2, 0);
		final int frameId = mTempInt[0];
		final int depthId = mTempInt2[0];
		mGles2.glBindRenderbuffer(GL2ES2.GL_RENDERBUFFER, depthId);
		mGles2.glRenderbufferStorage(GL2ES2.GL_RENDERBUFFER, GL2ES2.GL_DEPTH_COMPONENT32, target.mTargetTexture.getWidth(), target.mTargetTexture.getHeight());
		assert checkErrorInst("Create render target");
		target.set(frameId,depthId);
	}

	@Override
	public void derivedSetTextureRenderTarget(TextureRenderTarget renderTarget) {
		assert preCheck("Set texture render target");
		mGles2.glBindFramebuffer(GL2ES2.GL_FRAMEBUFFER, renderTarget.mFrameBufferId);
		mGles2.glFramebufferTexture2D(GL2ES2.GL_FRAMEBUFFER, GL2ES2.GL_COLOR_ATTACHMENT0, GL2ES2.GL_TEXTURE_2D, renderTarget.mTargetTexture.mId, 0);
		mGles2.glFramebufferRenderbuffer(GL2ES2.GL_FRAMEBUFFER, GL2ES2.GL_DEPTH_ATTACHMENT, GL2ES2.GL_RENDERBUFFER, renderTarget.mDepthBufferId);
		assert checkErrorInst("Set texture render target");
	}

	@Override
	public void setDepthFunction(boolean less,boolean equal) {
		if(less)
			mGles2.glDepthFunc(equal?GL2ES2.GL_LEQUAL:GL2ES2.GL_LESS);
		else
			mGles2.glDepthFunc(equal?GL2ES2.GL_GEQUAL:GL2ES2.GL_GREATER);
	}

	@Override
	public void generateMipMap() {
		mGles2.glGenerateMipmap(GL2ES2.GL_TEXTURE_2D);
		assert checkErrorInst("Generate mipmap");
	}

	@Override
	public void bindTexture(int texId, int level) {
		mGles2.glActiveTexture(GL2ES2.GL_TEXTURE0 + level);
		mGles2.glBindTexture(GL2ES2.GL_TEXTURE_2D, texId);
	}

	@Override
	public void readPixels(int x, int y, int width, int height, int channels, ByteFormat byteFormat, ByteBuffer target) {
		mGles2.glReadPixels(x, y, width, height, channelsToConst(channels), byteFormatToConst(byteFormat), target);
		assert checkErrorInst("Read pixels");
	}

	@Override
	public void setBlendFunction(int sourceFactor,int destFactor) {
		mGles2.glBlendFunc(sourceFactor, destFactor);
	}

	@Override
	public void setStencilFunction(int function, int ref, int mask) {
		mGles2.glStencilFunc(function, ref, mask);
	}

	@Override
	public void setStencilOperation(int fail, int zFail, int zPass) {
		mGles2.glStencilOp(fail,zFail,zPass);
	}

	@Override
	public void setScissorRectI(int x, int y, int width, int height) {
		mGles2.glScissor(x, y, width, height);
	}

	@Override
	public void switchZWriting(boolean enabled) {
		mGles2.glDepthMask(enabled);
	}

	@Override
	public void polygonOffset(float factor,float units) {
		mGles2.glPolygonOffset(factor, units);
	}

	@Override
	public void depthRange(float zNear,float zFar) {
		mGles2.glDepthRange(zNear,zFar);
	}

	public boolean isClosed() {
		return mClosed;
	}

	@Override
	public TextureDisplay createTextureDisplay(boolean undecorated) {
		return new PCGLTextureDisplay(this,mGles2.getContext(),mGlCapabilities,1,undecorated);
	}

	@Override
	public int[] getScreenBounds(int screenId) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		if(screenId>=gs.length || screenId<0)
			screenId = 0;
		GraphicsDevice gd = gs[screenId];
		GraphicsConfiguration conf = gd.getDefaultConfiguration();
		Rectangle bounds = conf.getBounds();
		mScreenBoundsInt[0] = bounds.x;
		mScreenBoundsInt[1] = bounds.y;
		mScreenBoundsInt[2] = bounds.width;
		mScreenBoundsInt[3] = bounds.height;
		return mScreenBoundsInt;
	}

	@Override
	public int getMainScreenId() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		for(int i=0;i<gs.length;i++) {
			GraphicsDevice gd = gs[i];
			Rectangle bounds = gd.getDefaultConfiguration().getBounds();
			if(bounds.x==0 && bounds.y==0)
				return i;
		}
		return 0;
	}

	@Override
	public int getNumberOfScreens() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length;
	}


}
