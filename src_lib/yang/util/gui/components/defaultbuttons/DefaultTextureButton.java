package yang.util.gui.components.defaultbuttons;

import yang.graphics.textures.TextureCoordinatesQuad;
import yang.graphics.translator.Texture;

public class DefaultTextureButton extends DefaultCaptionButton {

	public Texture mTexture;
	public TextureCoordinatesQuad mTexCoords;
	
	public DefaultTextureButton setTexture(Texture texture) {
		mTexture = texture;
		mTexCoords = new TextureCoordinatesQuad();
		mTexCoords.init(0, 0, 1);
		return this;
	}
	
	public DefaultTextureButton setBackgroundTexCoords(TextureCoordinatesQuad texCoords) {
		mTexCoords = texCoords;
		return this;
	}
	
	protected void drawBackground() {
		mGraphics2D.bindTexture(mTexture);
		mGraphics2D.setColor(mColor);
		mGraphics2D.drawRectWH(mProjLeft, mProjBottom, mProjWidth, mProjHeight, mTexCoords);
	}
	
	@Override
	public void draw() {
		drawBackground();
		drawCaption();
	}
	
}
