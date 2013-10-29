package yang.graphics.skeletons.animations;

import yang.graphics.skeletons.animations.interpolation.ConstantInterpolation;
import yang.graphics.skeletons.animations.interpolation.Interpolation;
import yang.graphics.skeletons.pose.Posture;

public class Animation<CarrierType> {

	public static WrapMode DEFAULT_WRAP_MODE = WrapMode.LOOP;

	public KeyFrame[] mKeyFrames;
	public KeyFrame[] mPreviousFrames;
	public KeyFrame[] mNextFrames;
	protected WrapMode mWrap;
	public boolean mAutoAnimate;
	protected float mFramesPerSecond;
	public Interpolation mInterpolation;
	public int mFrameCount;
	public float mTotalDuration;
	public boolean mInterpolate;
	public boolean mAutoSetAnimationTime = true;
	public boolean mBlocking = false;

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
		boolean insertFrame = mWrap==WrapMode.LOOP || mKeyFrames.length==1;
		mKeyFrames[0].mId = 0;
		for(int i=insertFrame?0:1;i<mKeyFrames.length;i++) {
			mFrameCount += mKeyFrames[i].mDuration;
			mKeyFrames[i].mId = i;
		}
		mTotalDuration = mFrameCount/mFramesPerSecond;
		mPreviousFrames = new KeyFrame[mFrameCount+1];
		mNextFrames = new KeyFrame[mFrameCount+1];
		int c = 0;
		int l = mKeyFrames.length;
		for(int i=0;i<l;i++) {
			int index = i;

			KeyFrame curPrevFrame = mKeyFrames[index];
			KeyFrame curNextFrame;
			if(index>=mKeyFrames.length-1) {
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
			KeyFrame curPrevFrame = mKeyFrames[mKeyFrames.length-1];
			mNextFrames[c-1] = mKeyFrames[0];
			for(int j=0;j<mKeyFrames[0].mDuration;j++) {
				mPreviousFrames[c] = curPrevFrame;
				mNextFrames[c] = mKeyFrames[0];
				c++;
			}
			curPrevFrame.mTimeFactor = 1f/mKeyFrames[0].mDuration;
		}
	}

	protected void setFrames(WrapMode wrapMode,KeyFrame... frames) {
		mKeyFrames = frames;
		mWrap = wrapMode;
		refreshKeyFrames();
	}

	protected void setFrames(KeyFrame... frames) {
		setFrames(mWrap,frames);
	}

	protected void setFrames(Posture<?, ?>[] frames,WrapMode clampMode) {
		mKeyFrames = new KeyFrame[frames.length];
		for(int i=0;i<frames.length;i++) {
			mKeyFrames[i] = new KeyFrame(frames[i]);
		}
		setFrames(clampMode,mKeyFrames);
	}

	protected void setFrames(Posture<?, ?>[] frames) {
		setFrames(frames,DEFAULT_WRAP_MODE);
	}

	protected void setFrame(Posture<?, ?> frame,WrapMode clampMode) {
		setFrames(clampMode,new KeyFrame[]{new KeyFrame(frame)});
	}

	protected void setFrame(Posture<?, ?> frame) {
		setFrames(new KeyFrame[]{new KeyFrame(frame)});
	}

	public final void start(CarrierType body) {

	}

	public String toSourceCode() {
		String res = "";
		int c = 0;
		for(KeyFrame keyFrame:mKeyFrames) {
			if(res!="")
				res += "\n";
			res += keyFrame.toSourceCode(c);
			c++;
		}
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

}
