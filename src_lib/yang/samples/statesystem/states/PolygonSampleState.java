package yang.samples.statesystem.states;

import yang.events.eventtypes.SurfacePointerEvent;
import yang.events.eventtypes.YangPointerEvent;
import yang.graphics.defaults.geometrycreators.PolygonCreator;
import yang.graphics.model.FloatColor;
import yang.graphics.translator.Texture;
import yang.samples.statesystem.SampleState;

public class PolygonSampleState extends SampleState {

	private PolygonCreator mPolygon;
	private int mPickedPoint;
	private Texture mFillTexture;
	private boolean mDrawTexture = true;
	private boolean mDrawDebug = true;
	private boolean mIntersects = false;
	private float grid = 0.1f;
	private boolean USE_GRID = false;

	@Override
	public void initGraphics() {
		mPolygon = new PolygonCreator(mGraphics2D,128);
		mFillTexture = mGFXLoader.getImage("grass");
	}

	@Override
	protected void step(float deltaTime) {

	}

	@Override
	protected void draw() {
		mGraphics.clear(0, 0, 0.1f);
		mGraphics2D.setWhite();

		if(mDrawTexture)
			mGraphics.bindTexture(mFillTexture);
		else
			mGraphics.bindTexture(null);
		mGraphics.switchCulling(false);
		mPolygon.putVertices();
		mPolygon.putTextureCoordinates(2);
		mGraphics2D.fillBuffers();

		if(mDrawDebug) {
			if(mIntersects)
				mGraphics2D.setColor(0.5f,0.5f,1f);
			else
				mGraphics2D.setColor(0.3f,0.3f,0.9f);
			mPolygon.drawTriangleLines(mGraphics2D,0.01f);
		}

		mGraphics.bindTexture(mStateSystem.mCircleTexture);
		mGraphics2D.setColor(FloatColor.RED);
		for(int i=0;i<mPolygon.getPointCount();i++) {
			mGraphics2D.drawRectCentered(mPolygon.getPosX(i), mPolygon.getPosY(i), 0.016f);
		}

	}

	@Override
	public void pointerDown(float x,float y,SurfacePointerEvent event) {

		if(event.mButton == YangPointerEvent.BUTTON_RIGHT) {
			mPickedPoint = mPolygon.pickPoint(x,y,0.03f);
		}else{
			int pick = mPolygon.pickPoint(x,y,0.025f);
			if(USE_GRID) {
				x = (int)(x/grid)*grid;
				y = (int)(y/grid)*grid;
			}
			if(pick>=0)
				mPolygon.addIndex(pick);
			else
				mPolygon.addPoint(x,y);
			mPolygon.triangulate();
		}
	}

	@Override
	public void pointerMoved(float x,float y,SurfacePointerEvent event) {
		mIntersects = mPolygon.interpenetrates(x, y);
	}

	@Override
	public void pointerDragged(float x,float y,SurfacePointerEvent event) {
		if(USE_GRID) {
			x = (int)(x/grid)*grid;
			y = (int)(y/grid)*grid;
		}
		if(mPickedPoint>=0) {
			mPolygon.triangulate();
			//mPolygon.setPointPos(mPickedPoint, (int)(x/grid)*grid, (int)(y/grid)*grid);
			mPolygon.setPointPos(mPickedPoint, x,y);
		}
	}

	@Override
	public void pointerUp(float x,float y,SurfacePointerEvent event) {
		if(mPickedPoint>=0)
			mPolygon.triangulate();
		mPickedPoint = -1;
	}

	@Override
	public void keyUp(int code) {
		super.keyUp(code);
		if(code=='c')
			mPolygon.clear();
		if(code=='t')
			mDrawTexture ^= true;
		if(code=='d')
			mDrawDebug ^= true;
	}

	@Override
	public void start() {
		mPickedPoint = -1;
	}

}
