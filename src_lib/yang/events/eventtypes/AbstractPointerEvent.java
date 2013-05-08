package yang.events.eventtypes;

public abstract class AbstractPointerEvent extends YangInputEvent {

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
		
		return button+action+"("+mX+","+mY+")";
	}
	
}
