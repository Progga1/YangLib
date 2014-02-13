package yang.physics.massaggregation.constraints;

import yang.graphics.skeletons.CartoonBone;
import yang.physics.massaggregation.MassAggregation;

public abstract class Constraint {

	public float mStrength = 1;
	public boolean mEnabled;

	public abstract void apply();
	public abstract boolean containsBone(CartoonBone bone);
	public abstract Constraint cloneInto(MassAggregation target);

	public void recalculate() {

	}

	public Constraint() {
		mEnabled = true;
	}

}
