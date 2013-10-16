package yang.util.window;

import yang.events.eventtypes.YangEvent;
import yang.events.eventtypes.YangPointerEvent;
import yang.events.listeners.RawEventListener;
import yang.graphics.defaults.DefaultGraphics;
import yang.math.objects.matrix.YangMatrix;
import yang.model.callback.Drawable;

public class YangWindow<InternalType extends RawEventListener & Drawable> implements RawEventListener {

	protected InternalType mInternalObject;
	protected YangMatrix mTransform = new YangMatrix();
	protected YangMatrix mInvertedTransform = new YangMatrix();
	protected DefaultGraphics<?> mGraphics;

	protected void prepareDraw() {

	}

	public YangWindow(InternalType internalObject,DefaultGraphics<?> graphics) {
		mInternalObject = internalObject;
		mGraphics = graphics;
	}

	public void updateTransform() {
		mTransform.asInverted(mInvertedTransform.mValues);
	}

	public void draw() {
		prepareDraw();
		mGraphics.mWorldTransform.stackPush();
		mGraphics.mWorldTransform.set(mTransform);
		mInternalObject.draw();
		mGraphics.mWorldTransform.stackPop();
	}

	@Override
	public boolean rawEvent(YangEvent event) {
		if(event instanceof YangPointerEvent) {
			final YangPointerEvent pointerEvent = (YangPointerEvent)event;
			final float[] matrix = mInvertedTransform.mValues;
			final float x = pointerEvent.mX;
			final float y = pointerEvent.mY;
			final float z = pointerEvent.mZ;
			final float w = matrix[3] * x + matrix[7] * y + matrix[11] * z + matrix[15];
			pointerEvent.mX = (matrix[0] * x + matrix[4] * y + matrix[8] * z + matrix[12])/w;
			pointerEvent.mY = (matrix[1] * x + matrix[5] * y + matrix[9] * z + matrix[13])/w;
			pointerEvent.mZ = (matrix[2] * x + matrix[6] * y + matrix[10] * z + matrix[14])/w;
			pointerEvent.handle(mInternalObject);
		}

		return false;
	}

}
