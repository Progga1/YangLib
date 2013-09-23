package yang.graphics.skeletons.defaults;

import yang.graphics.defaults.DefaultAnimationPlayer;
import yang.graphics.skeletons.Skeleton2D;
import yang.graphics.skeletons.SkeletonCarrier;
import yang.graphics.skeletons.animations.AnimationPlayer;
import yang.physics.massaggregation.MassAggregation;

public class DefaultSkeletonCarrier implements SkeletonCarrier {

	private MassAggregation mSkeleton;
	public DefaultAnimationPlayer mAnimationPlayer;
	public float mPosX;
	public float mPosY;
	public int mLookDirection;
	public float mScale;
	
	public DefaultSkeletonCarrier(MassAggregation skeleton) {
		mPosX = 0;
		mPosY = 0;
		mScale = 1;
		mLookDirection = 1;
		setSkeleton(skeleton);
	}
	
	public float getWorldX() {
		return mPosX;
	}

	public float getWorldY() {
		return mPosY;
	}

	public int getLookDirection() {
		return mLookDirection;
	}

	public float getScale() {
		return mScale;
	}

	public MassAggregation getSkeleton() {
		return mSkeleton;
	}

	public void setSkeleton(MassAggregation skeleton) {
		mSkeleton = skeleton;
		mAnimationPlayer = new DefaultAnimationPlayer(skeleton,null);
	}

	public void drawCollision() {
		
	}

	public AnimationPlayer<?> getAnimationPlayer() {
		return mAnimationPlayer;
	}

}
