package yang.events.macro;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import yang.events.eventtypes.YangEvent;
import yang.events.eventtypes.YangKeyEvent;
import yang.events.eventtypes.YangPointerEvent;
import yang.events.eventtypes.YangZoomEvent;
import yang.graphics.YangSurface;

public class DefaultMacroIO extends AbstractMacroIO {

	public static final int ID_POINTER_EVENT = 0;
	public static final int COUNT_POINTER_ACTIONS = 4;
	public static final int ID_KEY_EVENT = ID_POINTER_EVENT+COUNT_POINTER_ACTIONS;
	public static final int COUNT_KEY_ACTIONS = 2;
	public static final int ID_ZOOM_EVENT = 6;
	
	public DefaultMacroIO(YangSurface surface) {
		super(surface);
	}

	@Override
	protected void writeEvent(DataOutputStream stream, YangEvent event) throws IOException {
		if(event instanceof YangPointerEvent) {
			YangPointerEvent pointerEvent = (YangPointerEvent)event;
			stream.writeByte(ID_POINTER_EVENT+pointerEvent.mAction);
			stream.writeByte(pointerEvent.mId | pointerEvent.mButton<<4);
			stream.writeFloat(pointerEvent.mX);
			stream.writeFloat(pointerEvent.mY);
		} else if(event instanceof YangKeyEvent) {
			YangKeyEvent keyEvent = (YangKeyEvent)event;
			stream.writeByte(ID_KEY_EVENT+keyEvent.mAction);
			stream.writeShort(keyEvent.mKey);
		} else if(event instanceof YangZoomEvent) {
			stream.writeByte(ID_ZOOM_EVENT);
			stream.writeFloat(((YangZoomEvent)event).mValue);
		}
	}

	@Override
	protected YangEvent readEvent(DataInputStream stream) throws IOException {
		int id = stream.readByte();
		if(id>=ID_POINTER_EVENT && id<ID_POINTER_EVENT+COUNT_POINTER_ACTIONS) {
			YangPointerEvent pointerEvent = mEventQueue.newPointerEvent();
			pointerEvent.mAction = id-ID_POINTER_EVENT;
			int read = stream.readByte();
			pointerEvent.mButton = read>>4;
			pointerEvent.mId = read & 0x0F;
			pointerEvent.mX = stream.readFloat();
			pointerEvent.mY = stream.readFloat();
			return pointerEvent;
		}else if(id>=ID_KEY_EVENT && id<ID_KEY_EVENT+COUNT_KEY_ACTIONS) {
			YangKeyEvent keyEvent = mEventQueue.newKeyEvent();
			keyEvent.mAction = id-ID_KEY_EVENT;
			keyEvent.mKey = stream.readShort();
			return keyEvent;
		}else if(id==ID_ZOOM_EVENT) {
			YangZoomEvent zoomEvent = mEventQueue.newZoomEvent();
			zoomEvent.mValue = stream.readFloat();
			return zoomEvent;
		}

		return null;
	}

	
	
}
