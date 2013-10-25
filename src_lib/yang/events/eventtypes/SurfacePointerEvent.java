package yang.events.eventtypes;

import yang.events.listeners.PointerEventListener;
import yang.events.listeners.RawEventListener;

public class SurfacePointerEvent extends YangPointerEvent {

	@Override
	public boolean handle(RawEventListener listener) {
		if(listener.rawEvent(this))
			return true;
		if(!(listener instanceof PointerEventListener))
			return false;
		final PointerEventListener pointerListener = (PointerEventListener)listener;

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
		return true;
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
