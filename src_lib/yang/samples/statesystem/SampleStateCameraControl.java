package yang.samples.statesystem;

import yang.events.eventtypes.SurfacePointerEvent;
import yang.graphics.util.cameracontrol.Camera3DControl;
import yang.math.objects.Vector3f;

public abstract class SampleStateCameraControl extends SampleState {

	protected Vector3f mCamRight = new Vector3f();
	protected Vector3f mCamUp = new Vector3f();
	protected Camera3DControl mCamera;
	protected char mSwitchPerspectiveKey = 'p';
	protected char mInvertViewKey = 'v';

	@Override
	protected void initGraphics() {
		mCamera = new Camera3DControl(mStateSystem);
	}


	@Override
	protected void step(float deltaTime) {
		mCamera.step(deltaTime);
	}

	protected void setCamera() {
		mGraphics3D.setCamera(mCamera.getUpdatedCameraInstance());
	}

	@Override
	public void pointerDown(float x,float y,SurfacePointerEvent event) {
		mCamera.pointerDown(x,y,event);
	}

	@Override
	public void pointerDragged(float x,float y,SurfacePointerEvent event) {
		mCamera.pointerDragged(x,y,event);
	}

	@Override
	public void pointerUp(float x,float y,SurfacePointerEvent event) {
		mCamera.pointerUp(x,y,event);
	}

	@Override
	public void zoom(float value) {
		mCamera.zoom(value);
	}

	@Override
	public void keyDown(int code) {
		super.keyDown(code);
		mCamera.keyDown(code);
		if(code==mSwitchPerspectiveKey)
			mCamera.mOrthogonalProjection ^= true;
		if(code==mInvertViewKey)
			mCamera.mInvertView ^= true;
	}

	@Override
	public void keyUp(int code) {
		super.keyUp(code);
		mCamera.keyUp(code);
	}
}
