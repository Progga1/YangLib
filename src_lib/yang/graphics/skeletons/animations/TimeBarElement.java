package yang.graphics.skeletons.animations;

public class TimeBarElement {

	public KeyFrame mPreviousFrame;
	public KeyFrame mNextFrame;
	public float mInvDelta;
	
	public TimeBarElement(KeyFrame previousFrame,KeyFrame nextFrame) {
		mPreviousFrame = previousFrame;
		mNextFrame = nextFrame;
		mInvDelta = 1f/(nextFrame.mFirstFrame-previousFrame.mFirstFrame);
	}
	
}
