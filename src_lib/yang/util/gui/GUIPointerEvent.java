package yang.util.gui;

import yang.events.eventtypes.AbstractPointerEvent;
import yang.events.listeners.RawEventListener;
import yang.util.gui.components.GUIComponent;
import yang.util.gui.interfaces.GUIPointerListener;

public class GUIPointerEvent extends AbstractPointerEvent {

	public final static int ACTION_CLICK = 4;
	public GUIComponent mSender;
	
	@Override
	public void handle(RawEventListener eventInterface) {
		eventInterface.rawEvent(this);
		if(!(eventInterface instanceof GUIPointerListener))
			return;
		GUIPointerListener pointerListener = (GUIPointerListener)eventInterface;
		
		switch(mAction) {
		case ACTION_POINTERDOWN:
			pointerListener.guiPointerDown(mX, mY, this);
			break;
		case ACTION_POINTERMOVE:
			pointerListener.guiPointerMoved(mX, mY, this);
			break;
		case ACTION_POINTERDRAG:
			pointerListener.guiPointerDragged(mX, mY, this);
			break;
		case ACTION_POINTERUP:
			pointerListener.guiPointerUp(mX, mY, this);
			break;
		}
	}
	
	public void createFromPointerEvent(AbstractPointerEvent pointerEvent,GUIComponent sender) {
		mX = pointerEvent.mX-sender.mPosX;
		mY = pointerEvent.mY-sender.mPosY;
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
