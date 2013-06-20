package yang.graphics.defaults.meshcreators.outlinedrawer;

import yang.graphics.textures.TextureCoordinatesQuad;

public class OrthoStrokeDefaultProperties extends OrthoStrokeProperties {

	
	public OrthoStrokeDefaultProperties(float texRectLeft,float texRectTop,float texRectRight,float texRectBottom) {
		super.setLineTexCoords(new TextureCoordinatesQuad().init(0, 0.5f, 1, 0.75f).intoRect(texRectLeft,texRectTop,texRectRight,texRectBottom));
	}
	
}
