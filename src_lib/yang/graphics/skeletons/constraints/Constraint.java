package yang.graphics.skeletons.constraints;

import yang.graphics.skeletons.elements.Bone;

public abstract class Constraint {

	public float mStrength = 1;
	public boolean mEnabled;
	
	public abstract void apply();
	public abstract boolean containsBone(Bone bone);
	
	public void recalculate() {
		
	}
	
	public Constraint() {
		mEnabled = true;
	}
	
}
