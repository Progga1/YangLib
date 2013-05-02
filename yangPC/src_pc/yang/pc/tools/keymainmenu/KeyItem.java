package yang.pc.tools.keymainmenu;

import java.awt.event.KeyEvent;

import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class KeyItem extends JMenuItem implements KeyHolder{

	private static final long serialVersionUID = 1L;
	
	private String key;
	private String caption;
	
	public KeyItem(String key,String caption) {
		super(caption);
		this.key = key;
		this.caption = caption;
	}
	
	public KeyItem setShortCut(char shortCut) {
		this.setAccelerator(KeyStroke.getKeyStroke(shortCut,KeyEvent.CTRL_DOWN_MASK));
		return this;
	}

	public String getKey() {
		return this.key;
	}
	
	public String getCaption() {
		return this.caption;
	}
}
