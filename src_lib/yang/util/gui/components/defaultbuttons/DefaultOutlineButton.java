package yang.util.gui.components.defaultbuttons;

import yang.util.gui.components.defaultdrawers.GUICaptionDrawer;
import yang.util.gui.components.defaultdrawers.GUIIconDrawer;
import yang.util.gui.components.defaultdrawers.GUIOutlineDrawer;
import yang.util.gui.components.defaultdrawers.GUIRectDrawer;

public class DefaultOutlineButton extends DefaultIconButton {

	public DefaultOutlineButton() {
		GUIOutlineDrawer outlineDrawer = new GUIOutlineDrawer();
		outlineDrawer.mStroke.mProperties.mWidth = 0.02f;
		super.setPasses(new GUIRectDrawer(),null,new GUIIconDrawer(),new GUICaptionDrawer(),outlineDrawer);
		
	}
	
}
