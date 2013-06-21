package yang.samples.statesystem.states;

import yang.events.eventtypes.YangPointerEvent;
import yang.graphics.defaults.meshcreators.outlinedrawer.OrthoStrokeCreator;
import yang.graphics.defaults.meshcreators.outlinedrawer.OrthoStrokeDefaultProperties;
import yang.graphics.defaults.meshcreators.outlinedrawer.OrthoStrokeProperties;
import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.translator.Texture;
import yang.math.MathFunc;
import yang.samples.statesystem.SampleState;

public class StrokeDrawerState extends SampleState {

	private OrthoStrokeCreator mStroke;
	private OrthoStrokeDefaultProperties mStrokeProperties;
	private Texture mStrokeTex;
	private Texture mCircleTex;
	private float mLstX,mLstY;
	
	@Override
	protected void initGraphics() {
		mStrokeTex = mGFXLoader.getImage("stroke",TextureFilter.NEAREST);
		mCircleTex = mGFXLoader.getImage("circle",TextureFilter.LINEAR_MIP_LINEAR);
		mStrokeProperties = new OrthoStrokeDefaultProperties();
		mStrokeProperties.init();
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
		
		mGraphics.switchCulling(true);
		mGraphics.bindTexture(mStrokeTex);
		mGraphics2D.setColor(0.8f,0.8f,0.9f);
		//mStroke.drawDebugOutput(0.01f);
		
		mStroke.drawCompletely();
		mGraphics2D.fillBuffers();
		
		if(mLstX!=Float.MIN_VALUE) {
			mGraphics2D.setColor(0.9f,0.2f,0.2f,0.8f);
			mGraphics.bindTexture(mCircleTex);
			mGraphics2D.drawRectCentered(mLstX,mLstY,0.1f);
		}
	}
	
	@Override
	public void pointerDown(float x,float y,YangPointerEvent event) {
		x = (int)((x+mStrokeProperties.mWidth*0.5f*MathFunc.sign(x))/mStrokeProperties.mWidth)*mStrokeProperties.mWidth;
		y = (int)((y+mStrokeProperties.mWidth*0.5f*MathFunc.sign(y))/mStrokeProperties.mWidth)*mStrokeProperties.mWidth;
		if(mLstX==Float.MIN_VALUE || event.mButton==YangPointerEvent.BUTTON_RIGHT) {
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
