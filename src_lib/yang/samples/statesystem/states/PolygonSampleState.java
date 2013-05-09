package yang.samples.statesystem.states;

import yang.events.eventtypes.YangPointerEvent;
import yang.graphics.FloatColor;
import yang.graphics.defaults.meshcreators.PolygonCreator;
import yang.samples.statesystem.SampleState;

public class PolygonSampleState extends SampleState {

	private PolygonCreator mPolygon;
	
	@Override
	public void initGraphics() {
		mPolygon = new PolygonCreator(mGraphics2D,128);
	}
	
	@Override
	protected void step(float deltaTime) {
		
	}
	
	@Override
	protected void draw() {
		mGraphics.clear(0, 0, 0.1f);
		mGraphics.bindTexture(null);
		mGraphics2D.setWhite();
		
		mGraphics.switchCulling(false);
		mPolygon.putVertices(mGraphics2D);
		mGraphics2D.fillBuffers();
		
		mGraphics.bindTexture(mStateSystem.mCircleTexture);
		mGraphics2D.setColor(FloatColor.RED);
		for(int i=0;i<mPolygon.getPointCount();i++) {
			mGraphics2D.drawRectCentered(mPolygon.getPosX(i), mPolygon.getPosY(i), 0.016f);
		}
		
	}

	@Override
	public void pointerDown(float x,float y,YangPointerEvent event) {
		mPolygon.addPoint(x, y);
		mPolygon.triangulate();
	}
	
	@Override
	public void keyUp(int code) {
		if(code=='c')
			mPolygon.clear();
	}
	
}
