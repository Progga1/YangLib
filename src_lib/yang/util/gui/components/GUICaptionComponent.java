package yang.util.gui.components;

import yang.graphics.font.DrawableString;
import yang.graphics.font.StringProperties;
import yang.util.gui.components.defaultdrawers.GUICaptionDrawer;

public class GUICaptionComponent extends GUIInteractiveRectComponent{

	public GUICaptionComponent setCaption(DrawableString caption) {
		getPass(GUICaptionDrawer.class).setCaption(caption);
		return this;
	}

	public GUICaptionComponent createCaption(String caption) {
		getPass(GUICaptionDrawer.class).createCaption(caption);
		return this;
	}

	public GUICaptionComponent createCaption(String caption, StringProperties properties) {
		getPass(GUICaptionDrawer.class).createCaption(caption,properties);
		return this;
	}

	public DrawableString getCaption() {
		return ((GUICaptionDrawer)mPasses[3]).mCaption;
	}

}
