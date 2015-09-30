package yang.pc.gles;

import javax.media.opengl.GL2ES2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLES2;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;

import yang.graphics.buffers.UniversalVertexBuffer;
import yang.graphics.model.FloatColor;
import yang.graphics.programs.BasicProgram;
import yang.graphics.translator.Texture;
import yang.graphics.translator.TextureDisplay;
import yang.math.objects.YangMatrix;

public class PCGLTextureDisplay extends PCGLPanel implements GLEventListener,TextureDisplay {

	public Texture mTexture = null;
	public FloatColor mColorFactor = FloatColor.WHITE.clone();

	//Objects
	public PCGL2ES2Graphics mGraphics;
	public BasicProgram mProgram;
	private UniversalVertexBuffer mVertices;

	public PCGLTextureDisplay(PCGL2ES2Graphics graphics, GLContext context,GLCapabilities glCapabilities,int panelIndex) {
		super(graphics,glCapabilities,false,panelIndex);
		mGraphics = graphics;
		mPanelId = panelIndex;

		mComponent = new GLCanvas(glCapabilities,context);
		((GLCanvas)mComponent).addGLEventListener(this);

		setFramed();
	}

	@Override
	public void init(GLAutoDrawable glAutoDrawable) {
		mGles2 = glAutoDrawable.getGL().getGL2();

		mVertices = new UniversalVertexBuffer(true,true, 6,4);
		mVertices.init(new int[]{3,2,4},new float[][]{{0,0,0},{0,0},{1,1,1,1}});

		float s = 1;
		mVertices.putVec12(0, -s,-s,0, s,-s,0, -s,s,0, s,s,0);
		mVertices.putRect2D(1, 0,1,1,0);
		mVertices.putArrayMultiple(2, new float[]{1,1,1,1}, 4);

		mVertices.putRectIndices(0,1,2,3);
		mVertices.reset();

		mProgram = new BasicProgram();
		mProgram.init(mGraphics);
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
		mGles2.glVertexAttribPointer(handle, mVertices.mFloatBufferElementSizes[bufferIndex], GL2ES2.GL_FLOAT, false, 0, mVertices.getByteBuffer(bufferIndex));
	}

	public void unbindBuf(int handle) {
		mGles2.glDisableVertexAttribArray(handle);
	}

	private void checkErr(String msg) {
		int e = mGles2.glGetError();
		if(e!=0)
			System.err.println(msg+": "+e);
	}

	@Override
	public void display(GLAutoDrawable glAutoDrawable) {
		if(mGraphics.isClosed())
			return;

		checkErr("pre");
		if(mTexture!=null) {
			mGles2.glActiveTexture(GL2ES2.GL_TEXTURE0);
			mGles2.glBindTexture(GLES2.GL_TEXTURE_2D,mTexture.mId);
			checkErr("bind");

			PCGLProgram prog = (PCGLProgram)mProgram.mProgram;
			mGles2.glUseProgram(prog.mId);
			checkErr("program");

			bindBuf(mProgram.mPositionHandle,0);
			bindBuf(mProgram.mTextureHandle,1);
			bindBuf(mProgram.mColorHandle,2);
			checkErr("bindbuf");
			prog.setUniformMatrix4f(mProgram.mProjHandle, YangMatrix.IDENTITY.mValues);
			prog.setUniformInt(mProgram.mTexSamplerHandle, 0);
			prog.setUniform4f(mProgram.mColorFactorHandle, mColorFactor.mValues);
			checkErr("setUniform");

			mVertices.reset();
			mGles2.glDisable(GLES2.GL_CULL_FACE);
			mGles2.glDrawElements(GLES2.GL_TRIANGLE_STRIP, 4, GL2ES2.GL_UNSIGNED_SHORT, mVertices.mIndexBuffer);

			checkErr("draw");

			mGles2.glFlush();

			checkErr("flush");

			unbindBuf(mProgram.mPositionHandle);
			unbindBuf(mProgram.mColorHandle);
			unbindBuf(mProgram.mTextureHandle);

			checkErr("unbind");
		}else{
			mGles2.glClearColor(0, 0, 0.1f, 1);
			mGles2.glClearDepthf(1f);
			mGles2.glClear(GLES2.GL_COLOR_BUFFER_BIT | GLES2.GL_DEPTH_BUFFER_BIT);
		}

		checkErr("post");
	}

	@Override
	public Texture getTexture() {
		return mTexture;
	}

	@Override
	public void setTexture(Texture texture) {
		mTexture = texture;
	}

}
