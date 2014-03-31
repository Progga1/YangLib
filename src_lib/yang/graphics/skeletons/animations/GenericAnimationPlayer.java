package yang.graphics.skeletons.animations;

import yang.graphics.skeletons.SkeletonCarrier;
import yang.physics.massaggregation.MassAggregation;
import yang.sound.AbstractSound;

public class GenericAnimationPlayer<CarrierType extends SkeletonCarrier,AnimationType extends Animation<CarrierType>> extends AnimationPlayer<AnimationType> {

	public CarrierType mBody;

	public GenericAnimationPlayer(CarrierType body,MassAggregation skeleton,AnimationType startAnimation) {
		super(skeleton,startAnimation);
		mBody = body;
	}

	@Override
	public void setAnimation(AnimationType animation) {
		if(animation==mCurrentAnimation)
			return;
		if(mLockedAnimation)
			return;
		if(mCurrentAnimation!=null)
			cleanUp();

		if(animation==null) {
			super.setAnimation(null);
			return;
		}
		animation.startPhysics(mBody);
		if(!mOnlyPhysics) {
			animation.startVisuals(mBody);
			//mCurFrame = mStartFrame;
//			if(mCurrentAnimation.mAutoAnimate)
//				mCurrentAnimation.mFrames[mCurFrame].mPose.applyPose(mSkeleton);
		}
		super.setAnimation(animation);
		animation.start(mBody);
	}

//	public void incFrame() {
//		mCurFrame++;
//		if(mCurFrame>mEndFrame) {
//			if(mCurrentAnimation.mLoop)
//				mCurFrame = mStartFrame;
//			else {
//				mCurrentAnimation.onStop(mBody);
//				return;
//			}
//		}
//		mCurrentAnimation.mFrames[mCurFrame].mPose.applyPose(mSkeleton);
//	}

	@Override
	public void proceed(float deltaTime) {
		super.proceed(deltaTime);
		if(mCurrentAnimation==null)
			return;
		mCurrentAnimation.stepPhysics(mBody,mCurrentAnimationTime,deltaTime);
		if(!mOnlyPhysics)
			mCurrentAnimation.stepVisuals(mBody,mCurrentAnimationTime,deltaTime);
	}

	@Override
	public final void setAnimationTime(float newTime) {
		super.setAnimationTime(newTime);
		if(mCurrentAnimation==null)
			return;

		mCurrentAnimation.setTimePhysics(mBody,newTime);
		if(!mOnlyPhysics) {
			mCurrentAnimation.setTimeVisuals(mBody,newTime);
		}
	}

	public void cleanUp() {
		mCurrentAnimation.cleanUpPhysics(mBody);
		if(!mOnlyPhysics)
			mCurrentAnimation.cleanUpVisuals(mBody);
	}


	public void playSound(AbstractSound sound) {
		if(mPlaySounds)
			sound.play();
	}

	public void setFrame(int frame) {
		if(frame!=mCurFrame)
			mCurrentAnimation.mKeyFrames[frame].mPose.applyPosture(mSkeleton);
	}

	public void setFrameForceUpdate(int frame) {
		if(frame>=mCurrentAnimation.mKeyFrames.length)
			return;
		mCurrentAnimation.mKeyFrames[frame].mPose.applyPosture(mSkeleton);
	}

	public void restart() {
		setAnimation(this.mStartAnimation);
	}

	@Override
	public void stopNode() {
		mCurrentAnimation.onStop(mBody);
	}
}
