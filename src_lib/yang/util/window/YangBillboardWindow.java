package yang.util.window;

import yang.events.listeners.RawEventListener;
import yang.graphics.defaults.DefaultGraphics;
import yang.math.objects.Point3f;
import yang.math.objects.Vector3f;
import yang.model.Extents;
import yang.model.callback.Drawable;

public class YangBillboardWindow<InternalType extends RawEventListener & Drawable & Extents> extends YangWindow<InternalType> {

	private Point3f mLookAtPoint = new Point3f();
	private final Point3f mTargetPoint = new Point3f();
	public Point3f mPosition = new Point3f();
	private final Vector3f mScale = new Vector3f(1,1,1);
	public float mBetaWeight = 0.4f;
	public float mDelay = 0.12f;

	public YangBillboardWindow(InternalType internalObject,DefaultGraphics<?> graphics) {
		super(internalObject,graphics);
	}

	private void refreshTransform() {
		mTargetPoint.lerp(mLookAtPoint.mX,mLookAtPoint.mY*mBetaWeight+mPosition.mY*(1-mBetaWeight),mLookAtPoint.mZ,mDelay);
		mTransform.setPointFromTo(mPosition,mTargetPoint,Vector3f.UP);
		mTransform.scale(mScale);
		mTransform.postTranslate(mPosition);
		updateTransform();
	}

	@Override
	public void step(float deltaTime) {
		refreshTransform();
	}

	@Override
	protected void prepareDraw() {

	}

	public void setLookAtPointReference(Point3f point) {
		mLookAtPoint = point;
		mTargetPoint.set(point);
		refreshTransform();
	}

	public void snap() {
		//mTargetPoint.set(mLookAtPoint);
		mTargetPoint.set(mLookAtPoint.mX,mLookAtPoint.mY*mBetaWeight+mPosition.mY*(1-mBetaWeight),mLookAtPoint.mZ);
		refreshTransform();
	}

	public void setLookAtPoint(float x,float y,float z) {
		if(mLookAtPoint==null)
			mLookAtPoint = new Point3f();
		mLookAtPoint.set(x,y,z);
		refreshTransform();
	}

	public void setLookAtPoint(Point3f point) {
		setLookAtPoint(point.mX,point.mY,point.mZ);
	}

	public void setScale(float x,float y,float z) {
		mScale.set(x,y,z);
		refreshTransform();
	}

	public void setScale(float x,float y) {
		mScale.mX = x;
		mScale.mY = y;
		refreshTransform();
	}

	public void setScale(float xy) {
		mScale.mX = xy;
		mScale.mY = xy;
		refreshTransform();
	}

}
