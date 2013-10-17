package yang.util.window;

import yang.events.eventtypes.YangEvent;
import yang.events.eventtypes.YangPointerEvent;
import yang.events.listeners.RawEventListener;
import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.model.FloatColor;
import yang.math.objects.Point3f;
import yang.math.objects.matrix.YangMatrix;
import yang.model.callback.Drawable;

public class YangWindow<InternalType extends RawEventListener & Drawable> implements RawEventListener {

	public static int MAX_POINTERS = 16;

	protected InternalType mInternalObject;
	protected YangMatrix mTransform = new YangMatrix();
	protected YangMatrix mInvertedTransform = new YangMatrix();
	protected DefaultGraphics<?> mGraphics;
	protected Point3f[] mCursorPositions;
	private final Point3f mTempPoint = new Point3f();

	protected void prepareDraw() {

	}

	public YangWindow(InternalType internalObject,DefaultGraphics<?> graphics) {
		mInternalObject = internalObject;
		mGraphics = graphics;
		mCursorPositions = new Point3f[MAX_POINTERS];
		for(int i=0;i<MAX_POINTERS;i++)
			mCursorPositions[i] = new Point3f();

	}

	public void updateTransform() {
		mTransform.asInverted(mInvertedTransform.mValues);
	}

	public void draw() {
		prepareDraw();
		mGraphics.setGlobalTransformEnabled(true);
		mGraphics.mWorldTransform.stackPush();
		mGraphics.mWorldTransform.set(mTransform);
		mInternalObject.draw();
		mGraphics.mTranslator.flush();

		//mGraphics.mWorldTransform.apply3D(mCursorPositions[0], mTempPoint);
		mGraphics.mTranslator.bindTexture(null);
		mGraphics.setColor(FloatColor.YELLOW);
		mGraphics.drawRectCentered(mCursorPositions[0].mX,mCursorPositions[0].mY, 0.1f);

		mGraphics.mTranslator.flush();

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
			mCursorPositions[pointerEvent.mId].set(x,y,z);
			pointerEvent.mX = (matrix[0] * x + matrix[4] * y + matrix[8] * z + matrix[12])/w;
			pointerEvent.mY = (matrix[1] * x + matrix[5] * y + matrix[9] * z + matrix[13])/w;
			pointerEvent.mZ = (matrix[2] * x + matrix[6] * y + matrix[10] * z + matrix[14])/w;
			mCursorPositions[pointerEvent.mId].set(pointerEvent.mX,pointerEvent.mY,pointerEvent.mZ);
			pointerEvent.handle(mInternalObject);
		}

		return false;
	}

}
