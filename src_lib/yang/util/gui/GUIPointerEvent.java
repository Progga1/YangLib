package yang.util.gui;

import yang.graphics.events.eventtypes.PointerEvent;
import yang.util.gui.components.GUIComponent;

public class GUIPointerEvent extends PointerEvent {

	public final static int ACTION_CLICK = 4;
	public GUIComponent mSender;
	
	public void createFromPointerEvent(PointerEvent pointerEvent,GUIComponent sender) {
		mX = pointerEvent.mX-sender.mLeft;
		mY = pointerEvent.mY-sender.mTop;
		mButton = pointerEvent.mButton;
		mAction = pointerEvent.mAction;
		mSender = sender;
	}
	
	@Override
	protected String actionToString() {
		if(mAction==ACTION_CLICK)
			return "Click";
		else
			return super.actionToString();
	}
	
}
