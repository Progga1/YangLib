package yang.graphics.skeletons;

import yang.graphics.skeletons.animations.AnimationPlayer;

public interface SkeletonCarrier {

	public float getWorldX();
	public float getWorldY();
	public int getLookDirection();
	public float getScale();
	public Skeleton getSkeleton();
	public void draw();
	public void drawCollision();
	public AnimationPlayer<?> getAnimationPlayer();
	
}
