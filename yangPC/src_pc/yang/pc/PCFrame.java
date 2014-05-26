package yang.pc;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import yang.surface.YangSurface;

public class PCFrame extends JFrame implements WindowListener,FocusListener  {

	private static final long serialVersionUID = 42L;
	private final boolean mFirstFocLost = true;

	public YangSurface mSurface;

	public PCFrame() {
		this.addWindowListener(this);
	}

	public void present() {
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.addFocusListener(this);
	}

	protected void close() {
		mSurface.exit();
	}

	@Override
	public void windowActivated(WindowEvent arg0) {

	}

	@Override
	public void windowClosed(WindowEvent arg0) {

	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		close();
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {

	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {

	}

	@Override
	public void windowIconified(WindowEvent arg0) {

	}

	@Override
	public void windowOpened(WindowEvent arg0) {

	}

	@Override
	public void focusGained(FocusEvent arg0) {
		//mSurface.resume();

	}

	@Override
	public void focusLost(FocusEvent arg0) {
//		if(mFirstFocLost) {
//			mFirstFocLost = false;
//			return;
//		}
//		if(!mSurface.isInitialized())
//			return;
//		mSurface.mGraphics.deleteAllTextures();
//		mSurface.pause();
//		mSurface.stop();
	}

}
