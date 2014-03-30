package yang.samples.statesystem;

import yang.events.eventtypes.SurfacePointerEvent;
import yang.graphics.util.Camera3DControllable;
import yang.math.objects.Vector3f;

public abstract class SampleStateCameraControl extends SampleState {

	protected boolean mOrthogonalProjection = true;
	protected Vector3f mCamRight = new Vector3f();
	protected Vector3f mCamUp = new Vector3f();
	protected Camera3DControllable mCamera;
	protected char mSwitchPerspectiveKey = 'p';
	protected char mInvertViewKey = 'v';

	@Override
	protected void initGraphics() {
		mCamera = new Camera3DControllable(mStateSystem);
	}

	protected void refreshCamera() {
		if(mOrthogonalProjection)
			mGraphics3D.setOrthogonalProjection(-2, 20, mCamera.mZoom);
		else
			mGraphics3D.setPerspectiveProjection(100);
	}

	@Override
	protected void step(float deltaTime) {
		mCamera.step();
	}

	protected void setCamera() {
		refreshCamera();
		mGraphics3D.setCamera(mCamera.getUpdatedCamera());
	}

	@Override
	public void pointerDown(float x,float y,SurfacePointerEvent event) {
		mCamera.pointerDown(event);
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
			mOrthogonalProjection ^= true;
		if(code==mInvertViewKey)
			mCamera.mInvertView ^= true;
	}

	@Override
	public void keyUp(int code) {
		super.keyUp(code);
		mCamera.keyUp(code);
	}
}
