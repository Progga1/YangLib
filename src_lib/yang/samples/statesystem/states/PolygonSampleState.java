package yang.samples.statesystem.states;

import yang.events.eventtypes.PointerEvent;
import yang.graphics.defaults.meshcreators.PolygonCreator;
import yang.samples.statesystem.SampleState;
import yang.util.statesystem.YangProgramState;
import yang.util.statesystem.YangProgramStateSystem;

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
		mPolygon.putTriangulatedPositions();
		mPolygon.finish();
	}

	@Override
	public void pointerDown(float x,float y,PointerEvent event) {
		mPolygon.addPoint(x, y);
	}
	
}
