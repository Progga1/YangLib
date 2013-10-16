package yang.util.window;

import yang.events.eventtypes.YangEvent;
import yang.events.eventtypes.YangPointerEvent;
import yang.events.listeners.RawEventListener;
import yang.math.objects.matrix.YangMatrix;

public class YangWindow<InternalType extends RawEventListener> implements RawEventListener {

	protected InternalType mInternalObject;
	protected YangMatrix mTransform = new YangMatrix();

	public YangWindow(InternalType internalObject) {
		mInternalObject = internalObject;
	}

	public void updateTransform() {
		mTransform.refreshInverted();
	}

	@Override
	public boolean rawEvent(YangEvent event) {
		if(event instanceof YangPointerEvent) {
			final YangPointerEvent pointerEvent = (YangPointerEvent)event;
		}
		event.handle(mInternalObject);
		return true;
	}

}
