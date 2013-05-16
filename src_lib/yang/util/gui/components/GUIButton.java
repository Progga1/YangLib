package yang.util.gui.components;

import yang.util.gui.GUIPointerEvent;

public class GUIButton extends GUIInteractiveRectComponent{

	public boolean isPressable() {
		return true;
	}
	
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
