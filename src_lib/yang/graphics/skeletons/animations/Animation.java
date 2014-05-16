package yang.graphics.skeletons.animations;

import yang.graphics.skeletons.animations.interpolation.ConstantInterpolation;
import yang.graphics.skeletons.animations.interpolation.Interpolation;
import yang.graphics.skeletons.pose.Posture;
import yang.physics.massaggregation.MassAggregation;
import yang.physics.massaggregation.elements.Joint;
import yang.util.Util;

public class Animation<CarrierType> {

	public static WrapMode DEFAULT_WRAP_MODE = WrapMode.LOOP;
	public static final boolean[] ALL_JOINTS_ACTIVE = Util.createArray(256,true);

	public KeyFrame[] mKeyFrames;
	public KeyFrame[] mPreviousFrames;
	public KeyFrame[] mNextFrames;
	protected WrapMode mWrap;
	public boolean mAutoAnimate;
	public float mFramesPerSecond;
	public Interpolation mInterpolation;
	public int mFrameCount;
	public float mTotalDuration;
	public boolean mInterpolate;
	public boolean mAutoSetAnimationTime = true;
	public int mKeyFrameCount = -1;
	public int mTags = 0;
	public boolean mDebug = false;
	protected boolean[] mActiveJoints = ALL_JOINTS_ACTIVE;	//TODO not used yet, implement properly!

	protected void startVisuals(CarrierType body) { }
	public void startPhysics(CarrierType body) { }
	public void setTimePhysics(CarrierType body,float time) { }
	public void setTimeVisuals(CarrierType body,float time) { }
	public void cleanUpVisuals(CarrierType body) { }
	public void cleanUpPhysics(CarrierType body) { }
	public void onStop(CarrierType body) { }

	protected void stepPhysics(CarrierType body,float time,float deltaTime) {
		setTimePhysics(body,time);
	}

	protected void stepVisuals(CarrierType body,float time,float deltaTime) {
		setTimeVisuals(body,time);
	}

	public Animation() {
		mAutoAnimate = true;
		mWrap = DEFAULT_WRAP_MODE;
		mFramesPerSecond = 1;
		mInterpolation = ConstantInterpolation.INSTANCE;
		mFrameCount = -1;
		mTotalDuration = -1;
		mInterpolate = false;
	}

	public void refreshKeyFrames() {
		mFrameCount = 0;
		mTotalDuration = 0;
		final int keyFrameCount = mKeyFrameCount>=0?mKeyFrameCount:mKeyFrames.length;

		final boolean insertFrame = mWrap==WrapMode.LOOP || keyFrameCount==1;
		mKeyFrames[0].mId = 0;
		for(int i=insertFrame?0:1;i<keyFrameCount;i++) {
			mFrameCount += mKeyFrames[i].mDuration;
			mKeyFrames[i].mId = i;
		}
		mTotalDuration = mFrameCount/mFramesPerSecond;
		if(mPreviousFrames==null || mPreviousFrames.length<mFrameCount+1)
			mPreviousFrames = new KeyFrame[mFrameCount+1];
		if(mNextFrames==null || mNextFrames.length<mFrameCount+1)
			mNextFrames = new KeyFrame[mFrameCount+1];
		int c = 0;
		final int l = keyFrameCount;
		for(int i=0;i<l;i++) {
			final int index = i;

			final KeyFrame curPrevFrame = mKeyFrames[index];
			KeyFrame curNextFrame;
			if(index>=keyFrameCount-1) {
				curNextFrame = null;
			}else
				curNextFrame = mKeyFrames[index+1];
			curPrevFrame.mFirstFrame = c;
			int d;
			if(curNextFrame==null)
				d = 1;
			else
				d = curNextFrame.mDuration;
			for(int j=0;j<d;j++) {
				mPreviousFrames[c] = curPrevFrame;
				mNextFrames[c] = curNextFrame;
				c++;
			}
			curPrevFrame.mTimeFactor = 1f/d;
		}

		if(insertFrame) {
			final KeyFrame curPrevFrame = mKeyFrames[keyFrameCount-1];
			mNextFrames[c-1] = mKeyFrames[0];
			for(int j=0;j<mKeyFrames[0].mDuration;j++) {
				mPreviousFrames[c] = curPrevFrame;
				mNextFrames[c] = mKeyFrames[0];
				c++;
			}
			curPrevFrame.mTimeFactor = 1f/mKeyFrames[0].mDuration;
		}
	}

	public void setFrames(WrapMode wrapMode,KeyFrame... frames) {
		mKeyFrames = frames;
		mWrap = wrapMode;
		refreshKeyFrames();
	}

	public void setFrames(KeyFrame... frames) {
		setFrames(mWrap,frames);
	}

	public void setFrames(WrapMode clampMode,Posture<?, ?>... frames) {
		mKeyFrames = new KeyFrame[frames.length];
		for(int i=0;i<frames.length;i++) {
			mKeyFrames[i] = new KeyFrame(frames[i]);
		}
		setFrames(clampMode,mKeyFrames);
	}

	public void setFrames(Posture<?, ?>... frames) {
		setFrames(DEFAULT_WRAP_MODE,frames);
	}

	public final void start(CarrierType body) {

	}

	public String toSourceCode() {
		String res = "public static KeyFrame[] frames = {\n";
		int c = 0;
		for(final KeyFrame keyFrame:mKeyFrames) {
			if(c>0)
				res += ",\n";
			res += '\t'+keyFrame.toSourceCode();
			c++;
		}
		res += "\n};\n";
		return res;
	}

	public int timeToKeyFrameIndex(float animationTime) {
		int index = (int)(animationTime/mTotalDuration * mFrameCount);
		if(index>=mPreviousFrames.length)
			index = mPreviousFrames.length-1;
		if(index<0)
			index = 0;
		return mPreviousFrames[index].mId;
	}

	public void setFrameCount(int frameCount) {
		mKeyFrameCount = frameCount;
	}

	public void appendKeyFrame(KeyFrame keyFrame) {
		mKeyFrames[mKeyFrameCount++] = keyFrame;
		refreshKeyFrames();
	}

	public KeyFrame getPreviousKeyFrame(float time) {
		time *= mFramesPerSecond;
		if(time<0)
			return null;
		if(time>mFrameCount)
			return null;
		return mPreviousFrames[(int)(time)];
	}

	public KeyFrame getNextKeyFrame(float time) {
		time *= mFramesPerSecond;
		if(time<0)
			return null;
		if(time>mFrameCount)
			return null;
		return mNextFrames[(int)(time)];
	}

	public void setJointsAnimated(boolean[] animatedJoints) {
		mActiveJoints = animatedJoints;
	}

	public void setJointsAnimated(MassAggregation templateMassAggregation) {

		if(mActiveJoints==ALL_JOINTS_ACTIVE) {
			int count = 0;
			for(Joint joint:templateMassAggregation.mJoints) {
				if(joint.mAnimate) {
					count++;
				}
			}
			mActiveJoints = new boolean[count];
		}

		int c = 0;
		for(Joint joint:templateMassAggregation.mJoints) {
			if(joint.mAnimate) {
				mActiveJoints[c] = !joint.mAnimDisabled;
				c++;
			}
		}
	}

	public boolean isJointAnimated(int jointId) {
		return mActiveJoints[jointId];
	}

	public int getJointCount() {
		return mActiveJoints.length;
	}

}
