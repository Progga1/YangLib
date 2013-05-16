package yang.util.gui.components.defaultbuttons;

import yang.graphics.FloatColor;
import yang.graphics.textures.TextureCoordinatesQuad;
import yang.graphics.translator.Texture;

public class DefaultIconButton extends DefaultNinePatchButton {

	public Texture mIconTexture;
	public TextureCoordinatesQuad mIconTexCoords;
	public float mIconSize = 1;
	public FloatColor mIconColor = FloatColor.WHITE.clone();
	
	public DefaultIconButton setIcon(TextureCoordinatesQuad texCoords,float iconSize) {
		mIconTexCoords = texCoords;
		mIconSize = iconSize;
		return this;
	}
	
	public DefaultIconButton setIcon(Texture texture,TextureCoordinatesQuad texCoords,float iconSize) {
		mIconTexture = texture;
		return setIcon(texCoords,iconSize);
	}
	
	public DefaultIconButton setTexture(Texture texture) {
		mTexture = texture;
		if(mIconTexture==null)
			mIconTexture = texture;
		return this;
	}
	
	public DefaultIconButton setIconTexture(Texture texture) {
		mIconTexture = texture;
		return this;
	}
	
	public void draw() {
		drawNinePatch();
		mGraphics2D.bindTexture(mIconTexture);
		mGraphics2D.setColor(mIconColor.mValues);
		mGraphics2D.drawRectCentered(this.getProjCenterX(),this.getProjCenterY(),mIconSize,mIconTexCoords);
		drawCaption();
	}
	
}
