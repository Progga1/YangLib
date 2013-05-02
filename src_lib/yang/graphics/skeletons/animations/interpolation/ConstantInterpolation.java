package yang.graphics.skeletons.animations.interpolation;

public class ConstantInterpolation extends Interpolation {

	public final static ConstantInterpolation INSTANCE = new ConstantInterpolation();
	
	private ConstantInterpolation() {
		
	}
	
	@Override
	public float getInterpolated(float value) {
		return 0;
	}

	
	
}
