package yang.samples.statesystem.states;

import yang.events.eventtypes.SurfacePointerEvent;
import yang.model.DebugYang;
import yang.samples.statesystem.SampleState;
import yang.util.trajectory.YangIterativeTrajectory;
import yang.util.trajectory.YangSimpleTrajectory;
import yang.util.trajectory.YangTrajectory;

public class TrajectorySampleState extends SampleState {

	private YangTrajectory mTrajectory;
	private YangSimpleTrajectory mSimpleTrajectory;
	private YangIterativeTrajectory mIterTrajectory;
	private float mDeltaTimeSteps = 0.01f;

	public TrajectorySampleState() {
		mSimpleTrajectory = new YangSimpleTrajectory();
		mIterTrajectory = new YangIterativeTrajectory();
		mTrajectory = mSimpleTrajectory;
		mTrajectory.calculate(0.5f,0.5f);
	}

	@Override
	protected void step(float deltaTime) {

	}

	@Override
	protected void draw() {
		mGraphics2D.activate();
		mGraphics2D.switchGameCoordinates(false);
		mGraphics.clear(0,0,0.06f);
		DebugYang.clearState();

		float lstX = 0;
		float lstY = 0;
		float velX = mTrajectory.getResultVelX();
		float velY = mTrajectory.getResultVelY();
		float t = 0;
		float dt = mDeltaTimeSteps;
		mGraphics.bindTexture(null);

		while(t<=mTrajectory.getResultTime()*2+0.01f) {
			if(t>0) {
				float x = lstX + dt*velX;
				float y = lstY + dt*velY;
				mGraphics2D.setColor(0.6f,0.6f,0.9f);
				mGraphics2D.drawLine(lstX,lstY, x,y, 0.015f);
				mGraphics2D.setColor(0.9f,0.8f,0.2f);
				mGraphics2D.drawRectCentered(x,y, 0.01f);
				lstX = x;
				lstY = y;
				velY += mTrajectory.mGravity*dt;
			}
			t += mDeltaTimeSteps;
		}

		DebugYang.appendStateLn(mTrajectory.getResultTime());
	}


	@Override
	public void pointerDragged(float x,float y,SurfacePointerEvent event) {
		mTrajectory.calculate(x,y);
	}

	@Override
	public void zoom(float value) {
		mDeltaTimeSteps += value*0.01f;
		if(mDeltaTimeSteps<0.001f)
			mDeltaTimeSteps = 0.001f;
		if(mDeltaTimeSteps>0.5f)
			mDeltaTimeSteps = 0.5f;
	}

	@Override
	public void keyDown(int code) {
		if(code=='t') {
			if(mTrajectory==mSimpleTrajectory)
				mTrajectory = mIterTrajectory;
			else
				mTrajectory = mSimpleTrajectory;
		}
	}

}
