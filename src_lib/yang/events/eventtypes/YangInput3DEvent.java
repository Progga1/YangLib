package yang.events.eventtypes;

import yang.events.listeners.Input3DListener;
import yang.events.listeners.RawEventListener;
import yang.math.objects.Point3f;
import yang.math.objects.Quaternion;

public class YangInput3DEvent extends YangEvent {

	public int mId;
	public Point3f mPosition = new Point3f();
	public Quaternion mOrientation = new Quaternion();

	@Override
	public boolean handle(RawEventListener eventInterface) {
		if(eventInterface.rawEvent(this))
			return true;
		if(eventInterface instanceof Input3DListener) {
			((Input3DListener)eventInterface).input3D(this);
			return true;
		}else
			return false;
	}

}
