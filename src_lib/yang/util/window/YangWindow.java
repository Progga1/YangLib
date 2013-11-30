package yang.util.window;

import yang.events.eventtypes.SurfacePointerEvent;
import yang.events.eventtypes.YangEvent;
import yang.events.eventtypes.YangPointerEvent;
import yang.events.listeners.RawEventListener;
import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.model.FloatColor;
import yang.graphics.model.GFXDebug;
import yang.graphics.translator.GraphicsTranslator;
import yang.graphics.translator.Texture;
import yang.math.objects.Point3f;
import yang.math.objects.matrix.YangMatrix;
import yang.model.callback.Drawable;

public class YangWindow<InternalType extends RawEventListener & Drawable> implements RawEventListener {

	public static int PASS_MAIN = 0;
	public static int PASS_BACKGROUND = -1;
	public static int PASS_DEBUG = -2;

	public static int MAX_POINTERS = 16;
	public static Texture debugPointerTexture;

	protected InternalType mInternalObject;
	protected YangMatrix mTransform = new YangMatrix();
	protected YangMatrix mInvertedTransform = new YangMatrix();
	protected FloatColor[] mDebugColorPalette = GFXDebug.DEFAULT_PALETTE;
	protected DefaultGraphics<?> mGraphics;
	protected GraphicsTranslator mTranslator;
	protected boolean[] mActiveCursors;
	protected Point3f[] mCursorPositions;
	private final Point3f mTempPoint = new Point3f();
	public float mDebugPointsAlpha = 0;
	public boolean mDrawDebugPoints = false;
	public boolean mVisible = true;
	public float mMaxEventZ = 0.35f,mMinEventZ = -0.15f;

	public boolean mSolid = false;

	private final SurfacePointerEvent mTempPointerEvent = new SurfacePointerEvent();

	protected void prepareDraw() {

	}

	protected void drawBackground() {

	}

	protected void postDraw() {

	}

	protected void onPointerEvent(YangPointerEvent event) {

	}

	public void step(float deltaTime) {

	}

	public YangWindow(InternalType internalObject,DefaultGraphics<?> graphics) {
		mInternalObject = internalObject;
		mGraphics = graphics;
		mTranslator = graphics.mTranslator;
		mCursorPositions = new Point3f[MAX_POINTERS];
		mActiveCursors = new boolean[MAX_POINTERS];
		for(int i=0;i<MAX_POINTERS;i++) {
			mCursorPositions[i] = new Point3f();
			mActiveCursors[i] = false;
		}

	}

	public void updateTransform() {
		mTransform.asInverted(mInvertedTransform.mValues);
	}

	protected boolean inRangeZ(float z) {
		return z>=mMinEventZ && z<=mMaxEventZ;
	}

	public void draw(int drawPass) {
		if(!mVisible)
			return;
		prepareDraw();
		mGraphics.setGlobalTransformEnabled(true);
		mGraphics.mWorldTransform.stackPush();
		mGraphics.mWorldTransform.set(mTransform);

		if(drawPass==PASS_BACKGROUND) {
			drawBackground();
		}
		if(drawPass==PASS_MAIN) {
			mInternalObject.draw();
		}
		if(drawPass==PASS_DEBUG) {
			if(mDrawDebugPoints && mDebugPointsAlpha>0) {
				mGraphics.mTranslator.bindTexture(debugPointerTexture);
				for(int i=0;i<MAX_POINTERS;i++) {
					if(mActiveCursors[i]) {
						mGraphics.setColor(mDebugColorPalette[i%mDebugColorPalette.length]);
						mGraphics.mCurColor[3] *= mDebugPointsAlpha;
						if(!inRangeZ(mCursorPositions[i].mZ))
							mGraphics.mCurColor[3] *= 0.5f;
						mGraphics.mCurrentZ = 0.01f;
						mGraphics.drawRectCentered(mCursorPositions[i].mX,mCursorPositions[i].mY, 0.1f);
						mGraphics.mCurrentZ = 0;
					}
				}
			}
		}
		mGraphics.mTranslator.flush();

		mGraphics.mWorldTransform.stackPop();
	}

	public void draw() {
		draw(PASS_BACKGROUND);
		draw(PASS_MAIN);
		draw(PASS_DEBUG);
	}

	@Override
	public boolean rawEvent(YangEvent event) {
		if(event instanceof YangPointerEvent) {
			//final YangPointerEvent srcPointerEvent = (YangPointerEvent)event;
			final YangPointerEvent pointerEvent = (YangPointerEvent)event;
			onPointerEvent(pointerEvent);
			final float[] matrix = mInvertedTransform.mValues;
			final float x = pointerEvent.mX;
			final float y = pointerEvent.mY;
			final float z = pointerEvent.mZ;
			final float w = matrix[3] * x + matrix[7] * y + matrix[11] * z + matrix[15];
			mActiveCursors[pointerEvent.mId] = true;
			mCursorPositions[pointerEvent.mId].set(x,y,z);
			pointerEvent.mX = (matrix[0] * x + matrix[4] * y + matrix[8] * z + matrix[12])/w;
			pointerEvent.mY = (matrix[1] * x + matrix[5] * y + matrix[9] * z + matrix[13])/w;
			pointerEvent.mZ = (matrix[2] * x + matrix[6] * y + matrix[10] * z + matrix[14])/w;
			final boolean eventOK = pointerEvent.mZ>=mMinEventZ && pointerEvent.mZ<=mMaxEventZ;
			mCursorPositions[pointerEvent.mId].set(pointerEvent.mX,pointerEvent.mY,pointerEvent.mZ);
			if(eventOK)
				pointerEvent.handle(mInternalObject);
			pointerEvent.mX = x;
			pointerEvent.mY = y;
			pointerEvent.mZ = z;
			return eventOK;
		}

		return false;
	}

}
