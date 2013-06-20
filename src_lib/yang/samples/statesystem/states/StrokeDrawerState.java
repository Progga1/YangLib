package yang.samples.statesystem.states;

import yang.events.eventtypes.YangPointerEvent;
import yang.graphics.defaults.meshcreators.outlinedrawer.OrthoStrokeCreator;
import yang.graphics.defaults.meshcreators.outlinedrawer.OrthoStrokeProperties;
import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.translator.Texture;
import yang.samples.statesystem.SampleState;

public class StrokeDrawerState extends SampleState {

	private OrthoStrokeCreator mStroke;
	private OrthoStrokeProperties mStrokeProperties;
	private Texture mOutlineTex;
	private float mLstX,mLstY;
	
	@Override
	protected void initGraphics() {
		mOutlineTex = mGFXLoader.getImage("stroke",TextureFilter.LINEAR_MIP_LINEAR);
		mStrokeProperties = new OrthoStrokeProperties();
		mStroke = new OrthoStrokeCreator(mGraphics2D,256,mStrokeProperties);
		reset();
	}
	
	private void reset() {
		mLstX = Float.MIN_VALUE;
		mLstY = 0;
	}
	
	@Override
	protected void step(float deltaTime) {
		
	}
	
	@Override
	protected void draw() {
		mGraphics2D.activate();
		mGraphics.clear(0,0,0);
		
		mGraphics.switchCulling(false);
		mGraphics.bindTexture(null);
		mGraphics2D.setColor(0.5f,0.5f,0.9f);
		//mStroke.drawDebugOutput(0.01f);
		
		mStroke.draw();
		mGraphics2D.fillBuffers();
	}
	
	@Override
	public void pointerDown(float x,float y,YangPointerEvent event) {
		x = (int)(x/mStrokeProperties.mWidth)*mStrokeProperties.mWidth;
		y = (int)(y/mStrokeProperties.mWidth)*mStrokeProperties.mWidth;
		if(mLstX==Float.MIN_VALUE){
			mStroke.startStroke(x, y);
			mLstX = x;
			mLstY = y;
		}else{
			float deltaX = x-mLstX;
			float deltaY = y-mLstY;
			if(Math.abs(deltaX)>Math.abs(deltaY)) {
				mStroke.marchX(deltaX);
				mLstX = x;
			}else{
				mStroke.marchY(deltaY);
				mLstY = y;
			}
		}
		
	}
	
}
