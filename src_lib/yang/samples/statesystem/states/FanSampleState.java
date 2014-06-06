package yang.samples.statesystem.states;

import yang.events.eventtypes.SurfacePointerEvent;
import yang.graphics.defaults.geometrycreators.FanCreator;
import yang.graphics.model.FloatColor;
import yang.math.MathConst;
import yang.math.MathFunc;
import yang.samples.statesystem.SampleState;

public class FanSampleState extends SampleState {

	private static final FloatColor[] COLORS = {new FloatColor(1,0,0),new FloatColor(1,1,0),new FloatColor(0,1,0),new FloatColor(0,1,1),new FloatColor(0,0,1)};
	
	private static final float SX = -0.5f;
	private static final float SY = 0;
	private static final float SR = 0.25f;
	private static final int   SSEGS = 10;	
	private static final float SANGLE = MathConst.PI2*3/4;

	private static final float MX = 0.5f;
	private static final float MY = 0;
	private static       float MR = 0.25f;
	private static int   MSEGS = 10;	
	private static 			   float MANGLE = MathConst.PI2*3/4;	
	private static final int   MLAYER = 20;
	

	private float mCurX = Float.MAX_VALUE,mCurY;
	
	
	private FanCreator mSimple;
	private FanCreator mMulti;

	@Override
	protected void initGraphics() {
		mSimple = new FanCreator(mGraphics2D, 1);
		mMulti  = new FanCreator(mGraphics2D, MLAYER);
	}

	@Override
	public void step(float deltaTime) {
		
	}

	@Override
	public void draw() {
		mGraphics.clear(0, 0, 0.1f);
//		mGraphics2D.activate();
//		mGraphics2D.setWhite();
//		mGraphics2D.setColorFactor(1);
//		mGraphics2D.switchGameCoordinates(false);
//		mGraphics.switchCulling(true);

		mGraphics.bindTexture(null);
		
		//draw simple Fan -----------------------------------------------------
		
		mSimple.startFan(SX, SY);
		mGraphics2D.putColor(FloatColor.WHITE);
		mGraphics2D.putTextureCoord(0, 0); //dummy
		
		for(int i=0; i<=SSEGS; i++) {
			
			float angle = (SANGLE / (SSEGS)) * i;
			float x = MathFunc.cos(angle)*SR;
			float y = MathFunc.sin(angle)*SR;
			mSimple.continueFan(SX + x, SY + y);
			mGraphics2D.putColor(COLORS[i%COLORS.length]);
			mGraphics2D.putTextureCoord(0, 0);
		}
		
		//draw Multi Fan -----------------------------------------------------
		
		mMulti.startFan(MX, MY);
		mGraphics2D.putColor(FloatColor.WHITE);
		mGraphics2D.putTextureCoord(0, 0); //dummy
		
		MANGLE = (float)Math.atan2(mCurY-MY, mCurX-MX);		
		if(MANGLE<0)MANGLE+=MathConst.PI2;		
		MR = MathFunc.sqrt((mCurX-MX)*(mCurX-MX) +(mCurY-MY)*(mCurY-MY));		
		MSEGS = (int)Math.ceil(MANGLE * MR*8);
		
		
		for(int i=0; i<=MSEGS; i++) {
			
			for(int l = 0; l<MLAYER; l++){
			
				float ratio = ((float)(l+1)) / MLAYER;
				
				float angle = (MANGLE / (MSEGS)) * i;
				float x = MathFunc.cos(angle)*MR*ratio;
				float y = MathFunc.sin(angle)*MR*ratio;
				
				mMulti.continueFan(MX + x, MY + y);
				mGraphics2D.putColor(COLORS[l%COLORS.length]);
				mGraphics2D.putTextureCoord(0, 0);
			}
		}
		
	}

	@Override
	public void pointerDragged(float x,float y,SurfacePointerEvent event) {
		//mTail.refreshFront(x, y);
		mCurX = x;
		mCurY = y;
	}

	@Override
	public void pointerUp(float x,float y,SurfacePointerEvent event) {
	}

	@Override
	public void keyUp(int code) {
		
	}

}
