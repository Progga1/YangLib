package yang.events.eventtypes;

import yang.events.listeners.KeyEventListener;
import yang.events.listeners.RawEventListener;

public class YangKeyEvent extends YangEvent {
	
	public final static int ACTION_KEYDOWN = 0;
	public final static int ACTION_KEYUP = 1;
	
	public int mKey;
	public int mAction;
	
	@Override
	public void handle(RawEventListener listener) {
		if(listener.rawEvent(this))
			return;
		if(!(listener instanceof KeyEventListener))
			return;
		if(mAction==ACTION_KEYDOWN)
			((KeyEventListener)listener).keyDown(mKey);
		else
			((KeyEventListener)listener).keyUp(mKey);
	}
	
//	@Override
//	public int getID() {
//		return YangEventQueue.ID_KEY_EVENT;
//	}
//
//	@Override
//	public void writeToStream(DataOutputStream outStream) throws IOException {
//		outStream.writeByte(mAction);
//		outStream.writeShort(mKey);
//	}
//
//	@Override
//	public void readFromStream(DataInputStream inStream) throws IOException {
//		mAction = inStream.readByte();
//		
//	}
//	
	
}
