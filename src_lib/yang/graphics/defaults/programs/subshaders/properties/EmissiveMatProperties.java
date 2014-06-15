package yang.graphics.defaults.programs.subshaders.properties;

import yang.graphics.model.FloatColor;
import yang.math.objects.Quadruple;

public class EmissiveMatProperties extends ColorMatProperties {

	public EmissiveMatProperties(FloatColor color) {
		super(color);
	}

	public EmissiveMatProperties() {
		this(new FloatColor(Quadruple.ZERO));
	}

	@Override
	public EmissiveMatProperties clone() {
		return new EmissiveMatProperties(mColor.clone());
	}

}
