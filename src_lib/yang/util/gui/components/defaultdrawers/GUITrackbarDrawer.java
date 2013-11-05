package yang.util.gui.components.defaultdrawers;

import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.model.FloatColor;
import yang.graphics.textures.TextureCoordinatesQuad;
import yang.util.gui.components.GUIComponentDrawPass;
import yang.util.gui.components.defaults.GUITrackbar;

public class GUITrackbarDrawer extends GUIComponentDrawPass<GUITrackbar> {

	public FloatColor mBGColor = null, mColor = FloatColor.WHITE.clone(), mHoverColor = null;
	public TextureCoordinatesQuad mBGTexCoords = TextureCoordinatesQuad.FULL_TEXTURE,mBarTexCoords = TextureCoordinatesQuad.FULL_TEXTURE;

	@Override
	public void draw(DefaultGraphics<?> graphics, GUITrackbar component) {
		if(mHoverColor!=null && (component.mHoverTime>=0 || component.mPressedTime>=0))
			graphics.setColor(mHoverColor);
		else
			graphics.setColor(component.mIndividualColor!=null?component.mIndividualColor:mColor);
		final float tX = component.mProjLeft+component.mProjWidth*component.getNormValue();
		graphics.drawRect(component.mProjLeft, component.mProjBottom, tX, component.mProjBottom+component.mProjHeight, mBarTexCoords);

		graphics.setColor(component.mIndividualColor!=null?component.mIndividualColor:(mBGColor!=null?mBGColor:mColor));
		graphics.drawRect(tX, component.mProjBottom, component.mProjLeft+component.mProjWidth, component.mProjBottom+component.mProjHeight, mBGTexCoords);
	}

	@Override
	public String toString() {
		return "trackbar";
	}

}
