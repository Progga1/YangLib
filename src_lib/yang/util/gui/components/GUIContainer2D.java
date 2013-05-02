package yang.util.gui.components;

import yang.graphics.defaults.Default2DGraphics;
import yang.util.gui.BasicGUI;

public class GUIContainer2D extends GUIContainer {

	public Default2DGraphics mGraphics2D;
	
	public GUIContainer2D() {
		super();
	}

	@Override
	public void setGUI(BasicGUI gui) {
		super.setGUI(gui);
		mGraphics2D = gui.mGraphics2D;
	}
	
}
