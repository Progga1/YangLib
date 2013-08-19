package yang.samples.statesystem;

import yang.events.eventtypes.YangPointerEvent;
import yang.graphics.skeletons.JointEditData;
import yang.graphics.skeletons.elements.Joint;
import yang.math.objects.Vector3f;

public abstract class SampleStateCameraControl extends SampleState {

	protected float mViewAlpha,mViewBeta;
	protected float mZoom = 1.0f;
	protected Vector3f mCamRight = new Vector3f();
	protected Vector3f mCamUp = new Vector3f();
	protected float mPntX,mPntY;
	protected float mPntDeltaX,mPntDeltaY;
	
	@Override
	public void pointerDown(float x,float y,YangPointerEvent event) {
		mPntX = x;
		mPntY = y;
	}
	
	@Override
	public void pointerDragged(float x,float y,YangPointerEvent event) {
		mPntDeltaX = x-mPntX;
		mPntDeltaY = y-mPntY;
		mPntX = x;
		mPntY = y;
		if(event.mButton == YangPointerEvent.BUTTON_MIDDLE) {
			mViewAlpha -= mPntDeltaX*2;
			mViewBeta -= mPntDeltaY;
			final float MAX_BETA = PI/2-0.01f;
			if(mViewBeta<-MAX_BETA)
				mViewBeta = -MAX_BETA;
			if(mViewBeta>MAX_BETA)
				mViewBeta = MAX_BETA;
		}

	}
	
	@Override
	public void pointerUp(float x,float y,YangPointerEvent event) {
		
	}
	
}
