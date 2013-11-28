package yang.util.gui.components.defaults;

import yang.graphics.font.DrawableString;
import yang.graphics.textures.TextureCoordinatesQuad;
import yang.util.gui.GUIPointerEvent;
import yang.util.gui.components.defaultbuttons.GUIButton;
import yang.util.gui.components.defaultdrawers.GUICaptionDrawer;
import yang.util.gui.components.defaultdrawers.GUIIconDrawer;

public abstract class GUICheckbox extends GUIButton {

	private boolean mChecked = false;
	protected GUIIconDrawer mIconDrawer;
	protected GUICaptionDrawer mCaptionDrawer;

	public GUICheckbox(TextureCoordinatesQuad checkIcon) {
		mIconDrawer = new GUIIconDrawer();
		mIconDrawer.setIcon(checkIcon, 1);
		mCaptionDrawer = new GUICaptionDrawer();
		mCaptionDrawer.setComponentAnchor(DrawableString.ANCHOR_RIGHT+0.1f);
	}

	public boolean isChecked() {
		return mChecked;
	}

	public void setChecked(boolean checked) {
		mChecked = checked;
		mIconDrawer.mVisible = checked;
	}

	@Override
	public void guiClick(GUIPointerEvent event) {
		setChecked(!mChecked);
		if(mActionListener!=null)
			mActionListener.onGUIAction(this);
	}

}
