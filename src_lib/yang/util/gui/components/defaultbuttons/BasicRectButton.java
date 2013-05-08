package yang.util.gui.components.defaultbuttons;

import yang.graphics.defaults.Default2DGraphics;
import yang.util.gui.BasicGUI;
import yang.util.gui.GUIPointerEvent;
import yang.util.gui.components.RectangularInteractiveGUIComponent;

public class BasicRectButton extends RectangularInteractiveGUIComponent {

	public Default2DGraphics mGraphics2D;
	
	public BasicRectButton() {
		super(0.6f,0.2f);
	}
	
	@Override
	public void setGUI(BasicGUI gui) {
		super.setGUI(gui);
		mGraphics2D = gui.mGraphics2D;
	}
	
	public void guiClick(GUIPointerEvent event) {
		if(mActionListener!=null)
			mActionListener.onGUIAction(this);
	}
	
	@Override
	public void draw(float offsetX,float offsetY) {
		drawRect(offsetX,offsetY);
	}
	
	public float getRelativeCenterX() {
		return mPosX+mWidth*0.5f;
	}
	
	public float getRelativeCenterY() {
		return mPosY+mHeight*0.5f;
	}
	
	public String propertiesToString() {
		return super.propertiesToString()+"; extends="+mWidth+","+mHeight;
	}
	
	@Override
	public String toString() {
		return "Button: "+propertiesToString();
	}
	
}
