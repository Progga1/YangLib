package yang.graphics.defaults.meshcreators.outlinedrawer;

import yang.graphics.textures.TextureCoordinatesQuad;

public class OrthoStrokeProperties {

	public TextureCoordinatesQuad[] mTable;
	public float mWidth = 0.1f;
	
	public OrthoStrokeProperties() {
		mTable = new TextureCoordinatesQuad[16];
	}
	
}
