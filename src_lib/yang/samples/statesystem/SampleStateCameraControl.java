package yang.samples.statesystem;

import yang.events.eventtypes.YangPointerEvent;
import yang.graphics.util.Camera3D;
import yang.math.objects.Vector3f;

public abstract class SampleStateCameraControl extends SampleState {

	protected boolean mOrthogonalProjection = true;
	protected float mViewAlpha,mViewBeta;
	protected float mZoom = 1.0f;
	protected Vector3f mCamRight = new Vector3f();
	protected Vector3f mCamUp = new Vector3f();
	protected float mPntX,mPntY;
	protected float mPntDeltaX,mPntDeltaY;
	protected float mCamX,mCamY,mCamZ;
	protected Camera3D mCamera = new Camera3D();
	
	protected void refreshCamera() {
		if(mOrthogonalProjection)
			mGraphics3D.setOrthogonalProjection(-1, 100, mZoom);
		else
			mGraphics3D.setPerspectiveProjection(100);
		mCamera.setAlphaBeta(mViewAlpha,mViewBeta, mZoom, mCamX,mCamY,mCamZ);
	}
	
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
		refreshCamera();
	}
	
	@Override
	public void pointerUp(float x,float y,YangPointerEvent event) {
		
	}
	
	@Override
	public void zoom(float value) {
		mZoom += value;
		if(mZoom<0.3f)
			mZoom = 0.3f;
		if(mZoom>10f)
			mZoom = 10f;
		refreshCamera();
	}
	
}
