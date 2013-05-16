package yang.util.gui.components.defaultbuttons;

import yang.util.gui.components.defaultdrawers.GUICaptionDrawer;
import yang.util.gui.components.defaultdrawers.GUIIconDrawer;
import yang.util.gui.components.defaultdrawers.GUINinePatchDrawer;


public class DefaultIconButton extends DefaultNinePatchButton {

	public DefaultIconButton() {
		super.setPasses(null,new GUINinePatchDrawer(),new GUIIconDrawer(),new GUICaptionDrawer());
	}
	
}
