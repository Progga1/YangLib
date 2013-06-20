package yang.graphics.defaults.meshcreators.outlinedrawer;

import yang.graphics.textures.TextureCoordinatesQuad;

public class OrthoStrokeProperties {

	public TextureCoordinatesQuad[] mTexCoordTable;
	public float mWidth = 0.1f;
	public TextureCoordinatesQuad[] mLineTexCoords;
	
	public OrthoStrokeProperties() {
		mTexCoordTable = new TextureCoordinatesQuad[16];
		mLineTexCoords = new TextureCoordinatesQuad[4];
		
		for(int i=0;i<16;i++)
			mTexCoordTable[i] = new TextureCoordinatesQuad().init(0,0,0.25f);
		for(int i=0;i<4;i++)
			mLineTexCoords[i] = new TextureCoordinatesQuad().init(0,0.5f,0.75f);
	}
	
	public void setLineTexCoords(TextureCoordinatesQuad texCoords) {
		mLineTexCoords[0] = texCoords;
		mLineTexCoords[1] = texCoords;
		mLineTexCoords[2] = texCoords;
		mLineTexCoords[3] = texCoords;
	}
	
}
