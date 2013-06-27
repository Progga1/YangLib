package yang.events.macro;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import yang.events.YangEventQueue;
import yang.events.eventtypes.YangEvent;
import yang.graphics.YangSurface;

public abstract class AbstractMacroIO {

	protected YangEventQueue mEventQueue;
	public YangSurface mSurface;
	
	protected abstract void writeEvent(DataOutputStream stream,YangEvent event) throws IOException;
	protected abstract YangEvent readEvent(DataInputStream stream) throws IOException; 
	
	public AbstractMacroIO(YangSurface surface) {
		mSurface = surface;
		mEventQueue = surface.mEventQueue;
	}
	
}
