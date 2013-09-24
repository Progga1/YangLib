package yang.graphics.font;

import yang.graphics.textures.TextureCoordinatesQuad;
import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.translator.GraphicsTranslator;
import yang.graphics.translator.Texture;

public abstract class LegacyAbstractFont {

	protected Texture texture;
	
	protected String filename;
	protected GraphicsTranslator mGraphics;
	
	protected int fontBaseLine;
	protected int fontHeight;
	protected int border;

	protected int[] fontX; 
	protected int[] fontY; 
	protected int[] fontW; 
	protected byte[][] fontFix;

	protected TextureCoordinatesQuad[] textureTransformations;

	public void init(GraphicsTranslator graphics, int border, int overlap) {
		mGraphics = graphics;
		this.border = border;
		texture = mGraphics.mGFXLoader.getImage(filename,TextureFilter.LINEAR_MIP_LINEAR);
		textureTransformations = new TextureCoordinatesQuad[fontX.length];
		fontBaseLine += border;
		fontHeight += 2 * border;
		for(int c=0;c<fontX.length;c++) {
			fontX[c] -= border;
			fontY[c] -= border;
			fontW[c] += 2 * border;
			fontFix[c][0] += overlap;
			fontFix[c][1] += overlap;
			fontFix[c][2] += overlap;
			fontFix[c][3] += overlap;
			textureTransformations[c] = getTextureTransform(c);
		}
	}
	
	public float stringWidth(float lineHeight, String s) {
		int sLength = s.length();
		int sumWidth = 0;
		for (int i = 0; i < sLength; ++i)
			sumWidth += fontW[s.codePointAt(i)];
		return sumWidth * lineHeight / fontHeight;
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	public int getTextureWidth() {
		return texture.getWidth();
	}
	
	public int getTextureHeight() {
		return texture.getHeight();
	}
	
	public int getFontBaseLine() {
		return fontBaseLine;
	}

	public int getFontHeight() {
		return fontHeight;
	}
	
	public float getFontHeightNorm() {
		return getFontHeight() / (float)texture.getHeight();
	}

	public final int getFontX(int c) {
		return fontX[c];
	}
	
	public float getFontXNorm(int index) {
		return fontX[index] / (float)texture.getWidth();
	}
	
	public int getFontY(int c) {
		return fontY[c];
	}
	
	public float getFontYNorm(int index) {
		return fontY[index] / (float)texture.getHeight();
	}

	public int getFontW(int index) {
		return fontW[index];
	}
	
	public int getFontFix(int index, int side) {
		return fontFix[index][side];
	}
	
	public int getBorder() {
		return border;
	}
	
	private TextureCoordinatesQuad getTextureTransform(int c) {
		float charX = fontX[c] - fontFix[c][1];
		float charY = fontY[c] - fontFix[c][0];
		float charWidth = fontW[c] + fontFix[c][1] + fontFix[c][2];
		float charHeight = fontHeight + fontFix[c][0] + fontFix[c][3];
		float textureWidth = getTextureWidth();
		float textureHeight = getTextureHeight();
		TextureCoordinatesQuad result = new TextureCoordinatesQuad();
		result.init(
			charX / textureWidth,
			charY / textureHeight,
			(charX + charWidth) / textureWidth,
			(charY + charHeight) / textureHeight);
		return result;
	}
	
	public TextureCoordinatesQuad getTexTransformation(int c) {
		return textureTransformations[c];
	}
	
}