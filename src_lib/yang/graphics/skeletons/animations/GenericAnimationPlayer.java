package yang.graphics.skeletons.animations;

import yang.graphics.skeletons.SkeletonCarrier;
import yang.sound.AbstractSound;

public class GenericAnimationPlayer<CarrierType extends SkeletonCarrier,AnimationType extends Animation<CarrierType>> extends AnimationPlayer<AnimationType> {

	public CarrierType mBody;
	
	public GenericAnimationPlayer(CarrierType body,AnimationType startAnimation) {
		super(body.getSkeleton(),startAnimation);
		mBody = body;
	}
	
	public void setAnimation(AnimationType animation) {
		if(mCurrentAnimation!=null)
			cleanUp();
		
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
			mSound.play(sound);
	}

	public void setFrame(int frame) {
		if(frame!=mCurFrame)
			mCurrentAnimation.mFrames[frame].mPose.applyPose(mSkeleton);
	}

	public void setFrameForceUpdate(int frame) {
		mCurrentAnimation.mFrames[frame].mPose.applyPose(mSkeleton);
	}

	public void restart() {
		setAnimation(this.mStartAnimation);
	}
	
	@Override
	public void stopNode() {
		mCurrentAnimation.onStop(mBody);
	}
}
