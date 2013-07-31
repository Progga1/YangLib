package yang.graphics.defaults.programs.subshaders.properties;

import yang.graphics.model.FloatColor;
import yang.graphics.translator.Texture;

public class SpecularMatProperties {

	public FloatColor mColor = new FloatColor(FloatColor.ZERO);
	public Texture mTexture = null;
	public float mExponent = 16;
	
	public SpecularMatProperties(FloatColor color,float exponent) {
		mColor = color;
		mExponent = exponent;
	}
	
	public SpecularMatProperties() {
		this(FloatColor.WHITE.clone(),16);
	}
	
}
