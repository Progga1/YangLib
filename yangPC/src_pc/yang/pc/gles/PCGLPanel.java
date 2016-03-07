package yang.pc.gles;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
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
import yang.events.eventtypes.YangPointerEvent;
import yang.graphics.translator.DisplayMouseListener;
import yang.graphics.translator.GLHolder;
import yang.pc.PCEventHandler;

import com.jogamp.opengl.util.Animator;

public class PCGLPanel extends GLHolder implements GLEventListener,MouseMotionListener,MouseListener,MouseWheelListener {

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

		setCursorVisible(false);
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
			mGraphics.mOriginalContext = mGles2;
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
		if(mDisplayListener!=null)
			mDisplayListener.onDisplay(this);
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
	public void setTitle(String title) {
		if(mFrame==null)
			throw new RuntimeException("Not framed");
		mFrame.setTitle(title);
	}

	@Override
	public void setAlwaysOnTop(boolean alwaysOnTop) {
		mFrame.setAlwaysOnTop(alwaysOnTop);
	}

	@Override
	public void setFullscreen(int screenId) {
		if(mFrame==null)
			throw new RuntimeException("Not framed");
		int[] bounds = mGraphics.getScreenBounds(screenId);
		mComponent.setPreferredSize(new Dimension(bounds[2],bounds[3]));
		mFrame.pack();
		mFrame.setLocation(bounds[0], bounds[1]);
//		mFrame.setVisible(true);
	}

	public void setFullscreen() {
		setFullscreen(mGraphics.getMainScreenId());
	}

	@Override
	public void setSize(int width, int height) {
		mComponent.setPreferredSize(new Dimension(width,height));
		if(mFrame!=null) {
			mFrame.pack();
//			mFrame.setVisible(true);
		}
	}

	@Override
	public void setLocation(int x, int y) {
		if(mFrame!=null)
			mFrame.setLocation(new Point(x,y));
	}

	@Override
	public void setCentered() {
		if(mFrame!=null)
			mFrame.setLocationRelativeTo(null);
	}

	@Override
	public void requestFocus() {
		if(mFrame!=null)
			mFrame.requestFocus();
	}

	@Override
	public void setVisible(boolean visible) {
		mFrame.setVisible(visible);
	}

	@Override
	public void setFramed(boolean undecorated) {
		if(mFrame!=null)
			return;
		mFrame = new JFrame();
		mFrame.setUndecorated(undecorated);
		mFrame.add(mComponent);
	}

	public void setFramed(JFrame target) {
		if(mFrame!=null && mFrame!=target)
			throw new RuntimeException("Already framed");
		if(mFrame==null) {
			target.add(mComponent);
			mFrame = target;
		}
	}

	@Override
	public boolean isFramed() {
		return mFrame!=null;
	}

	@Override
	public void setMouseListener(DisplayMouseListener listener) {
		if(mMouseListeners==null) {
			mComponent.addMouseListener(this);
			mComponent.addMouseMotionListener(this);
			mComponent.addMouseWheelListener(this);
		}
		super.setMouseListener(listener);
	}

	protected float projMouseX(int mouseX) {
		return mouseX;
	}

	protected float projMouseY(int mouseY) {
		return mouseY;
	}

	protected int toYangMouseButton(int button) {
		switch(button) {
		case MouseEvent.BUTTON1: return YangPointerEvent.BUTTON_LEFT;
		case MouseEvent.BUTTON2: return YangPointerEvent.BUTTON_MIDDLE;
		case MouseEvent.BUTTON3: return YangPointerEvent.BUTTON_RIGHT;
		default: return YangPointerEvent.BUTTON_NONE;
		}
	}

	@Override
	public void mouseClicked(MouseEvent ev) {

	}

	@Override
	public void mouseEntered(MouseEvent ev) {
		for(DisplayMouseListener listener:mMouseListeners)
			listener.displayMouseEnter(this);
	}

	@Override
	public void mouseExited(MouseEvent ev) {
		for(DisplayMouseListener listener:mMouseListeners)
			listener.displayMouseExit(this);
	}

	@Override
	public void mousePressed(MouseEvent ev) {
		for(DisplayMouseListener listener:mMouseListeners)
			listener.displayMouseDown(this, projMouseX(ev.getX()), projMouseY(ev.getY()), toYangMouseButton(ev.getButton()));
	}

	@Override
	public void mouseReleased(MouseEvent ev) {
		for(DisplayMouseListener listener:mMouseListeners)
			listener.displayMouseUp(this, projMouseX(ev.getX()), projMouseY(ev.getY()), toYangMouseButton(ev.getButton()));
	}

	@Override
	public void mouseDragged(MouseEvent ev) {
		for(DisplayMouseListener listener:mMouseListeners)
			listener.displayMouseDrag(this, projMouseX(ev.getX()), projMouseY(ev.getY()), toYangMouseButton(ev.getButton()));
	}

	@Override
	public void mouseMoved(MouseEvent ev) {
		for(DisplayMouseListener listener:mMouseListeners)
			listener.displayMouseMove(this, projMouseX(ev.getX()), projMouseY(ev.getY()));
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent ev) {
		for(DisplayMouseListener listener:mMouseListeners)
			listener.displayMouseWheel(this, ev.getScrollAmount());
	}

}
