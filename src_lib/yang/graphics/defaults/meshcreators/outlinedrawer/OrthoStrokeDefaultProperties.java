package yang.graphics.defaults.meshcreators.outlinedrawer;

import yang.graphics.textures.TextureCoordBounds;
import yang.graphics.textures.TextureCoordinatesQuad;

public class OrthoStrokeDefaultProperties extends OrthoStrokeProperties {

	public OrthoStrokeDefaultProperties(TextureCoordBounds texBounds) {
		super(texBounds);

		//super.setLineTexCoords(new TextureCoordinatesQuad().initBiased(0, 0.5f, 1, 0.75f, 0));
		super.setLineTexCoords(2,3);
		super.putPatch(0, 0, 0);
		super.putPatch(LEFT | RIGHT, 1,0);
		super.putPatch(UP | DOWN, 1,0, TextureCoordinatesQuad.ROTATE_CW_90);
		super.putTurns(2,0);
		super.putEndings(0,1);
		super.putMerging(1,1);
		super.putPatch(UP | RIGHT | DOWN | LEFT, 2, 1);

	}

	public OrthoStrokeDefaultProperties() {
		this(new TextureCoordBounds(0,0,1,1));
	}

}
