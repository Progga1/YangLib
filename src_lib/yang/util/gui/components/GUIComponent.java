package yang.util.gui.components;

import yang.util.gui.BasicGUI;

public class GUIComponent {

	public boolean mVisible = true;
	public float mLeft;
	public float mTop;
	protected BasicGUI mGUI;
	
	public void draw(float offsetX,float offsetY) {
		
	}
	
	public GUIComponent setPosition(float x,float y) {
		mLeft = x;
		mTop = y;
		return this;
	}
	
	public boolean isPressable() {
		return false;
	}
	
	public String propertiesToString() {
		return "pos="+mLeft+","+mTop;
	}
	
	public float projX(float guiX) {
		return mGUI.mProjShiftX+guiX;
	}
	
	public float projY(float guiY) {
		return mGUI.mProjShiftY-guiY;
	}
	
	public float screenToGUIX(float screenX) {
		return screenX-mGUI.mProjShiftX;
	}
	
	public float screenToGUIY(float screenY) {
		return -screenY+mGUI.mProjShiftY;
	}
	
	public void setGUI(BasicGUI gui) {
		mGUI = gui;
	}
	
}
