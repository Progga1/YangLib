package yang.events;

import yang.events.eventtypes.YangEvent;
import yang.events.eventtypes.YangKeyEvent;
import yang.events.eventtypes.YangPointerEvent;
import yang.events.eventtypes.YangZoomEvent;
import yang.events.listeners.YangEventListener;
import yang.graphics.translator.GraphicsTranslator;

public class YangEventQueue {

	private int mMaxEvents;
	private YangPointerEvent[] mPointerEventQueue;
	private YangKeyEvent[] mKeyEventQueue;
	private YangZoomEvent[] mZoomEventQueue;
	private YangEvent[] mQueue;
	private YangEvent[] mMetaEventQueue;
	private int mPointerEventId;
	private int mKeyEventId;
	private int mZoomEventId;
	private int mQueueId;
	private int mMetaEventQueueId;
	private int mQueueFirst;
	private int mMetaEventQueueFirst;
	private GraphicsTranslator mGraphics;
	private boolean[] mMetaKeys;
	public boolean mMetaMode = false;
	
	public YangEventQueue(int maxEvents) {
		mGraphics = null;
		mMaxEvents = maxEvents;
		mPointerEventId = 0;
		mKeyEventId = 0;
		mQueueId = 0;
		mMetaEventQueueId = 0;
		mQueueFirst = 0;
		mMetaEventQueueFirst = 0;
		mQueue = new YangEvent[maxEvents];
		mMetaEventQueue = new YangEvent[maxEvents];
		mPointerEventQueue = new YangPointerEvent[maxEvents];
		mKeyEventQueue = new YangKeyEvent[maxEvents];
		mZoomEventQueue = new YangZoomEvent[maxEvents];
		for(int i=0;i<maxEvents;i++) {
			mPointerEventQueue[i] = new YangPointerEvent();
			mKeyEventQueue[i] = new YangKeyEvent();
			mZoomEventQueue[i] = new YangZoomEvent();
		}
		mMetaKeys = new boolean[512];
		for(int i=0;i<mMetaKeys.length;i++) {
			mMetaKeys[i] = false;
		}
	}
	
	public synchronized void putEvent(YangEvent event) {
		if(mMetaMode) {
			putMetaEvent(event);
			return;
		}
		mQueue[mQueueId++] = event;
		if(mQueueId>=mMaxEvents)
			mQueueId = 0;
	}
	
	public synchronized void putMetaEvent(YangEvent event) {
		mMetaEventQueue[mMetaEventQueueId++] = event;
		if(mMetaEventQueueId>=mMaxEvents)
			mMetaEventQueueId = 0;
	}
	
	public void setMetaKey(int code) {
		mMetaKeys[code] = true;
	}
	
	public synchronized void putPointerEvent(int button, int x,int y, int action, int id) {
		if(mGraphics==null)
			return;
		YangPointerEvent newEvent = mPointerEventQueue[mPointerEventId++];
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
		YangKeyEvent newEvent = mKeyEventQueue[mKeyEventId++];
		if(mKeyEventId>=mMaxEvents)
			mKeyEventId = 0;
		newEvent.mKey = key;
		newEvent.mAction = action;
		if(key<512 && mMetaKeys[key]) 
			putMetaEvent(newEvent);
		else
			putEvent(newEvent);
	}
	
	public void putZoomEvent(float value) {
		YangZoomEvent newEvent = mZoomEventQueue[mZoomEventId++];
		if(mZoomEventId>=mMaxEvents)
			mZoomEventId = 0;
		newEvent.mValue = value;
		putEvent(newEvent);
	}
	
	public YangEvent peekEvent() {
		if(mQueueFirst==mQueueId)
			return null;
		else
			return mQueue[mQueueFirst];
	}
	
	public YangEvent peekMetaEvent() {
		if(mMetaEventQueueFirst==mMetaEventQueueId)
			return null;
		else
			return mMetaEventQueue[mMetaEventQueueFirst];
	}
	
	public synchronized YangEvent pollEvent() {
		YangEvent result = peekEvent();
		if(result==null)
			return null;
		else{
			mQueueFirst++;
			if(mQueueFirst>=mMaxEvents)
				mQueueFirst = 0;
			return result;
		}
	}
	
	public synchronized YangEvent pollMetaEvent() {
		YangEvent result = peekMetaEvent();
		if(result==null)
			return null;
		else{
			mMetaEventQueueFirst++;
			if(mMetaEventQueueFirst>=mMaxEvents)
				mMetaEventQueueFirst = 0;
			return result;
		}
	}
	
	public boolean hasEvent() {
		return mQueueFirst!=mQueueId;
	}
	
	public boolean hasMetaEvent() {
		return mMetaEventQueueFirst!=mMetaEventQueueId;
	}
	
	public void handleEvents(YangEventListener eventInterface) {
		YangEvent event;
		while((event = pollEvent())!=null) {
			event.handle(eventInterface);
		}
	}
	
	public void handleMetaEvents(YangEventListener eventInterface) {
		YangEvent event;
		while((event = pollMetaEvent())!=null) {
			event.handle(eventInterface);
		}
	}

	public void setGraphics(GraphicsTranslator graphics) {
		mGraphics = graphics;
	}

	public void setMetaKeys(int startKey, int count) {
		for(int i=0;i<count;i++) {
			setMetaKey(startKey+i);
		}
	}
	
}
