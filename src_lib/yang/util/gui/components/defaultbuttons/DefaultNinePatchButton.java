package yang.util.gui.components.defaultbuttons;

import yang.util.gui.components.defaultdrawers.GUICaptionDrawer;
import yang.util.gui.components.defaultdrawers.GUINinePatchDrawer;


public class DefaultNinePatchButton extends GUIButton {

	public DefaultNinePatchButton() {
		super.setPasses(null,new GUINinePatchDrawer(),null,new GUICaptionDrawer());
	}
	
}
