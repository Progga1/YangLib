package yang.graphics.skeletons.animations;

import yang.graphics.skeletons.pose.Posture;

public class KeyFrame {

	public int mId;
	public int mFirstFrame;
	public int mDuration;
	public float mTimeFactor;
	public Posture mPose;
	public boolean mCloned = false;

	public KeyFrame(Posture<?,?> pose, int duration) {
		mPose = pose;
		mDuration = duration;
	}

	public KeyFrame(Posture<?,?> pose) {
		this(pose,1);
	}

	public String toSourceCode() {
		if(mCloned)
			return "";
		final String res = "new KeyFrame(new "+mPose.getClassName()+"("+mPose.toSourceCode()+"),"+mDuration+")";
		return res;
	}

	@Override
	public String toString() {
		return ""+mId+": @"+mFirstFrame+" "+mDuration+"f"+" "+mPose.toString();
	}

	@Override
	public KeyFrame clone() {
		final KeyFrame result = new KeyFrame(mPose,mDuration);
		result.mCloned = true;
		return result;
	}

	public KeyFrame setDuration(int duration) {
		mDuration = duration;
		return this;
	}

}
