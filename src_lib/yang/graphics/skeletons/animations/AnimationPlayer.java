package yang.graphics.skeletons.animations;

import yang.model.App;
import yang.model.DebugYang;
import yang.physics.massaggregation.MassAggregation;
import yang.sound.AbstractSoundManager;

public class AnimationPlayer<AnimationType extends Animation<?>> {

	public MassAggregation mSkeleton;
	public boolean mOnlyPhysics;

	protected AnimationType mStartAnimation;
	public AnimationType mCurrentAnimation;
	public boolean mPlaySounds;
	public int mStateMarkInt;
	public float mCurrentAnimationTime;
	public int mCurFrame;
	public AbstractSoundManager mSound;
	public float mAnimationSpeed;
	protected KeyFrame mCurrentPrevFrame,mCurrentNextFrame;


	public AnimationPlayer(MassAggregation skeleton,AnimationType startAnimation) {
		mSkeleton = skeleton;
		mStartAnimation = startAnimation;
		mStartAnimation = startAnimation;
		mCurrentAnimation = mStartAnimation;
		mSound = App.soundManager;
		mOnlyPhysics = false;
		mAnimationSpeed = 1;
		mPlaySounds = true;
	}

	public AnimationPlayer(MassAggregation skeleton) {
		this(skeleton,null);
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
					stopNode();
					return;
				}else{
					newTime = mCurrentAnimation.mFrameCount;
				}
			}
			mCurrentAnimationTime = newTime/mCurrentAnimation.mFramesPerSecond;
		}
		if(newTime<0) {
			if(mCurrentAnimation.mWrap==WrapMode.LOOP && mCurrentAnimation.mFrameCount>0)
				newTime += mCurrentAnimation.mFrameCount;
			else{
				if(mCurrentAnimation.mWrap==WrapMode.CALL_STOP) {
					stopNode();
					return;
				}else
					newTime = 0;
			}
			mCurrentAnimationTime = newTime/mCurrentAnimation.mFramesPerSecond;
		}

		if(mCurrentAnimation.mAutoAnimate) {
			final int frameId = (int)newTime;
			mCurrentPrevFrame = mCurrentAnimation.mPreviousFrames[frameId];
			mCurrentNextFrame = mCurrentAnimation.mNextFrames[frameId];
			if(!mCurrentAnimation.mInterpolate) {
				mCurrentPrevFrame.mPose.applyPosture(mSkeleton);
			}else{
				final KeyFrame prevFrame = mCurrentPrevFrame;
				final KeyFrame nextFrame = mCurrentNextFrame;

				if(nextFrame==null) {
					prevFrame.mPose.applyPosture(mSkeleton);
				}else{
					final float t = (newTime-prevFrame.mFirstFrame)*prevFrame.mTimeFactor;
					nextFrame.mPose.applyPosture(mSkeleton, prevFrame.mPose, t);
				}
			}
		}

	}

	public void addAnimationTime(float timeOffset) {
		setAnimationTime(mCurrentAnimationTime+timeOffset);
	}

	public void start() {
		setAnimation(mStartAnimation);
	}

	public void setAnimation(AnimationType animation) {
		mCurrentAnimation = animation;
		mCurrentAnimationTime = 0;
		proceed(0);
	}

	public void crossAnimation(AnimationType animation) {
		mCurrentAnimation = animation;
	}

	public void proceed(float deltaTime) {
		if(mCurrentAnimation==null)
			return;
		deltaTime *= mAnimationSpeed;
		if(mCurrentAnimation.mAutoSetAnimationTime)
			setAnimationTime(mCurrentAnimationTime+deltaTime);
//		if(mCurrentAnimation.mAutoRotation!=0) {
//			mSkeleton.mRotation += mCurrentAnimation.mAutoRotation*deltaTime;
//			if(!mOnlyPhysics)
//				mSkeleton.reApplyPose();
//		}
	}

	public void stopNode() {

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

	public KeyFrame getCurrentPreviousKeyFrame() {
		return mCurrentPrevFrame;
	}

	public KeyFrame getCurrentNextKeyFrame() {
		return mCurrentNextFrame;
	}

}
