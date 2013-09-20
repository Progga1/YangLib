package yang.graphics.skeletons.animations;

import yang.graphics.skeletons.Skeleton;
import yang.model.App;
import yang.sound.AbstractSoundManager;

public class AnimationPlayer<AnimationType extends Animation<?>> {

	public Skeleton mSkeleton;
	public boolean mOnlyPhysics;
	
	protected AnimationType mStartAnimation;
	public AnimationType mCurrentAnimation;
	public boolean mPlaySounds;
	public int mStateMarkInt;
	public float mCurrentAnimationTime;
	public int mCurFrame;
	public AbstractSoundManager mSound;
	public float mAnimationSpeed;
	public boolean mLockedAnimation;
	
	public AnimationPlayer(Skeleton skeleton,AnimationType startAnimation) {
		mSkeleton = skeleton;
		mStartAnimation = startAnimation;
		mStartAnimation = startAnimation;
		mCurrentAnimation = mStartAnimation;
		mSound = App.soundManager;
		mOnlyPhysics = false;
		mAnimationSpeed = 1;
		mPlaySounds = true;
	}
	
	public void setAnimationTime(float newTime) {
		mCurrentAnimationTime = newTime;
		if(mCurrentAnimation==null)
			return;
	
		newTime *= mCurrentAnimation.mFramesPerSecond;
		
		if(newTime>mCurrentAnimation.mFrameCount) {
			if(mCurrentAnimation.mWrap==WrapMode.LOOP && mCurrentAnimation.mFrameCount>0)
				newTime -= mCurrentAnimation.mFrameCount;
			else{
				if(mCurrentAnimation.mWrap==WrapMode.CALL_STOP) {
					mLockedAnimation = false;
					stopNode();
					return;
				}else
					newTime = mCurrentAnimation.mFrameCount;
			}
			mCurrentAnimationTime = newTime/mCurrentAnimation.mFramesPerSecond;
		}
		if(newTime<0) {
			if(mCurrentAnimation.mWrap==WrapMode.LOOP && mCurrentAnimation.mFrameCount>0)
				newTime += mCurrentAnimation.mFrameCount;
			else{
				if(mCurrentAnimation.mWrap==WrapMode.CALL_STOP) {
					mLockedAnimation = false;
					stopNode();
					return;
				}else
					newTime = 0;
			}
			mCurrentAnimationTime = newTime/mCurrentAnimation.mFramesPerSecond;
		}
		
		if(mCurrentAnimation.mAutoAnimate) {
			int frameId = (int)newTime;
			if(!mCurrentAnimation.mInterpolate) {
				mCurrentAnimation.mPreviousFrames[frameId].mPose.applyPose(mSkeleton);
			}else{
				KeyFrame prevFrame = mCurrentAnimation.mPreviousFrames[frameId];
				KeyFrame nextFrame = mCurrentAnimation.mNextFrames[frameId];
				if(nextFrame==null)
					prevFrame.mPose.applyPose(mSkeleton);
				else{
					float t = (newTime-prevFrame.mFirstFrame)*prevFrame.mTimeFactor;
					nextFrame.mPose.applyPose(mSkeleton, prevFrame.mPose, t);
				}
			}
		}
		
	}
	
	public void start() {
		setAnimation(mStartAnimation);
	}
	
	public void setAnimation(AnimationType animation) {
		if(mLockedAnimation)
			return;
		mCurrentAnimation = animation;
		mCurrentAnimationTime = 0;
		proceed(0);
		mLockedAnimation = mCurrentAnimation.mBlocking;
	}
	
	public void crossAnimation(AnimationType animation) {
		mCurrentAnimation = animation;
	}
	
	public void proceed(float deltaTime) {
		deltaTime *= mAnimationSpeed;
		if(mCurrentAnimation.mAutoSetAnimationTime)
			setAnimationTime(mCurrentAnimationTime+deltaTime);
		if(mCurrentAnimation.mAutoRotation!=0) {
			mSkeleton.mRotation += mCurrentAnimation.mAutoRotation*deltaTime;
			if(!mOnlyPhysics)
				mSkeleton.reApplyPose();
		}
	}
	
	public void stopNode() {
		
	}
	
	public void interruptAnimation() {
		mLockedAnimation = false;
	}
	
	public void setNormalizedAnimationTime(float normalAnimationTime) {
		setAnimationTime(normalAnimationTime*mCurrentAnimation.mTotalDuration);
	}

	public float getNormalizedAnimationTime() {
		if(mCurrentAnimation==null)
			return 0;
		else
			return mCurrentAnimationTime/mCurrentAnimation.mTotalDuration;
	}
	
}
