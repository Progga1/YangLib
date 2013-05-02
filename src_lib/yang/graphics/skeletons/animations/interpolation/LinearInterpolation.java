package yang.graphics.skeletons.animations.interpolation;

public class LinearInterpolation extends Interpolation {

public final static LinearInterpolation INSTANCE = new LinearInterpolation();
	
	private LinearInterpolation() {
		
	}
	
	@Override
	public float getInterpolated(float value) {
		return value;
	}

	
	
}
