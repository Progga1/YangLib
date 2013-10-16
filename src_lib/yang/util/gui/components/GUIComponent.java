package yang.util.gui.components;

import yang.util.gui.BasicGUI;

public class GUIComponent {

	public boolean mVisible = true;
	public boolean mEnabled = true;
	public float mPosX,mPosY,mPosZ;
	public float mProjLeft,mProjBottom,mProjZ;
	public BasicGUI mGUI;

	public void draw(int pass) {

	}

	public void refreshProjections(float offsetX,float offsetY) {
		mProjLeft = mGUI.mProjShiftX+(mPosX+offsetX)*mGUI.mProjXFactor;
		mProjBottom = mGUI.mProjShiftY+(mPosY+offsetY)*mGUI.mProjYFactor;
		mProjZ = mPosZ;
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

	public void init(BasicGUI gui) {
		mGUI = gui;
	}

	public GUIComponent cloneSwallow() {
		try {
			final GUIComponent instance = this.getClass().newInstance();
			instance.mPosX = mPosX;
			instance.mPosY = mPosY;
			instance.mVisible = mVisible;
			instance.mEnabled = mEnabled;
			instance.mGUI = mGUI;
			return instance;
		} catch (final InstantiationException e) {
			e.printStackTrace();
			return null;
		} catch (final IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

}
