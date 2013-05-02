package yang.graphics.skeletons;

import yang.graphics.defaults.Default2DGraphics;
import yang.graphics.skeletons.animations.AnimationPlayer;

public interface SkeletonCarrier {

	public float getWorldX();
	public float getWorldY();
	public int getLookDirection();
	public float getScale();
	public Skeleton getSkeleton();
	public void draw();
	public Default2DGraphics getGraphics();
	public void setSkeleton(Skeleton skeleton);
	public void drawCollision();
	public AnimationPlayer<?> getAnimationPlayer();
	
}
