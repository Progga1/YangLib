package yang.graphics.util.clippedrect;

import yang.events.eventtypes.YangEvent;
import yang.events.eventtypes.SurfacePointerEvent;
import yang.events.listeners.PointerEventListener;
import yang.graphics.translator.GraphicsTranslator;
import yang.math.objects.Bounds;

public class ClippedRect implements PointerEventListener{

	public GraphicsTranslator mGraphics;
	public Bounds mBounds;
	public ClippedDrawerCallback mDrawer;
	public float mDragX,mDragY;
	public float mShiftX,mShiftY;
	
	public ClippedRect(GraphicsTranslator graphics,ClippedDrawerCallback drawer) {
		mGraphics = graphics;
		mDrawer = drawer;
		mBounds = new Bounds(0,0,1,1);
	}
	
	public void step(float deltaTime) {
		
	}
	
	public void draw() {
		mGraphics.flush();
		mGraphics.setScissorRectNormalized(mBounds);
		mDrawer.draw(mBounds,mShiftX,mShiftY);
		mGraphics.flush();
		mGraphics.switchScissor(false);
	}

	@Override
	public boolean rawEvent(YangEvent event) {
		return false;
	}

	@Override
	public void pointerDown(float x, float y, SurfacePointerEvent event) {
		mDragX = x;
		mDragY = y;
	}

	@Override
	public void pointerMoved(float x, float y, SurfacePointerEvent event) {
		
	}

	@Override
	public void pointerDragged(float x, float y, SurfacePointerEvent event) {
		float deltaX = mDragX-x;
		float deltaY = mDragY-y;
		mShiftX += deltaX;
		mShiftY += deltaY;
	}

	@Override
	public void pointerUp(float x, float y, SurfacePointerEvent event) {
		
	}
	
}
