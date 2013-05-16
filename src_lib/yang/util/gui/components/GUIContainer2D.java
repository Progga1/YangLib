package yang.util.gui.components;

import yang.graphics.defaults.DefaultGraphics;
import yang.util.gui.BasicGUI;

public class GUIContainer2D extends GUIContainer {

	public DefaultGraphics<?> mGraphics;

	@Override
	public void setGUI(BasicGUI gui) {
		super.setGUI(gui);
		mGraphics = gui.mGraphics2D;
	}
	
}
