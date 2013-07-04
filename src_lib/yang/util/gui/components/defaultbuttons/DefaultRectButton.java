package yang.util.gui.components.defaultbuttons;

import yang.graphics.font.DrawableString;
import yang.util.gui.components.GUIButton;
import yang.util.gui.components.defaultdrawers.GUICaptionDrawer;
import yang.util.gui.components.defaultdrawers.GUIRectDrawer;

public class DefaultRectButton extends GUIButton {

	public DefaultRectButton() {
		super();
		super.setPasses(new GUIRectDrawer(),null,null,new GUICaptionDrawer(),null);
	}

	public DefaultRectButton setCaption(DrawableString caption) {
		getPass(GUICaptionDrawer.class).setCaption(caption);
		return this;
	}
	
	public DefaultRectButton createCaption(String caption) {
		getPass(GUICaptionDrawer.class).createCaption(caption);
		return this;
	}

	public DrawableString getCaption() {
		return ((GUICaptionDrawer)mPasses[3]).mCaption;
	}
	
}
