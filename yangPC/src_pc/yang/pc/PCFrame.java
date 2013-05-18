package yang.pc;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import yang.graphics.YangSurface;
import yang.model.ExitCallback;

public class PCFrame extends JFrame implements ExitCallback {
	
	private static final long serialVersionUID = 42L;
	
	public YangSurface mSurface;
	
	public void present() {
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
	
	@Override
	public void exit() {
		System.exit(0);
	}
	
}
