package yang.pc.gles;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.awt.GLJPanel;

import yang.events.EventQueueHolder;
import yang.pc.PCEventHandler;

import com.jogamp.opengl.util.Animator;

public class PCGLEventListener implements GLEventListener {

	private GL2ES2 mGles2;
	private int mPanelId;
	public PCGL2ES2Graphics mGraphics;
	protected Component mPanel = null;
	private Cursor mBlankCursor = null;
	private Animator mGLAnimator;

	public PCGLEventListener(PCGL2ES2Graphics graphics,GLCapabilities glCapabilities, boolean useGLPanel,int panelIndex) {
		mGraphics = graphics;
		mPanelId = panelIndex;

		if(!useGLPanel) {
			mPanel = new GLCanvas(glCapabilities);
			((GLCanvas)mPanel).addGLEventListener(this);
		}else{
			mPanel = new GLJPanel(glCapabilities);
			((GLJPanel)mPanel).addGLEventListener(this);
		}
	}

	public boolean isMainPanel() {
		return mPanelId==0;
	}

	@Override
	public void init(GLAutoDrawable glAutoDrawable) {
		mGles2 = glAutoDrawable.getGL().getGL2();
		if(isMainPanel()) {
			while(mGraphics.mSurface==null)
				try {
					Thread.sleep(50);
				} catch (final InterruptedException e) {
					break;
				}
			mGraphics.mGles2 = mGles2;
			mGles2.glEnable(GL2.GL_TEXTURE_2D);
			mGraphics.mSurface.onSurfaceCreated(true);
		}
	}

	@Override
	public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
		mGraphics.mSurface.onSurfaceChanged(width, height);
	}

	@Override
	public void dispose(GLAutoDrawable glAutoDrawable) {

	}

	@Override
	public void display(GLAutoDrawable glAutoDrawable) {
		mGraphics.mSurface.drawFrame();
	}

	public void setSystemCursorEnabled(boolean enabled) {
		if(mBlankCursor==null)
			mBlankCursor = mPanel.getToolkit().createCustomCursor(new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0),"null");
		if(enabled)
			mPanel.setCursor(Cursor.getDefaultCursor());
		else
			mPanel.setCursor(mBlankCursor);
	}

	public PCEventHandler setMouseEventListener(EventQueueHolder eventListener) {
		final PCEventHandler eventHandler = new PCEventHandler(eventListener);
		mPanel.addMouseListener(eventHandler);
		mPanel.addMouseMotionListener(eventHandler);
		mPanel.addMouseWheelListener(eventHandler);
		return eventHandler;
	}

	public void run() {
		mGLAnimator = new Animator((GLAutoDrawable)mPanel);
	    mGLAnimator.start();
	}

	public void stop() {
		mGLAnimator.stop();
	}

}