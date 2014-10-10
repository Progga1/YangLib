package yang.graphics.skeletons.animations.interpolation;

public class CosineInterpolation extends Interpolation {

public final static CosineInterpolation INSTANCE = new CosineInterpolation();

	private CosineInterpolation() {

	}

	@Override
	public float getInterpolated(float value) {
		return 0.5f-(float)Math.cos(value*Math.PI)*0.5f;
	}



}
