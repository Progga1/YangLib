package yang.events;

import yang.events.eventtypes.PointerTracker;

public class InputState {

	private static int MAX_POINTERS = YangEventQueue.MAX_POINTERS;
	private static int MAX_KEY_INDICES = YangEventQueue.MAX_KEY_INDICES;

	public PointerTracker mPointerTrackers[] = new PointerTracker[MAX_POINTERS];
	public boolean mKeyStates[] = new boolean[MAX_KEY_INDICES];
	public float mPointerDistance = -1;
	public int mCurPointerDownCount = 0;
	public boolean mShiftDown = false;

	public boolean mTriggerZooming = true;

	public final YangEventQueue mEventQueue;

	public InputState(YangEventQueue eventQueue) {
		mEventQueue = eventQueue;
		for(int i=0;i<MAX_POINTERS;i++) {
			mPointerTrackers[i] = new PointerTracker();
		}
		for(int i=0;i<MAX_KEY_INDICES;i++) {
			mKeyStates[i] = false;
		}
	}

	public double getTime() {
		return mEventQueue.getTime();
	}

	public boolean isKeyDown(int code) {
		if(code==Keys.SHIFT)
			return mShiftDown;
		if(code>=MAX_KEY_INDICES || code<0)
			return false;
		return mKeyStates[code];
	}
}
