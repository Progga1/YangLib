package yang.events.eventtypes;

import yang.math.objects.Point3f;
import yang.math.objects.Vector3f;


public abstract class YangPointerEvent extends YangEvent {

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
	public float mZ;
	public float mDeltaX,mDeltaY,mDeltaZ;
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
	public void onPoll() {
		final PointerTracker pointer = mInputState.mPointerTrackers[mId];
		mDeltaX = mX - pointer.mX;
		mDeltaY = mY - pointer.mY;
		mDeltaZ = mZ - pointer.mZ;
		pointer.mLastMovementRealtime = mInputState.getTime();
		if(mAction!=ACTION_POINTERMOVE)
			pointer.mLastTouchRealtime = mInputState.getTime();
		pointer.mX = mX;
		pointer.mY = mY;
		pointer.mZ = mZ;
		if(mAction==ACTION_POINTERDOWN) {
			mInputState.mPointerDistance = -1;
			mInputState.mCurPointerDownCount++;
		}
		if(mAction==ACTION_POINTERUP)
			mInputState.mCurPointerDownCount--;
		if(mInputState.mTriggerZooming && mInputState.mCurPointerDownCount==2 && mAction==ACTION_POINTERDRAG) {
			final float dist = mInputState.mPointerTrackers[0].getDistance(mInputState.mPointerTrackers[1]);
			if(mInputState.mPointerDistance>=0) {
				final float deltaDist = dist-mInputState.mPointerDistance;
				mInputState.mEventQueue.putZoomEvent(-deltaDist);
			}
			mInputState.mPointerDistance = dist;
		}
	}

	public PointerTracker getTrackingData() {
		return mInputState.mPointerTrackers[mId];
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
		final String action = actionToString();
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

		return button+action+"(x,y,z="+mX+","+mY+","+mZ+"; Id="+mId+")";
	}

	public void setPos(float x,float y,float z) {
		mDeltaX = x-mX;
		mDeltaY = y-mY;
		mDeltaZ = z-mZ;
		mX = x;
		mY = y;
		mZ = z;
	}

	public void setPos(Point3f position) {
		setPos(position.mX,position.mY,position.mZ);
	}

	public void setDelta(Vector3f delta) {
		mDeltaX = delta.mX;
		mDeltaY = delta.mY;
		mDeltaZ = delta.mZ;
	}

}
