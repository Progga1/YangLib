package yang.graphics.skeletons.animations;

import yang.graphics.skeletons.pose.Pose;

public class KeyFrame {

	public int mFirstFrame;
	public int mDuration;
	public float mInvDuration;
	public Pose mPose;
	
	public KeyFrame(Pose<?> pose, int duration) {
		mPose = pose;
		mDuration = duration;
		mInvDuration = 1f/duration;
	}
	
	public KeyFrame(Pose<?> pose) {
		this(pose,1);
	}
	
	public String toSourceCode(int frameNr) {
		String res = "public static KeyFrame frame"+frameNr+" = new KeyFrame(new "+mPose.getClassName()+"("+mPose.toSourceCode()+"),"+mDuration+");";
		return res;
	}
	
}
