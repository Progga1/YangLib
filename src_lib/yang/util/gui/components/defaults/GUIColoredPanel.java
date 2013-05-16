package yang.util.gui.components.defaults;

import yang.util.gui.components.GUIContainer2D;
import yang.util.gui.components.defaultdrawers.GUIRectDrawer;

public class GUIColoredPanel extends GUIContainer2D{
	
	public GUIColoredPanel() {
		super();
		setExtends(1,1);
		super.setPasses(new GUIRectDrawer());
	}
	
}
