package yang.util.gui.components.defaults;

import yang.graphics.font.DrawableString;
import yang.util.gui.components.GUIMultipassComponent;
import yang.util.gui.components.defaultdrawers.GUICaptionDrawer;

public class GUILabel extends GUIMultipassComponent {
	
	public GUILabel() {
		setPasses(null,null,null,new GUICaptionDrawer());
	}
	
	public GUILabel setCaption(DrawableString caption) {
		getPass(GUICaptionDrawer.class).setCaption(caption);
		return this;
	}
	
	public GUILabel createCaption(String caption) {
		getPass(GUICaptionDrawer.class).createCaption(caption);
		return this;
	}
	
}
