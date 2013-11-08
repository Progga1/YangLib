package yang.graphics.skeletons.animations;

import yang.graphics.skeletons.pose.Posture;

public class KeyFrame {

	public int mId;
	public int mFirstFrame;
	public int mDuration;
	public float mTimeFactor;
	public Posture mPose;

	public KeyFrame(Posture<?,?> pose, int duration) {
		mPose = pose;
		mDuration = duration;
	}

	public KeyFrame(Posture<?,?> pose) {
		this(pose,1);
	}

	public String toSourceCode(int frameNr) {
		final String res = "public static KeyFrame frame"+frameNr+" = new KeyFrame(new "+mPose.getClassName()+"("+mPose.toSourceCode()+"),"+mDuration+");";
		return res;
	}

	@Override
	public String toString() {
		return ""+mId+": @"+mFirstFrame+" "+mDuration+"f";
	}

	@Override
	public KeyFrame clone() {
		return new KeyFrame(mPose,mDuration);
	}

	public KeyFrame setDuration(int duration) {
		mDuration = duration;
		return this;
	}

}
