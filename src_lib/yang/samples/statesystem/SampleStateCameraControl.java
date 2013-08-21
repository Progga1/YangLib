package yang.samples.statesystem;

import yang.events.eventtypes.YangPointerEvent;
import yang.graphics.util.Camera3D;
import yang.graphics.util.Camera3DControllable;
import yang.math.objects.Vector3f;

public abstract class SampleStateCameraControl extends SampleState {

	protected boolean mOrthogonalProjection = true;
	protected Vector3f mCamRight = new Vector3f();
	protected Vector3f mCamUp = new Vector3f();
	protected Camera3DControllable mCamera = new Camera3DControllable();
	
	protected void refreshCamera() {
		if(mOrthogonalProjection)
			mGraphics3D.setOrthogonalProjection(-2, 100, mCamera.mZoom);
		else
			mGraphics3D.setPerspectiveProjection(100);
	}
	
	@Override
	protected void step(float deltaTime) {
		mCamera.step();
	}
	
	protected void setCamera() {
		mGraphics3D.setCamera(mCamera.getUpdatedCamera());
	}
	
	@Override
	public void pointerDown(float x,float y,YangPointerEvent event) {
		mCamera.pointerDown(event);
	}
	
	@Override
	public void pointerDragged(float x,float y,YangPointerEvent event) {
		mCamera.pointerDragged(event);
		refreshCamera();
	}
	
	@Override
	public void pointerUp(float x,float y,YangPointerEvent event) {
		mCamera.pointerUp(event);
	}
	
	@Override
	public void zoom(float value) {
		mCamera.zoom(value);
		refreshCamera();
	}
	
	@Override
	public void keyDown(int code) {
		super.keyDown(code);
		mCamera.keyDown(code);
	}
	
	@Override
	public void keyUp(int code) {
		super.keyUp(code);
		mCamera.keyUp(code);
	}
}
