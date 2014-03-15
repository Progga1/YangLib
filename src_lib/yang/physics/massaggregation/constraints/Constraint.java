package yang.physics.massaggregation.constraints;

import yang.physics.massaggregation.MassAggregation;
import yang.physics.massaggregation.elements.JointConnection;

public abstract class Constraint {

	public float mStrength = 1;
	public boolean mEnabled;

	public abstract void apply();
	public abstract Constraint cloneInto(MassAggregation target);

	public boolean containsBone(JointConnection bone) {
		return false;
	}

	public void recalculate() {

	}

	public Constraint() {
		mEnabled = true;
	}

}
