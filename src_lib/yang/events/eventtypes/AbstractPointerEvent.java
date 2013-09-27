package yang.events.eventtypes;

import yang.model.DebugYang;

public abstract class AbstractPointerEvent extends YangEvent {

	public int mAction;
	public static final int BUTTON_NONE = -1;
	public static final int BUTTON_LEFT = 0;
	public static final int BUTTON_MIDDLE = 2;
	public static final int BUTTON_RIGHT = 1;
	
	public static final int ACTION_POINTERDOWN = 0;
	public static final int ACTION_POINTERMOVE = 1;
	public static final int ACTION_POINTERDRAG = 2;
	public static final int ACTION_POINTERUP = 3;
	
	public int mButton;
	public float mX;
	public float mY;
	public float mDeltaX,mDeltaY;
	public int mId;
	
	protected String actionToString() {
		switch(mAction) {
		case ACTION_POINTERDOWN:
			return "Down";
		case ACTION_POINTERMOVE:
			return "Move";
		case ACTION_POINTERDRAG:
			return "Drag";	
		case ACTION_POINTERUP:
			return "Up";
		default: return "<UndefAction>";
		}
	}
	
	public boolean isLeftButton() {
		return mButton == BUTTON_LEFT;
	}
	
	public boolean isMiddleButton() {
		return mButton == BUTTON_MIDDLE;
	}
	
	public boolean isRightButton() {
		return mButton == BUTTON_RIGHT;
	}
	
	@Override
	public void onPut() {
		PointerTracker pointer = mEventQueue.mPointerTrackers[mId];
		mDeltaX = mX - pointer.mPosX;
		mDeltaY = mY - pointer.mPosY;
		pointer.mLastMovement = mEventQueue.getTime();
		if(mAction!=ACTION_POINTERMOVE)
			pointer.mLastTouch = mEventQueue.getTime();
		pointer.mPosX = mX;
		pointer.mPosY = mY;
		if(mAction==ACTION_POINTERDOWN) {
			mEventQueue.mPointerDistance = -1;
			mEventQueue.mCurPointerDownCount++;
		}
		if(mAction==ACTION_POINTERUP)
			mEventQueue.mCurPointerDownCount--;
		if(mEventQueue.mTriggerZooming && mEventQueue.mCurPointerDownCount==2 && mAction==ACTION_POINTERDRAG) {
			float dist = mEventQueue.mPointerTrackers[0].getDistance(mEventQueue.mPointerTrackers[1]);
			if(mEventQueue.mPointerDistance>=0) {
				float deltaDist = dist-mEventQueue.mPointerDistance;
				mEventQueue.putZoomEvent(-deltaDist);
			}
			mEventQueue.mPointerDistance = dist;	
		}
	}
	
	public PointerTracker getTrackingData() {
		return mEventQueue.mPointerTrackers[mId];
	}
	
//	public float getDeltaX() {
//		return mEventQueue.mPointerTrackers[mId].mDeltaX;
//	}
//	
//	public float getDeltaY() {
//		return mEventQueue.mPointerTrackers[mId].mDeltaY;
//	}
//	
//	public float getCurrentX() {
//		return mEventQueue.mPointerTrackers[mId].mX;
//	}
//	
//	public float getCurrentY() {
//		return mEventQueue.mPointerTrackers[mId].mY;
//	}
	
	@Override
	public String toString() {
		String action = actionToString();
		String button;
		switch(mButton) {
		case BUTTON_LEFT:
			button = "Left";
			break;
		case BUTTON_MIDDLE:
			button = "Middle";
			break;
		case BUTTON_RIGHT:
			button = "Right";
			break;
		default: button = "";
		}
		
		return button+action+"("+mX+","+mY+", Id="+mId+")";
	}
	
}
