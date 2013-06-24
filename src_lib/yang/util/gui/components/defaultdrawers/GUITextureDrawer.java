package yang.util.gui.components.defaultdrawers;

import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.model.FloatColor;
import yang.graphics.textures.TextureCoordinatesQuad;
import yang.util.Util;
import yang.util.gui.components.GUIComponentDrawPass;
import yang.util.gui.components.GUIInteractiveRectComponent;

public class GUITextureDrawer extends GUIComponentDrawPass<GUIInteractiveRectComponent> {

	public static FloatColor DEFAULT_COLOR = FloatColor.WHITE;
	
	public FloatColor mColor = DEFAULT_COLOR.clone();
	public float mWidth=1, mHeight=1;
	public TextureCoordinatesQuad mTexCoords;
	
	@Override
	public void draw(DefaultGraphics<?> graphics,GUIInteractiveRectComponent component) {
		if(mTexCoords==null)
			return;
		graphics.setColor(mColor);
		graphics.drawRectCentered(component.getProjCenterX(), component.getProjCenterY(), mWidth, mHeight, 0, mTexCoords);
	}
	
	public GUITextureDrawer setTextureCoordinates(TextureCoordinatesQuad texCoords) {
		mTexCoords = texCoords;
		return this;
	}
	
	public GUITextureDrawer setSize(int width,int height) {
		mWidth = width;
		mHeight = height;
		return this;
	}
	
	public GUITextureDrawer setSize(int widthAndHeight) {
		return setSize(widthAndHeight,widthAndHeight);
	}
	
	@Override
	public String toString() {
		return "baseColor="+Util.arrayToString(mColor.mValues,",",0);
	}
	
}
