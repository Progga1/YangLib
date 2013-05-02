package yang.util.gui.components.defaultbuttons;

import yang.graphics.FloatColor;
import yang.util.Util;

public class ColoredRectButton extends BasicRectButton {

	public static FloatColor DEFAULT_COLOR = FloatColor.WHITE;
	public float[] mRGBA = new float[4];
	
	public ColoredRectButton() {
		setColor(DEFAULT_COLOR);
	}
	
	public ColoredRectButton setColor(float r,float g,float b, float a) {
		mRGBA[0] = r;
		mRGBA[1] = g;
		mRGBA[2] = b;
		mRGBA[3] = a;
		return this;
	}
	
	public ColoredRectButton setColor(float r,float g,float b) {
		return setColor(r,g,b,1);
	}
	
	public ColoredRectButton setColor(FloatColor color) {
		color.copyToArray(mRGBA);
		return this;
	}
	
	@Override
	public String propertiesToString() {
		return super.propertiesToString()+"; color="+Util.arrayToString(mRGBA,",",0);
	}
	
	@Override
	public void draw(float offsetX,float offsetY) {
		mGraphics2D.setColor(mRGBA);
		super.draw(offsetX,offsetY);
	}
	
}
