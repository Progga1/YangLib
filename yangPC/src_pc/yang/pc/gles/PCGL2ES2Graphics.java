package yang.pc.gles;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.awt.GLJPanel;

import yang.events.EventQueueHolder;
import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.programs.GLProgram;
import yang.graphics.textures.TextureProperties;
import yang.graphics.textures.TextureRenderTarget;
import yang.graphics.translator.Texture;
import yang.model.enums.ByteFormat;
import yang.pc.PCEventHandler;
import yang.pc.PCGraphics;
import yang.pc.fileio.PCGFXLoader;
import yang.surface.YangSurface;

import com.jogamp.opengl.util.Animator;

public class PCGL2ES2Graphics extends PCGraphics implements GLEventListener {

	private GL2ES2 gles2;
	private YangSurface mSurface;
	private Component mPanel;
	private Animator mGLAnimator;
	public static boolean DEFAULT_USE_GLPANEL = false;
	public static boolean USE_DISPLAY = false;
	public boolean mStereo = false;

	private Cursor mBlankCursor = null;

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
		return checkError(gles2,message,pre);
	}

	public PCGL2ES2Graphics(int resolutionX,int resolutionY,boolean useGLPanel) {
		super();
		mDriverKey = "PC_GLES20";
		mGFXLoader = new PCGFXLoader(this);

		final GLProfile glProfile = GLProfile.getDefault();

		final GLCapabilities glCapabilities = new GLCapabilities(glProfile);
		glCapabilities.setDoubleBuffered(true);
		glCapabilities.setHardwareAccelerated(true);
		glCapabilities.setOnscreen(true);

		if(!USE_DISPLAY) {
			if(!useGLPanel) {
				mPanel = new GLCanvas(glCapabilities);
				((GLCanvas)mPanel).addGLEventListener(this);
			}else{
				mPanel = new GLJPanel(glCapabilities);
				((GLJPanel)mPanel).addGLEventListener(this);
			}
	//		GLCanvas canvas = new GLCanvas(caps);
	//	    canvas.addGLEventListener(this);
	//
	//		mPanel.add(canvas, BorderLayout.CENTER);
	//
	//	    Animator anim = new Animator(canvas);
	//	    anim.start();

			mPanel.setPreferredSize(new Dimension(resolutionX,resolutionY));
		}else{

		}

	}

	public PCGL2ES2Graphics(int resolutionX,int resolutionY) {
		this(resolutionX,resolutionY,DEFAULT_USE_GLPANEL);
	}

	@Override
	public void setSystemCursorEnabled(boolean enabled) {
		if(mBlankCursor==null)
			mBlankCursor = mPanel.getToolkit().createCustomCursor(new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0),"null");
		if(enabled)
			mPanel.setCursor(Cursor.getDefaultCursor());
		else
			mPanel.setCursor(mBlankCursor);

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

	@Override
	public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
		mSurface.onSurfaceChanged(width, height);
	}

	@Override
	public void init(GLAutoDrawable glAutoDrawable) {
		while(mSurface==null)
			try {
				Thread.sleep(50);
			} catch (final InterruptedException e) {
				break;
			}
		gles2 = glAutoDrawable.getGL().getGL2();
		gles2.glEnable(GL2.GL_TEXTURE_2D);
		mSurface.onSurfaceCreated(true);
	}

	public void setSurface(YangSurface surface) {
		mSurface = surface;
		mSurface.mPlatformKey = "PC";
	}

	@Override
	public void dispose(GLAutoDrawable glAutoDrawable) {
		gles2 = glAutoDrawable.getGL().getGL2();

	}

	@Override
	public void display(GLAutoDrawable glAutoDrawable) {
		gles2 = glAutoDrawable.getGL().getGL2();
		mSurface.drawFrame();
	}

	@Override
	public void genTextures(int[] target,int count) {
		assert checkErrorInst("PRE create texture");
		gles2.glGenTextures(count, target, 0);
		assert checkErrorInst("Generate texture");
	}

	@Override
	public void setTextureParameter(int pName,int param) {
		gles2.glTexParameteri(GL2ES2.GL_TEXTURE_2D, pName, param);
	}

	@Override
	public void derivedSetTextureData(int texId,int width,int height, ByteBuffer data,TextureProperties properties) {
		assert preCheck("Init texture");
		gles2.glActiveTexture(GL2ES2.GL_TEXTURE0);
		gles2.glBindTexture(GL2ES2.GL_TEXTURE_2D, texId);
		gles2.glPixelStorei(GL2ES2.GL_UNPACK_ALIGNMENT, GL2ES2.GL_TRUE);
		assert checkErrorInst("Bind new texture");

		int channels = properties.mChannels;
		final int format = channelsToConst(channels);
		gles2.glTexImage2D(GL2ES2.GL_TEXTURE_2D, 0, format, width, height, 0, format, GL2ES2.GL_UNSIGNED_BYTE, data);
		assert checkErrorInst("Pass texture data with "+channels+" channels");
	}

	@Override
	public void setTextureRectData(int texId,int level,int offsetX,int offsetY,int width,int height,int channels, ByteBuffer data) {
		gles2.glActiveTexture(GL2ES2.GL_TEXTURE0);
		gles2.glBindTexture(GL2ES2.GL_TEXTURE_2D, texId);
		gles2.glTexSubImage2D(GL2ES2.GL_TEXTURE_2D, level, offsetX,offsetY,width,height, channelsToConst(channels), GL2ES2.GL_UNSIGNED_BYTE, data);
	}

	//TODO proper modes
	@Override
	public void drawDefaultVertices(int bufferStart, int drawVertexCount, int mode, ShortBuffer indexBuffer) {
//		if(mode==T_TRIANGLES)
//			gles2.glDrawElements(GL2ES2.GL_TRIANGLES, drawVertexCount, GL2ES2.GL_UNSIGNED_SHORT, indexBuffer);
//		else if(mode==T_LINELIST)
//			gles2.glDrawElements(GL2ES2.GL_LINES, drawVertexCount, GL2ES2.GL_UNSIGNED_SHORT, indexBuffer);
//		else{
//			gles2.glDrawElements(GL2ES2.GL_POINTS, drawVertexCount, GL2ES2.GL_UNSIGNED_SHORT, indexBuffer);
//		}
		gles2.glDrawElements(mode, drawVertexCount, GL2ES2.GL_UNSIGNED_SHORT, indexBuffer);
	}

	@Override
	public void enableAttributePointer(int handle) {
		gles2.glEnableVertexAttribArray(handle);
	}

	@Override
	public void disableAttributePointer(int handle) {
		gles2.glDisableVertexAttribArray(handle);
	}

	@Override
	public void derivedSetAttributeBuffer(int handle,int bufferIndex,IndexedVertexBuffer vertexBuffer) {
		gles2.glVertexAttribPointer(handle, vertexBuffer.mFloatBufferElementSizes[bufferIndex], GL2ES2.GL_FLOAT, false, 0, vertexBuffer.getByteBuffer(bufferIndex));
	}

	@Override
	public GLProgram createProgram() {
		return new PCGLProgram(gles2);
	}

	@Override
	public void enable(int glConstant) {
		gles2.glEnable(glConstant);
	}

	@Override
	public void disable(int glConstant) {
		gles2.glDisable(glConstant);
	}

	@Override
	public void setClearColor(float r,float g,float b,float a) {
		gles2.glClearColor(r,g,b,a);
	}

	@Override
	public void clear(int mask) {
		gles2.glClear(mask);
	}

	@Override
	protected void setViewPort(int width, int height) {
		gles2.glViewport(0, 0, width, height);
	}

	public void run() {
//		if(mUseGLPanel) {
			mGLAnimator = new Animator((GLAutoDrawable)mPanel);
		    mGLAnimator.start();
//		}else{
//			new Thread() {
//				@Override
//				public void run() {
//					while (true) {
//						mPanel.repaint();
//					}
//				}
//			}.start();
//		}
	}

	public void stop() {
		mGLAnimator.stop();
	}

	@Override
	public Component getPanel() {
		return mPanel;
	}

	public PCEventHandler setMouseEventListener(EventQueueHolder eventListener) {
		final PCEventHandler eventHandler = new PCEventHandler(eventListener);
		mPanel.addMouseListener(eventHandler);
		mPanel.addMouseMotionListener(eventHandler);
		mPanel.addMouseWheelListener(eventHandler);
		return eventHandler;
	}

	@Override
	public void setCullMode(boolean drawClockwise) {
		if(drawClockwise)
			gles2.glCullFace(GL2ES2.GL_FRONT);
		else
			gles2.glCullFace(GL2ES2.GL_BACK);
	}

	@Override
	protected void updateTexture(Texture texture, ByteBuffer source, int left,int top, int width,int height) {
		bindTexture(texture);

		//gles2.glTexSubImage2D(GL2ES2.GL_TEXTURE_2D, 0, 0, 0, left, top, width, height, GL2ES2.GL_RGBA, GL2ES2.GL_UNSIGNED_BYTE,source);
	}

	@Override
	public void deleteTextures(int[] ids) {
		gles2.glDeleteTextures(ids.length, ids, 0);
	}

	@Override
	protected void deleteBuffers(int[] bufIds) {
		gles2.glDeleteBuffers(bufIds.length,bufIds,0);
	}

	@Override
	protected void deleteFrameBuffers(int[] bufIds) {
		gles2.glDeleteFramebuffers(bufIds.length,bufIds,0);
	}

	@Override
	public void derivedSetScreenRenderTarget() {
		gles2.glBindFramebuffer(GL2ES2.GL_FRAMEBUFFER, 0);
	}

	@Override
	public void initRenderTarget(TextureRenderTarget target) {
		assert preCheck("Create render target");
		gles2.glGenFramebuffers(1, mTempInt, 0);
		gles2.glGenRenderbuffers(1, mTempInt2, 0);
		final int frameId = mTempInt[0];
		final int depthId = mTempInt2[0];
		gles2.glBindRenderbuffer(GL2ES2.GL_RENDERBUFFER, depthId);
		gles2.glRenderbufferStorage(GL2ES2.GL_RENDERBUFFER, GL2ES2.GL_DEPTH_COMPONENT32, target.mTargetTexture.getWidth(), target.mTargetTexture.getHeight());
		assert checkErrorInst("Create render target");
		target.set(frameId,depthId);
	}

	@Override
	public void derivedSetTextureRenderTarget(TextureRenderTarget renderTarget) {
		assert preCheck("Set texture render target");
		gles2.glBindFramebuffer(GL2ES2.GL_FRAMEBUFFER, renderTarget.mFrameBufferId);
		gles2.glFramebufferTexture2D(GL2ES2.GL_FRAMEBUFFER, GL2ES2.GL_COLOR_ATTACHMENT0, GL2ES2.GL_TEXTURE_2D, renderTarget.mTargetTexture.mId, 0);
		gles2.glFramebufferRenderbuffer(GL2ES2.GL_FRAMEBUFFER, GL2ES2.GL_DEPTH_ATTACHMENT, GL2ES2.GL_RENDERBUFFER, renderTarget.mDepthBufferId);
		assert checkErrorInst("Set texture render target");
	}

	@Override
	public void setDepthFunction(boolean less,boolean equal) {
		if(less)
			gles2.glDepthFunc(equal?GL2ES2.GL_LEQUAL:GL2ES2.GL_LESS);
		else
			gles2.glDepthFunc(equal?GL2ES2.GL_GEQUAL:GL2ES2.GL_GREATER);
	}

	@Override
	public void generateMipMap() {
		gles2.glGenerateMipmap(GL2ES2.GL_TEXTURE_2D);
		assert checkErrorInst("Generate mipmap");
	}

	@Override
	public void bindTexture(int texId, int level) {
		gles2.glActiveTexture(GL2ES2.GL_TEXTURE0 + level);
		gles2.glBindTexture(GL2ES2.GL_TEXTURE_2D, texId);
	}

	@Override
	public void readPixels(int x, int y, int width, int height, int channels, ByteFormat byteFormat, ByteBuffer target) {
		gles2.glReadPixels(x, y, width, height, channelsToConst(channels), byteFormatToConst(byteFormat), target);
		assert checkErrorInst("Read pixels");
	}

	@Override
	public void setBlendFunction(int sourceFactor,int destFactor) {
		gles2.glBlendFunc(sourceFactor, destFactor);
	}

	@Override
	public void setStencilFunction(int function, int ref, int mask) {
		gles2.glStencilFunc(function, ref, mask);
	}

	@Override
	public void setStencilOperation(int fail, int zFail, int zPass) {
		gles2.glStencilOp(fail,zFail,zPass);
	}

	@Override
	public void setScissorRectI(int x, int y, int width, int height) {
		gles2.glScissor(x, y, width, height);
	}

	@Override
	public void switchZWriting(boolean enabled) {
		gles2.glDepthMask(enabled);
	}

	@Override
	public void polygonOffset(float factor,float units) {
		gles2.glPolygonOffset(factor, units);
	}

	@Override
	public void depthRange(float zNear,float zFar) {
		gles2.glDepthRange(zNear,zFar);
	}

}
