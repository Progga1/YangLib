package yang.graphics.util.cameracontrol;

import yang.events.eventtypes.SurfacePointerEvent;
import yang.graphics.camera.Camera3D;
import yang.math.MathConst;
import yang.math.objects.Point3f;
import yang.math.objects.Vector3f;
import yang.surface.YangSurface;


public class Camera3DControl extends CameraControl {

	final float MAX_BETA = MathConst.PI/2-0.01f;

	//Settings
	public boolean 	mInvertView = false;
	public boolean mOrthogonalProjection = true;
	public float mViewDelay = 0.3f;
	public char mSwitchPerspectiveKey = 'p';
	public char mInvertViewKey = 'v';

	//State
	public Vector3f mTarViewValues = new Vector3f();
	public Vector3f mViewValues = new Vector3f();
	public Point3f mFocus = new Point3f();
	public float mVelAlpha = 0,mVelBeta = 0,mVelRoll = 0, mVelZoom = 0;

	//Objects
	protected Camera3D mCamera;

	//Temp
	private final Vector3f mCamRight = new Vector3f();
	private final Vector3f mCamUp = new Vector3f();

	public Camera3DControl() {
		super();
		mCamera = new Camera3D();
	}

	public Camera3DControl(YangSurface surface) {
		super(surface);
		mCamera = new Camera3D();
	}

	@Override
	public void step(float deltaTime) {
		super.step(deltaTime);
		if(mCurPointerDownCount==0) {
			mTarViewValues.mX += mVelAlpha*deltaTime;
			mTarViewValues.mY += mVelBeta*deltaTime;
			mTarViewValues.mZ += mVelRoll*deltaTime;
			mTargetZoom += mVelZoom*deltaTime;
		}
		mViewValues.setDelayed(mTarViewValues,mViewDelay);
	}

	@Override
	public Camera3D getUpdatedCameraInstance() {
		if(mOrthogonalProjection)
			mCamera.setOrthogonalProjection(-2, 20, mZoom);
		else
			mCamera.setPerspectiveProjection(0.6f,100);
		if(mInvertView)
			mCamera.setLookOutwardsAlphaBeta(mViewValues.mX+MathConst.PI,-mViewValues.mY, mZoom, mFocus);
		else{
			mCamera.setLookAtAlphaBeta(mViewValues.mX,mViewValues.mY, mZoom, mFocus);
		}
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
		mFocus.set(x,y,z);
	}

	public void shiftFocus(float dx, float dy, float dz) {
		mFocus.add(dx,dy,dz);
	}

	public void setViewAngle(float alpha,float beta) {
		mTarViewValues.mX = alpha;
		mViewValues.mX = alpha;
		mTarViewValues.mY = beta;
		mViewValues.mY = beta;
	}

	public void setViewAngle(float alpha,float beta,float roll) {
		setViewAngle(alpha,beta);
		mTarViewValues.mZ = roll;
		mViewValues.mZ = roll;
	}

	@Override
	public void snap() {
		super.snap();
		mViewValues.set(mViewValues);
	}

	@Override
	protected void onShift(float deltaX, float deltaY) {
		mCamera.getRightVector(mCamRight);
		mCamera.getUpVector(mCamUp);
		shiftFocus(mCamRight.mX*deltaX+mCamUp.mX*deltaY, mCamRight.mY*deltaX+mCamUp.mY*deltaY, mCamRight.mZ*deltaX+mCamUp.mZ*deltaY);
	}

	@Override
	protected void onDrag(SurfacePointerEvent event) {
		mTarViewValues.mX -= event.mDeltaX*2;
		mTarViewValues.mY -= event.mDeltaY;
		if(mTarViewValues.mY<-MAX_BETA)
			mTarViewValues.mY = -MAX_BETA;
		if(mTarViewValues.mY>MAX_BETA)
			mTarViewValues.mY = MAX_BETA;
	}

	@Override
	public void keyDown(int code) {
		super.keyDown(code);
		if(code==mSwitchPerspectiveKey)
			mOrthogonalProjection ^= true;
		if(code==mInvertViewKey)
			mInvertView ^= true;

	}

	public void setAlpha(float alpha) {
		mTarViewValues.mX = alpha;
	}

	public void setBeta(float beta) {
		mTarViewValues.mY = beta;
	}

	public void setRoll(float roll) {
		mTarViewValues.mZ = roll;
	}

	@Override
	public String toString() {
		return "yaw = "+mViewValues.mX+" pitch = "+mViewValues.mY+" roll = "+mViewValues.mZ+" zoom = "+mZoom+" focus = "+mFocus+"";
	}

	public void set(Camera3DControl template) {

	}

	public float getViewAlpha() {
		return mViewValues.mX;
	}

	public float getViewBeta() {
		return mViewValues.mY;
	}

	public float getViewRoll() {
		return mViewValues.mZ;
	}

}
