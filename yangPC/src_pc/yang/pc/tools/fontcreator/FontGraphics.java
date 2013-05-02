package yang.pc.tools.fontcreator;

public class FontGraphics {
	public TextureCoordinate createTexCoords(float x1, float y1, float x2, float y2, float tw, float th) {
		return new TextureCoordinate(x1, y1, x2, y2, tw, th);
	}
}
