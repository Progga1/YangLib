package yang.util.gui.components.defaultbuttons;

import yang.graphics.FloatColor;
import yang.util.Util;

public class ColoredRectButton extends BasicRectButton {

	public static FloatColor DEFAULT_COLOR = FloatColor.WHITE;
	public FloatColor mColor = new FloatColor();
	
	public ColoredRectButton() {
		setColor(DEFAULT_COLOR);
	}
	
	public ColoredRectButton setColor(float r,float g,float b, float a) {
		mColor.set(r,g,b,a);
		return this;
	}
	
	public ColoredRectButton setColor(float r,float g,float b) {
		return setColor(r,g,b,1);
	}
	
	public ColoredRectButton setColor(FloatColor color) {
		mColor.set(color);
		return this;
	}
	
	@Override
	public String propertiesToString() {
		return super.propertiesToString()+"; color="+Util.arrayToString(mColor.mValues,",",0);
	}
	
	@Override
	public void draw() {
		mGraphics2D.setColor(mColor);
		super.draw();
	}
	
}
