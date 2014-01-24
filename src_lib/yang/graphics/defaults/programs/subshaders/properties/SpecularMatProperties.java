package yang.graphics.defaults.programs.subshaders.properties;

import yang.graphics.model.FloatColor;

public class SpecularMatProperties extends ColorMatProperties {

	public float mExponent;

	public SpecularMatProperties(FloatColor color,float exponent) {
		super(color);
		mExponent = exponent;
	}

	public SpecularMatProperties() {
		this(new FloatColor(0.5f),16);
	}

}
