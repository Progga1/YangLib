package yang.graphics.defaults.geometrycreators.outlinedrawer;

import yang.graphics.textures.TextureCoordBounds;

public class OrthoStrokeDefaultProperties extends OrthoStrokeProperties {

	public OrthoStrokeDefaultProperties(TextureCoordBounds texBounds) {
		super(texBounds);

		//super.setLineTexCoords(new TextureCoordinatesQuad().initBiased(0, 0.5f, 1, 0.75f, 0));
		super.setLineTexCoords(2,3);
		super.putPatch(0, 0, 0);
		super.putStraight(1,0);
		super.putTurns(2,0, 3,0);
		super.putEndings(0,1, 3,1);
		super.putMerging(1,1);
		super.putCross(2,1);

	}

	public OrthoStrokeDefaultProperties() {
		this(new TextureCoordBounds(0,0,1,1));
	}

}
