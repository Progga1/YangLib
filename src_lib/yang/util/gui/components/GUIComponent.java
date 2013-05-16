package yang.util.gui.components;

import yang.util.gui.BasicGUI;

public class GUIComponent {

	public boolean mVisible = true;
	public float mPosX;
	public float mPosY;
	public float mProjLeft,mProjBottom;
	public BasicGUI mGUI;
	
	public void draw(int pass) {
		
	}
	
	public void refreshProjections(float offsetX,float offsetY) {
		mProjLeft = mGUI.mProjShiftX+(mPosX+offsetX)*mGUI.mProjXFactor;
		mProjBottom = mGUI.mProjShiftY+(mPosY+offsetY)*mGUI.mProjYFactor;
	}
	
	public GUIComponent setPosition(float x,float y) {
		mPosX = x;
		mPosY = y;
		return this;
	}
	
	public boolean isPressable() {
		return false;
	}
	
	public String propertiesToString() {
		return "pos="+mPosX+","+mPosY;
	}
	
	public float projX(float guiX) {
		return mGUI.mProjShiftX+guiX*mGUI.mProjXFactor;
	}
	
	public float projY(float guiY) {
		return mGUI.mProjShiftY+guiY*mGUI.mProjYFactor;
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
