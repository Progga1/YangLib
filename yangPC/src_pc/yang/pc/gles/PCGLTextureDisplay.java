package yang.pc.gles;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.media.opengl.GL2ES2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLES2;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.awt.GLJPanel;

import model.globalassets.VeltTextures;

import yang.graphics.buffers.UniversalVertexBuffer;
import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.programs.BasicProgram;
import yang.graphics.translator.Texture;
import yang.math.objects.YangMatrix;

import com.jogamp.opengl.util.Animator;

public class PCGLTextureDisplay implements GLEventListener {

	private GL2ES2 mGles2;
	private int mPanelId;
	public PCGL2ES2Graphics mGraphics;
	public static DefaultGraphics<?> mDefGraphics;
	protected Component mComponent = null;
	private Cursor mBlankCursor = null;
	private Animator mGLAnimator;
	public static Texture TEXTURE_TEST = null;

	public BasicProgram mBProg;

	public UniversalVertexBuffer buf;

	public PCGLTextureDisplay(PCGL2ES2Graphics graphics, GLContext context,GLCapabilities glCapabilities,int panelIndex) {
		mGraphics = graphics;
		mPanelId = panelIndex;

		mComponent = new GLCanvas(glCapabilities,context);
		((GLCanvas)mComponent).addGLEventListener(this);
	}

	public boolean isMainPanel() {
		return mPanelId==0;
	}

	@Override
	public void init(GLAutoDrawable glAutoDrawable) {
		mGles2 = glAutoDrawable.getGL().getGL2();

		buf = new UniversalVertexBuffer(true,true, 6,4);
		buf.init(new int[]{3,2,4},new float[][]{{0,0,0},{0,0},{1,1,1,1}});

		float s = 0.5f;
		buf.putVec12(0, -s,-s,0, s,-s,0, -s,s,0, s,s,0);
		buf.putRect2D(1, 0,1,1,0);
		buf.putArrayMultiple(2, new float[]{1,1,0.5f,1}, 4);

		buf.putRectIndices(0,1,2,3);
		buf.reset();

		mBProg = new BasicProgram();
		mBProg.init(mGraphics);
//		mProgram = new PCGLProgram(mGles2);
	}

	@Override
	public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
		mGles2.glViewport(0, 0, width, height);
	}

	@Override
	public void dispose(GLAutoDrawable glAutoDrawable) {

	}

	public void bindBuf(int handle, int bufferIndex) {
		mGles2.glEnableVertexAttribArray(handle);
		mGles2.glVertexAttribPointer(handle, buf.mFloatBufferElementSizes[bufferIndex], GL2ES2.GL_FLOAT, false, 0, buf.getByteBuffer(bufferIndex));
	}

	public void unbindBuf(int handle) {
		mGles2.glDisableVertexAttribArray(handle);
	}

	private void out(String msg) {
		int e = mGles2.glGetError();
		if(e!=0)
			System.out.println(msg+": "+e);
	}

	@Override
	public void display(GLAutoDrawable glAutoDrawable) {
		if(mDefGraphics==null)
			return;
		mGles2.glClearColor(0f, .33f, 0.66f, 1f);
		mGles2.glClearDepthf(1f);
		mGles2.glClear(GLES2.GL_COLOR_BUFFER_BIT | GLES2.GL_DEPTH_BUFFER_BIT);
		out("pre");
		if(TEXTURE_TEST!=null) {
			mGles2.glActiveTexture(GL2ES2.GL_TEXTURE0);
			mGles2.glBindTexture(GLES2.GL_TEXTURE_2D,VeltTextures.CUBE.mId);
			out("bind");
		}

		PCGLProgram prog = (PCGLProgram)mBProg.mProgram;
		mGles2.glUseProgram(prog.mId);
		out("program");

		bindBuf(mBProg.mPositionHandle,0);
		bindBuf(mBProg.mTextureHandle,1);
		bindBuf(mBProg.mColorHandle,2);
		out("bindbuf");
		prog.setUniformMatrix4f(mBProg.mProjHandle, YangMatrix.IDENTITY.mValues);
		prog.setUniformInt(mBProg.mTexSamplerHandle, 0);
		prog.setUniform4f(mBProg.mColorFactorHandle, 1,1,1,1);
		out("setUniform");

		buf.reset();
		mGles2.glDisable(GLES2.GL_CULL_FACE);
		mGles2.glDrawElements(GLES2.GL_TRIANGLE_STRIP, 4, GL2ES2.GL_UNSIGNED_SHORT, buf.mIndexBuffer);

		out("draw");

		mGles2.glFlush();

		out("flush");

		unbindBuf(mBProg.mPositionHandle);
		unbindBuf(mBProg.mColorHandle);
		unbindBuf(mBProg.mTextureHandle);

		out("unbind");
	}

	public void setSystemCursorEnabled(boolean enabled) {
		if(mBlankCursor==null)
			mBlankCursor = mComponent.getToolkit().createCustomCursor(new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0),"null");
		if(enabled)
			mComponent.setCursor(Cursor.getDefaultCursor());
		else
			mComponent.setCursor(mBlankCursor);
	}

	public void run() {
		mGLAnimator = new Animator((GLAutoDrawable)mComponent);
	    mGLAnimator.start();
	}

	public void stop() {
		mGLAnimator.stop();
	}

	public Component getComponent() {
		return mComponent;
	}

}
