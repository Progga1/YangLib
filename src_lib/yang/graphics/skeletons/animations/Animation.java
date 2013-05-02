package yang.graphics.skeletons.animations;

import yang.graphics.skeletons.SkeletonCarrier;
import yang.graphics.skeletons.animations.interpolation.ConstantInterpolation;
import yang.graphics.skeletons.animations.interpolation.Interpolation;
import yang.graphics.skeletons.pose.Pose;

public class Animation<CarrierType extends SkeletonCarrier> {

	public static WrapMode DEFAULT_WRAP_MODE = WrapMode.LOOP;
	
	public KeyFrame[] mFrames;
	public KeyFrame[] mPreviousFrames;
	public KeyFrame[] mNextFrames;
	protected WrapMode mWrap;
	protected boolean mAutoAnimate;
	protected float mAutoRotation;
	protected float mFramesPerSecond;
	public Interpolation mInterpolation;
	public int mFrameCount;
	public float mTotalDuration;
	public boolean mInterpolate;
	public boolean mAutoSetAnimationTime = true;
	
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
		mWrap = WrapMode.LOOP;
		mFramesPerSecond = 1;
		mInterpolation = ConstantInterpolation.INSTANCE;
		mFrameCount = -1;
		mTotalDuration = -1;
		mInterpolate = false;
	}
	
	public void refreshKeyFrames() {
		mFrameCount = 0;
		mTotalDuration = 0;
		boolean insertFrame = mWrap==WrapMode.LOOP || mFrames.length==1;
		for(int i=insertFrame?0:1;i<mFrames.length;i++) {
			mFrameCount += mFrames[i].mDuration;
		}
		mTotalDuration = mFrameCount/mFramesPerSecond;
		mPreviousFrames = new KeyFrame[mFrameCount+1];
		mNextFrames = new KeyFrame[mFrameCount+1];
		int c = 0;
		int l = mFrames.length;
		for(int i=0;i<l;i++) {
			int index = i;
			
			KeyFrame curPrevFrame = mFrames[index];
			KeyFrame curNextFrame;
			if(index>=mFrames.length-1) {
				curNextFrame = null;
			}else
				curNextFrame = mFrames[index+1];
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
		}
		
		if(insertFrame) {
			KeyFrame curPrevFrame = mFrames[mFrames.length-1];
			mNextFrames[c-1] = mFrames[0];
			for(int j=0;j<mFrames[0].mDuration;j++) {
				mPreviousFrames[c] = curPrevFrame;
				mNextFrames[c] = mFrames[0];
				c++;
			}
		}

	}
	
	protected void setFrames(KeyFrame[] frames,WrapMode clampMode) {
		mFrames = frames;
		mWrap = clampMode;
		refreshKeyFrames();
	}
	
	protected void setFrames(KeyFrame[] frames) {
		setFrames(frames,DEFAULT_WRAP_MODE);
	}
	
	protected void setFrames(Pose[] frames,WrapMode clampMode) {
		mFrames = new KeyFrame[frames.length];
		for(int i=0;i<frames.length;i++) {
			mFrames[i] = new KeyFrame(frames[i]);
		}
		setFrames(mFrames,clampMode);
	}
	
	protected void setFrames(Pose[] frames) {
		setFrames(frames,DEFAULT_WRAP_MODE);
	}
	
	protected void setFrame(Pose frame,WrapMode clampMode) {
		setFrames(new KeyFrame[]{new KeyFrame(frame)},clampMode);
	}
	
	protected void setFrame(Pose frame) {
		setFrames(new KeyFrame[]{new KeyFrame(frame)});
	}
	
	public final void start(CarrierType body) {
		
	}
	
	public String toSourceCode() {
		String res = "";
		int c = 0;
		for(KeyFrame keyFrame:mFrames) {
			if(res!="")
				res += "\n";
			res += keyFrame.toSourceCode(c);
			c++;
		}
		return res;
	}
	
	public int timeToKeyFrameIndex(float animationTime) {
		int index = (int)(animationTime/mTotalDuration * mFrameCount);
		if(index>=mFrames.length)
			index = mFrames.length-1;
		if(index<0)
			index = 0;
		return mFrames[index].mFirstFrame;
	}
	
}
