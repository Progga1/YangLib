package yang.graphics.skeletons;

import yang.graphics.skeletons.animations.AnimationPlayer;
import yang.physics.massaggregation.MassAggregation;

public interface SkeletonCarrier {

	public float getWorldX();
	public float getWorldY();
	public int getLookDirection();
	public float getScale();
	public MassAggregation getSkeleton();
	public AnimationPlayer<?> getAnimationPlayer();
	
}
