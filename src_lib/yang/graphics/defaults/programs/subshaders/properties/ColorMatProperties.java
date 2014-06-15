package yang.graphics.defaults.programs.subshaders.properties;

import yang.graphics.model.FloatColor;
import yang.graphics.translator.Texture;

public class ColorMatProperties {

	public FloatColor mColor = new FloatColor(FloatColor.ZERO);
	public Texture mTexture = null;

	public ColorMatProperties(FloatColor color) {
		mColor = color;
	}

	public ColorMatProperties() {
		this(FloatColor.WHITE.clone());
	}

	@Override
	public String toString() {
		return mColor.toString();
	}

}
