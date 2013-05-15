package yang.util.gui.components.defaultbuttons;

import yang.graphics.textures.TextureCoordinatesQuad;

public class DefaultIconButton extends DefaultCaptionButton {

	public TextureCoordinatesQuad mIconTexCoords;
	public float mIconSize;
	
	public DefaultIconButton setIcon(TextureCoordinatesQuad texCoords,float iconSize) {
		mIconTexCoords = texCoords;
		mIconSize = iconSize;
		return this;
	}
	
	public void draw() {
		
	}
	
}
