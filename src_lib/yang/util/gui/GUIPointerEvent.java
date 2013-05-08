package yang.util.gui;

import yang.events.eventtypes.AbstractPointerEvent;
import yang.util.gui.components.GUIComponent;
import yang.util.gui.interfaces.GUIPointerListener;

public class GUIPointerEvent extends AbstractPointerEvent {

	public final static int ACTION_CLICK = 4;
	public GUIComponent mSender;
	
	public void handlePointerEvent(GUIPointerListener eventInterface) {
		switch(mAction) {
		case ACTION_POINTERDOWN:
			eventInterface.guiPointerDown(mX, mY, this);
			break;
		case ACTION_POINTERMOVE:
			eventInterface.guiPointerMoved(mX, mY, this);
			break;
		case ACTION_POINTERDRAG:
			eventInterface.guiPointerDragged(mX, mY, this);
			break;	
		case ACTION_POINTERUP:
			eventInterface.guiPointerUp(mX, mY, this);
			break;
		}
	}
	
	public void createFromPointerEvent(AbstractPointerEvent pointerEvent,GUIComponent sender) {
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
