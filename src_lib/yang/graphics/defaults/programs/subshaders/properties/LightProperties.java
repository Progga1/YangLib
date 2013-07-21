package yang.graphics.defaults.programs.subshaders.properties;

import yang.graphics.model.FloatColor;
import yang.math.objects.Vector3f;

public class LightProperties {

	public Vector3f mDirection;
	public FloatColor mDiffuse;
	
	public LightProperties(Vector3f direction,FloatColor diffuse) {
		
	}
	
	public LightProperties() {
		mDirection = Vector3f.UP.clone();
		mDiffuse = FloatColor.WHITE.clone();
	}
	
}
