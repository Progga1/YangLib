package yang.events;

import yang.events.eventtypes.AbstractKeyEvent;
import yang.events.eventtypes.AbstractZoomEvent;
import yang.events.eventtypes.PointerEvent;
import yang.events.eventtypes.YangInputEvent;
import yang.events.listeners.FullEventListener;
import yang.graphics.translator.GraphicsTranslator;

public class InputEventQueue {

	private int mMaxEvents;
	private PointerEvent[] mPointerEventQueue;
	private AbstractKeyEvent[] mKeyEventQueue;
	private AbstractZoomEvent[] mZoomEventQueue;
	private YangInputEvent[] mQueue;
	private int mPointerEventId;
	private int mKeyEventId;
	private int mZoomEventId;
	private int mQueueId;
	private int mQueueFirst;
	private GraphicsTranslator mGraphics;
	
	public InputEventQueue(int maxEvents) {
		mGraphics = null;
		mMaxEvents = maxEvents;
		mPointerEventId = 0;
		mKeyEventId = 0;
		mQueueId = 0;
		mQueueFirst = 0;
		mQueue = new YangInputEvent[maxEvents];
		mPointerEventQueue = new PointerEvent[maxEvents];
		mKeyEventQueue = new AbstractKeyEvent[maxEvents];
		mZoomEventQueue = new AbstractZoomEvent[maxEvents];
		for(int i=0;i<maxEvents;i++) {
			mPointerEventQueue[i] = new PointerEvent();
			mKeyEventQueue[i] = new AbstractKeyEvent();
			mZoomEventQueue[i] = new AbstractZoomEvent();
		}
	}
	
	public synchronized void putEvent(YangInputEvent event) {
		mQueue[mQueueId++] = event;
		if(mQueueId>=mMaxEvents)
			mQueueId = 0;
	}
	
	public synchronized void putPointerEvent(int button, int x,int y, int action, int id) {
		if(mGraphics==null)
			return;
		PointerEvent newEvent = mPointerEventQueue[mPointerEventId++];
		if(mPointerEventId>=mMaxEvents)
			mPointerEventId = 0;
		newEvent.mButton = button;
		newEvent.mX = mGraphics.toNormX(x);
		newEvent.mY = mGraphics.toNormY(y);
		newEvent.mAction = action;
		newEvent.mId = id;
		putEvent(newEvent);
	}
	
	public synchronized void putKeyEvent(int key, int action) {
		AbstractKeyEvent newEvent = mKeyEventQueue[mKeyEventId++];
		if(mKeyEventId>=mMaxEvents)
			mKeyEventId = 0;
		newEvent.mKey = key;
		newEvent.mAction = action;
		putEvent(newEvent);
	}
	
	public void putZoomEvent(float value) {
		AbstractZoomEvent newEvent = mZoomEventQueue[mZoomEventId++];
		if(mZoomEventId>=mMaxEvents)
			mZoomEventId = 0;
		newEvent.mValue = value;
		putEvent(newEvent);
	}
	
	public YangInputEvent peekEvent() {
		if(mQueueFirst==mQueueId)
			return null;
		else
			return mQueue[mQueueFirst];
	}
	
	public synchronized YangInputEvent pollEvent() {
		YangInputEvent result = peekEvent();
		if(result==null)
			return null;
		else{
			mQueueFirst++;
			if(mQueueFirst>=mMaxEvents)
				mQueueFirst = 0;
			return result;
		}
	}
	
	public boolean hasEvent() {
		return mQueueFirst!=mQueueId;
	}
	
	public void handleEvents(FullEventListener eventInterface) {
		YangInputEvent event;
		while((event = pollEvent())!=null) {
			event.handle(eventInterface);
		}
	}

	public void setGraphics(GraphicsTranslator graphics) {
		mGraphics = graphics;
	}
	
}
