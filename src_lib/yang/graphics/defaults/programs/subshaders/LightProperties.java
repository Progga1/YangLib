package yang.graphics.defaults.programs.subshaders;

import yang.graphics.model.FloatColor;
import yang.math.objects.HVector4f;

public class LightProperties {

	public HVector4f mDirection;
	public FloatColor mColor;
	
	public LightProperties() {
		mDirection = HVector4f.UP.clone();
		mColor = FloatColor.WHITE.clone();
	}
	
}
