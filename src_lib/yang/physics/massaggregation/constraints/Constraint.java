package yang.physics.massaggregation.constraints;

import yang.graphics.skeletons.CartoonBone;

public abstract class Constraint {

	public float mStrength = 1;
	public boolean mEnabled;
	
	public abstract void apply();
	public abstract boolean containsBone(CartoonBone bone);
	
	public void recalculate() {
		
	}
	
	public Constraint() {
		mEnabled = true;
	}
	
}
