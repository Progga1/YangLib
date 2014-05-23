package yang.graphics.util.cameracontrol;

import yang.events.eventtypes.SurfacePointerEvent;
import yang.graphics.camera.Camera3D;
import yang.math.MathConst;
import yang.math.objects.Vector3f;
import yang.surface.YangSurface;


public class Camera3DControl extends CameraControl {

	final float MAX_BETA = MathConst.PI/2-0.01f;

	//Settings
	public boolean 	mInvertView = false;
	public boolean mOrthogonalProjection = true;
	public float mViewDelay = 0.3f;

	//State
	public float mTargetViewAlpha = 0,mTargetViewBeta = 0;
	public float mViewAlpha,mViewBeta;
	public float mFocusX,mFocusY,mFocusZ;

	//Objects
	protected Camera3D mCamera;

	//Temp
	private final Vector3f mCamRight = new Vector3f();
	private final Vector3f mCamUp = new Vector3f();

	public Camera3DControl(YangSurface surface) {
		super(surface);
		mCamera = new Camera3D();
	}

	public void pointerDown(SurfacePointerEvent event) {
		mCurPointerDownCount++;
		if(event.mId!=0)
			return;
	}

	@Override
	public void step() {
		super.step();
		mViewAlpha += (mTargetViewAlpha-mViewAlpha)*mViewDelay;
		mViewBeta += (mTargetViewBeta-mViewBeta)*mViewDelay;
	}

	@Override
	public Camera3D getUpdatedCameraInstance() {
		if(mOrthogonalProjection)
			mCamera.setOrthogonalProjection(-2, 20, mZoom);
		else
			mCamera.setPerspectiveProjection(0.6f,100);
		if(mInvertView)
			mCamera.setLookOutwardsAlphaBeta(mViewAlpha+MathConst.PI,-mViewBeta, mZoom, mFocusX,mFocusY,mFocusZ);
		else
			mCamera.setLookAtAlphaBeta(mViewAlpha,mViewBeta, mZoom, mFocusX,mFocusY,mFocusZ);
		return mCamera;
	}

	@Override
	public Camera3D getCameraInstance() {
		return mCamera;
	}

	@Override
	public void setZoom(float zoom) {
		if(!mInvertView)
			super.setZoom(zoom);
	}

	public void setFocus(float x,float y,float z) {
		mFocusX = x;
		mFocusY = y;
		mFocusZ = z;
	}

	public void shiftFocus(float dx, float dy, float dz) {
		mFocusX += dx;
		mFocusY += dy;
		mFocusZ += dz;
	}

	public void setViewAngle(float alpha,float beta) {
		mViewAlpha = alpha;
		mTargetViewAlpha = alpha;
		mViewBeta = beta;
		mTargetViewBeta = beta;
	}

	@Override
	public void snap() {
		super.snap();
		mViewAlpha = mTargetViewAlpha;
		mViewBeta = mTargetViewBeta;
	}

	@Override
	protected void onShift(float deltaX, float deltaY) {
		mCamera.getRightVector(mCamRight);
		mCamera.getUpVector(mCamUp);
		shiftFocus(mCamRight.mX*deltaX+mCamUp.mX*deltaY, mCamRight.mY*deltaX+mCamUp.mY*deltaY, mCamRight.mZ*deltaX+mCamUp.mZ*deltaY);
	}

	@Override
	protected void onDrag(SurfacePointerEvent event) {
		mTargetViewAlpha -= event.mDeltaX*2;
		mTargetViewBeta -= event.mDeltaY;
		if(mTargetViewBeta<-MAX_BETA)
			mTargetViewBeta = -MAX_BETA;
		if(mTargetViewBeta>MAX_BETA)
			mTargetViewBeta = MAX_BETA;
	}

}
