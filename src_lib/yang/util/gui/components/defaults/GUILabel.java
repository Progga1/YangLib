package yang.util.gui.components.defaults;

import yang.graphics.font.DrawableString;
import yang.util.gui.components.GUIComponentDrawPass;
import yang.util.gui.components.GUIMultipassComponent;
import yang.util.gui.components.defaultdrawers.GUICaptionDrawer;

public class GUILabel extends GUIMultipassComponent {

	public GUICaptionDrawer mCaption;

	public GUILabel(int passCount,int textPassId) {
		super();
		mCaption = new GUICaptionDrawer();
		GUIComponentDrawPass[] passes = new GUIComponentDrawPass[passCount];
		passes[textPassId] = mCaption;
		setPasses(passes);
	}

	public GUILabel() {
		this(4,3);
	}

	public GUILabel setCaption(DrawableString caption) {
		mCaption.setCaption(caption);
		return this;
	}

	public GUILabel createCaption(String caption) {
		mCaption.createCaption(caption);
		return this;
	}

}
