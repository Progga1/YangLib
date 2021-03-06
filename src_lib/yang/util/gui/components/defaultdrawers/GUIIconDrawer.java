package yang.util.gui.components.defaultdrawers;

import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.model.FloatColor;
import yang.graphics.textures.TextureCoordinatesQuad;
import yang.util.gui.components.GUIComponentDrawPass;
import yang.util.gui.components.GUIInteractiveRectComponent;

public class GUIIconDrawer extends GUIComponentDrawPass<GUIInteractiveRectComponent> {

	public TextureCoordinatesQuad mIconTexCoords;
	public float mIconSize = 1;
	public FloatColor mIconColor = FloatColor.WHITE.clone();
	public FloatColor mIconColorDisabled = FloatColor.WHITE.clone();
	public boolean mVisible = true;

	public GUIIconDrawer setIcon(TextureCoordinatesQuad texCoords,float iconSize) {
		mIconTexCoords = texCoords;
		mIconSize = iconSize;
		return this;
	}

	@Override
	public void draw(DefaultGraphics<?> graphics, GUIInteractiveRectComponent component) {
		if(mVisible && mIconTexCoords!=null) {
			graphics.setColor(component.mEnabled?mIconColor.mValues:mIconColorDisabled.mValues);
			graphics.drawRectCentered(component.getProjCenterX(),component.getProjCenterY(),mIconSize,mIconTexCoords);
		}
	}

}
