package yang.samples.statesystem.states;

import yang.events.eventtypes.SurfacePointerEvent;
import yang.samples.statesystem.SampleState;
import yang.util.trajectory.YangAngleTrajectory;
import yang.util.trajectory.YangTrajectory;

public class TrajectorySampleState extends SampleState {

	private YangTrajectory mTrajectory;
	private float mDeltaTimeSteps = 0.01f;
	private float mTargetX = 0.5f,mTargetY = 0.5f;

	public TrajectorySampleState() {
		mTrajectory = new YangAngleTrajectory();
		mTrajectory.calculate(mTargetX,mTargetY);
		mTrajectory.mMaxVel = 4;
	}

	@Override
	protected void step(float deltaTime) {

	}

	@Override
	protected void draw() {
		mGraphics2D.activate();
		mGraphics2D.switchGameCoordinates(false);
		mGraphics.clear(0,0,0.06f);

		float lstX = 0;
		float lstY = 0;
		float velX = mTrajectory.getResultVelX();
		float velY = mTrajectory.getResultVelY();
		float t = 0;
		float dt = mDeltaTimeSteps;
		mGraphics.bindTexture(null);

		float alpha = mTrajectory.isReachable()?1:0.5f;
		while(t<=mTrajectory.getResultTime()*2+0.01f) {
			if(t>0) {
				float x = lstX + dt*velX;
				float y = lstY + dt*velY;
				mGraphics2D.setColor(0.6f,0.6f,0.9f,alpha);
				mGraphics2D.drawLine(lstX,lstY, x,y, 0.015f);
				mGraphics2D.setColor(0.9f,0.8f,0.2f,alpha);
				mGraphics2D.drawRectCentered(x,y, 0.01f);
				lstX = x;
				lstY = y;
				velY += mTrajectory.mGravity*dt;
			}
			t += mDeltaTimeSteps;
		}

		mGraphics2D.setColor(1,0.1f,0);
		mGraphics2D.drawRectCentered(mTargetX,mTargetY, 0.015f);
	}


	@Override
	public void pointerDragged(float x,float y,SurfacePointerEvent event) {
		mTargetX = x;
		mTargetY = y;
		mTrajectory.calculate(mTargetX,mTargetY);
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
//		if(code=='t') {
//			if(mTrajectory==mSimpleTrajectory)
//				mTrajectory = mIterTrajectory;
//			else
//				mTrajectory = mSimpleTrajectory;
//		}
	}

}
