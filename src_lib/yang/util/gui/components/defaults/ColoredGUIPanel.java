package yang.util.gui.components.defaults;

import yang.graphics.FloatColor;
import yang.util.gui.components.GUIContainer2D;

public class ColoredGUIPanel extends GUIContainer2D{

	public float[] mColor;
	
	public ColoredGUIPanel() {
		super();
		mColor = FloatColor.WHITE.createArray();
	}

	public void draw(float offsetX,float offsetY) {
		mGraphics2D.mTranslator.bindTexture(null);
		mGraphics2D.setColor(mColor);
		drawRect(offsetX,offsetY);
		super.draw(offsetX, offsetY);
	}
	
	public ColoredGUIPanel setColor(float r,float g,float b, float a) {
		mColor[0] = r;
		mColor[1] = g;
		mColor[2] = b;
		mColor[3] = a;
		return this;
	}
	
	public ColoredGUIPanel setColor(float r,float g,float b) {
		return setColor(r,g,b,1);
	}
	
	public ColoredGUIPanel setColor(FloatColor color) {
		color.copyToArray(mColor);
		return this;
	}
	
}
