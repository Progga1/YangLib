package yang.pc.gles;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.JFrame;

import yang.events.EventQueueHolder;
import yang.graphics.translator.GLHolder;
import yang.pc.PCEventHandler;

import com.jogamp.opengl.util.Animator;

public class PCGLPanel implements GLEventListener,GLHolder {

	protected GL2ES2 mGles2;
	protected int mPanelId;
	public PCGL2ES2Graphics mGraphics;
	protected Component mComponent = null;
	protected Cursor mBlankCursor = null;
	protected Animator mGLAnimator;
	public JFrame mFrame;

	public PCGLPanel(PCGL2ES2Graphics graphics,GLCapabilities glCapabilities, boolean useGLPanel,int panelIndex) {
		mGraphics = graphics;
		mPanelId = panelIndex;

		if(!useGLPanel) {
			mComponent = new GLCanvas(glCapabilities);
			((GLCanvas)mComponent).addGLEventListener(this);
		}else{
			mComponent = new GLJPanel(glCapabilities);
			((GLJPanel)mComponent).addGLEventListener(this);
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

			mGraphics.postInitMain(glAutoDrawable);
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

	public void setCursorVisible(boolean enabled) {
		if(mBlankCursor==null)
			mBlankCursor = mComponent.getToolkit().createCustomCursor(new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), new Point(0, 0),"null");
		if(enabled)
			mComponent.setCursor(Cursor.getDefaultCursor());
		else
			mComponent.setCursor(mBlankCursor);
	}

	public PCEventHandler setMouseEventListener(EventQueueHolder eventListener) {
		final PCEventHandler eventHandler = new PCEventHandler(eventListener);
		mComponent.addMouseListener(eventHandler);
		mComponent.addMouseMotionListener(eventHandler);
		mComponent.addMouseWheelListener(eventHandler);
		return eventHandler;
	}

	@Override
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

	@Override
	public void setProperties(String title, boolean undecorated,boolean alwaysOnTop) {
		mFrame.setTitle(title);
		mFrame.setUndecorated(undecorated);
		mFrame.setAlwaysOnTop(alwaysOnTop);
	}

	@Override
	public void setFullscreen(int screenId) {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		if(screenId>=gs.length || screenId<0)
			screenId = 0;
		//gs[screenId].setFullScreenWindow(mFrame);
		GraphicsDevice gd = gs[screenId];
		GraphicsConfiguration conf = gd.getDefaultConfiguration();
		Rectangle bounds = conf.getBounds();
		mComponent.setPreferredSize(new Dimension(bounds.width,bounds.height));
		mFrame.pack();
		mFrame.setLocation(bounds.x, bounds.y);
		mFrame.setVisible(true);
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		mComponent.setPreferredSize(new Dimension(width,height));
		mFrame.setLocation(new Point(x,y));
		mFrame.pack();
		mFrame.setVisible(true);
	}

	@Override
	public void setVisible(boolean visible) {
		mFrame.setVisible(visible);
	}

	@Override
	public void setFramed() {
		mFrame = new JFrame();
		mFrame.add(mComponent);
	}

}
