package yang.util.gui.components.defaultdrawers;

import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.model.FloatColor;
import yang.util.Util;
import yang.util.gui.components.GUIComponentDrawPass;
import yang.util.gui.components.GUIInteractiveRectComponent;

public class GUIRectDrawer extends GUIComponentDrawPass<GUIInteractiveRectComponent> {

	public static FloatColor DEFAULT_COLOR = FloatColor.WHITE;
	public static FloatColor DEFAULT_BORDER_COLOR = FloatColor.GRAY;
	public static float DEFAULT_BORDERSIZE = 0;
	
	public float mBorderSize = DEFAULT_BORDERSIZE;
	public FloatColor mBorderColor = DEFAULT_BORDER_COLOR.clone();
	public FloatColor mColor = DEFAULT_COLOR.clone();
	
	@Override
	public void draw(DefaultGraphics<?> graphics,GUIInteractiveRectComponent component) {
		if(mBorderSize>0) {
			graphics.setColor(mBorderColor);
			graphics.drawRect(component.mProjLeft, component.mProjBottom, component.mProjLeft+component.mProjWidth, component.mProjBottom+component.mProjHeight);
		}
		graphics.setColor(component.mIndividualColor!=null?component.mIndividualColor:mColor);
		graphics.drawRect(component.mProjLeft+mBorderSize, component.mProjBottom+mBorderSize, component.mProjLeft+component.mProjWidth-mBorderSize, component.mProjBottom+component.mProjHeight-mBorderSize);
	}
	
	@Override
	public String toString() {
		return "rectColor="+Util.arrayToString(mColor.mValues,",",0);
	}
	
	public GUIRectDrawer setBorderSize(float borderSize) {
		mBorderSize = borderSize;
		return this;
	}
	
}
