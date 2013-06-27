package yang.events.eventtypes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import yang.events.YangEventQueue;
import yang.events.listeners.PointerEventListener;
import yang.events.listeners.RawEventListener;

public class YangPointerEvent extends AbstractPointerEvent {
	
	@Override
	public void handle(RawEventListener listener) {
		if(listener.rawEvent(this))
			return;
		if(!(listener instanceof PointerEventListener))
			return;
		PointerEventListener pointerListener = (PointerEventListener)listener;
		
		switch(mAction) {
		case ACTION_POINTERDOWN:
			pointerListener.pointerDown(mX, mY, this);
			break;
		case ACTION_POINTERMOVE:
			pointerListener.pointerMoved(mX, mY, this);
			break;
		case ACTION_POINTERDRAG:
			pointerListener.pointerDragged(mX, mY, this);
			break;
		case ACTION_POINTERUP:
			pointerListener.pointerUp(mX, mY, this);
			break;
		}
	}

//	@Override
//	public int getID() {
//		return YangEventQueue.ID_POINTER_EVENT;
//	}
//
//	@Override
//	public void writeToStream(DataOutputStream outStream) throws IOException {
//		
//	}
//
//	@Override
//	public void readFromStream(DataInputStream inStream) throws IOException {
//		// TODO Auto-generated method stub
//		
//	}
	
}
