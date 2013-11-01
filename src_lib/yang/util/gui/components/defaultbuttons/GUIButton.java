package yang.util.gui.components.defaultbuttons;

import yang.util.gui.GUIPointerEvent;
import yang.util.gui.components.GUICaptionComponent;
import yang.util.gui.components.defaultdrawers.GUICaptionDrawer;
import yang.util.gui.components.defaultdrawers.GUIRectDrawer;

public class GUIButton extends GUICaptionComponent {

	public GUIButton() {
		super();
		super.setPasses(new GUIRectDrawer(),null,null,new GUICaptionDrawer(),null);
	}

	@Override
	public boolean isPressable() {
		return true;
	}

	@Override
	public void guiClick(GUIPointerEvent event) {
		if(mActionListener!=null)
			mActionListener.onGUIAction(this);
	}

	public float getRelativeCenterX() {
		return mPosX+mWidth*0.5f;
	}

	public float getRelativeCenterY() {
		return mPosY+mHeight*0.5f;
	}

}
