package yang.graphics.skeletons.defaults;

import yang.graphics.defaults.Default2DGraphics;
import yang.graphics.defaults.DefaultAnimationPlayer;
import yang.graphics.skeletons.Skeleton;
import yang.graphics.skeletons.SkeletonCarrier;
import yang.graphics.skeletons.animations.AnimationPlayer;

public class DefaultSkeletonCarrier implements SkeletonCarrier {

	private Skeleton mSkeleton;
	public DefaultAnimationPlayer mAnimationPlayer;
	public Default2DGraphics mGraphics2D;
	public float mPosX;
	public float mPosY;
	public int mLookDirection;
	public float mScale;
	
	public DefaultSkeletonCarrier(Default2DGraphics graphics2D) {
		mGraphics2D = graphics2D;
		mPosX = 0;
		mPosY = 0;
		mScale = 1;
		mLookDirection = 1;
		
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

	public Skeleton getSkeleton() {
		return mSkeleton;
	}

	public void draw() {
		if(mSkeleton!=null) {
			mSkeleton.refreshVisualVars();
			mSkeleton.draw();
		}
	}

	public Default2DGraphics getGraphics() {
		return mGraphics2D;
	}

	public void setSkeleton(Skeleton skeleton) {
		mSkeleton = skeleton;
		mAnimationPlayer = new DefaultAnimationPlayer(skeleton,null);
	}

	public void drawCollision() {
		
	}

	public AnimationPlayer<?> getAnimationPlayer() {
		return mAnimationPlayer;
	}

}
