package yang.graphics.events;

import yang.graphics.events.eventtypes.AbstractKeyEvent;
import yang.graphics.events.eventtypes.AbstractZoomEvent;
import yang.graphics.events.eventtypes.InputEvent;
import yang.graphics.events.eventtypes.PointerEvent;
import yang.graphics.events.listeners.FullEventListener;
import yang.graphics.translator.GraphicsTranslator;

public class InputEventQueue {

	private int mMaxEvents;
	private PointerEvent[] mPointerEventQueue;
	private AbstractKeyEvent[] mKeyEventQueue;
	private AbstractZoomEvent[] mZoomEventQueue;
	private InputEvent[] mQueue;
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
		mQueue = new InputEvent[maxEvents];
		mPointerEventQueue = new PointerEvent[maxEvents];
		mKeyEventQueue = new AbstractKeyEvent[maxEvents];
		mZoomEventQueue = new AbstractZoomEvent[maxEvents];
		for(int i=0;i<maxEvents;i++) {
			mPointerEventQueue[i] = new PointerEvent();
			mKeyEventQueue[i] = new AbstractKeyEvent();
			mZoomEventQueue[i] = new AbstractZoomEvent();
		}
	}
	
	public synchronized void putEvent(InputEvent event) {
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
	
	public InputEvent peekEvent() {
		if(mQueueFirst==mQueueId)
			return null;
		else
			return mQueue[mQueueFirst];
	}
	
	public synchronized InputEvent pollEvent() {
		InputEvent result = peekEvent();
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
		InputEvent event;
		while((event = pollEvent())!=null) {
			event.handle(eventInterface);
		}
	}

	public void setGraphics(GraphicsTranslator graphics) {
		mGraphics = graphics;
	}
	
}
