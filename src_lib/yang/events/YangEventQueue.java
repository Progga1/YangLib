package yang.events;

import yang.events.eventtypes.YangEvent;
import yang.events.eventtypes.YangKeyEvent;
import yang.events.eventtypes.YangPointerEvent;
import yang.events.eventtypes.YangZoomEvent;
import yang.events.listeners.YangEventListener;
import yang.events.macro.MacroWriter;
import yang.graphics.translator.GraphicsTranslator;
import yang.util.NonConcurrentList;

public class YangEventQueue {

	public static final int ID_POINTER_EVENT = 0;
	public static final int ID_KEY_EVENT = 1;
	public static final int ID_ZOOM_EVENT = 2;
	
	private int mMaxEvents;
	private YangPointerEvent[] mPointerEventQueue;
	private YangKeyEvent[] mKeyEventQueue;
	private YangZoomEvent[] mZoomEventQueue;
	public YangEvent[][] mQueuePools;
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
	public NonConcurrentList<MacroWriter> mMacroWriters;
	
	public YangEventQueue(int maxEvents,int eventTypes) {
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
		mQueuePools = new YangEvent[eventTypes][];
		mQueuePools[ID_POINTER_EVENT] = mPointerEventQueue;
		mQueuePools[ID_KEY_EVENT] = mKeyEventQueue;
		mQueuePools[ID_ZOOM_EVENT] = mZoomEventQueue;
	}
	
	public YangEventQueue(int maxEvents) {
		this(maxEvents,3);
	}
	
	public synchronized void putRuntimeEvent(YangEvent event) {
		if(mMetaMode) {
			putMetaEvent(event);
			return;
		}
		mQueue[mQueueId++] = event;
		if(mQueueId>=mMaxEvents)
			mQueueId = 0;
	}
	
	public synchronized void putEvent(YangEvent event) {
		mQueue[mQueueId++] = event;
		if(mQueueId>=mMaxEvents)
			mQueueId = 0;
	}
	
	public synchronized void putMetaEvent(YangEvent event) {
		mMetaEventQueue[mMetaEventQueueId++] = event;
		if(mMetaEventQueueId>=mMaxEvents)
			mMetaEventQueueId = 0;
	}
	
	public void registerEventWriter(MacroWriter writer) {
		if(mMacroWriters==null)
			mMacroWriters = new NonConcurrentList<MacroWriter>();
		mMacroWriters.add(writer);
		writer.start();
	}
	
	public void removeEventWriter(MacroWriter writer) {
		mMacroWriters.remove(writer);
	}
	
	public void setMetaKey(int code) {
		mMetaKeys[code] = true;
	}
	
	public synchronized YangPointerEvent newPointerEvent() {
		YangPointerEvent newEvent = mPointerEventQueue[mPointerEventId++];
		if(mPointerEventId>=mMaxEvents)
			mPointerEventId = 0;
		return newEvent;
	}
	
	public synchronized YangKeyEvent newKeyEvent() {
		YangKeyEvent newEvent = mKeyEventQueue[mKeyEventId++];
		if(mKeyEventId>=mMaxEvents)
			mKeyEventId = 0;
		return newEvent;
	}
	
	public synchronized YangZoomEvent newZoomEvent() {
		YangZoomEvent newEvent = mZoomEventQueue[mZoomEventId++];
		if(mZoomEventId>=mMaxEvents)
			mZoomEventId = 0;
		return newEvent;
	}
	
	public synchronized void putPointerEvent(int action, int x,int y, int button, int id) {
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
		putRuntimeEvent(newEvent);
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
			putRuntimeEvent(newEvent);
	}
	
	public void putZoomEvent(float value) {
		YangZoomEvent newEvent = mZoomEventQueue[mZoomEventId++];
		if(mZoomEventId>=mMaxEvents)
			mZoomEventId = 0;
		newEvent.mValue = value;
		putRuntimeEvent(newEvent);
	}
	
	public synchronized YangEvent peekEvent() {
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
	
	public synchronized void handleEvents(YangEventListener eventInterface) {
		YangEvent event;
		while((event = pollEvent())!=null) {
			if(mMacroWriters!=null) {
				for(MacroWriter writer:mMacroWriters) {
					writer.writeEvent(event);
				}
			}
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

	public void close() {
		if(mMacroWriters!=null) {
			for(MacroWriter writer:mMacroWriters)
				writer.close();
		}
	}
	
}