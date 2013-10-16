package yang.util.window;

import yang.events.listeners.RawEventListener;
import yang.graphics.defaults.DefaultGraphics;
import yang.math.objects.Point3f;
import yang.math.objects.Vector3f;
import yang.model.callback.Drawable;

public class YangBillboardWindow<InternalType extends RawEventListener & Drawable> extends YangWindow<InternalType> {

	private Point3f mLookAtPoint;
	private final Point3f mPosition = new Point3f();
	private final Vector3f mScale = new Vector3f(1,1,1);

	public YangBillboardWindow(InternalType internalObject,DefaultGraphics<?> graphics) {
		super(internalObject,graphics);
	}

	@Override
	protected void prepareDraw() {
		mTransform.loadIdentity();
		mTransform.translate(mPosition);
		mTransform.scale(mScale);
	}

	public void setLookAtPointReference(Point3f point) {
		mLookAtPoint = point;
	}

	public void setLookAtPoint(float x,float y,float z) {
		if(mLookAtPoint==null)
			mLookAtPoint = new Point3f();
		mLookAtPoint.set(x,y,z);
	}

	public void setScale(float x,float y,float z) {
		mScale.set(x,y,z);
	}

	public void setScale(float x,float y) {
		mScale.mX = x;
		mScale.mY = y;
	}

	public void setScale(float xy) {
		mScale.mX = xy;
		mScale.mY = xy;
	}

}
