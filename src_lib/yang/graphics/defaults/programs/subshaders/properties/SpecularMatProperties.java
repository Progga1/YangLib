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

	@Override
	public SpecularMatProperties clone() {
		SpecularMatProperties result = new SpecularMatProperties(mColor.clone(),mExponent);
		return result;
	}

	@Override
	public String toString() {
		return mColor.toString()+", "+mExponent;
	}

}
