package yang.graphics.util.cameracontrol;

import yang.events.eventtypes.SurfacePointerEvent;
import yang.graphics.camera.Camera3D;
import yang.math.MathConst;
import yang.math.objects.EulerAngles;
import yang.math.objects.Point3f;
import yang.math.objects.Vector3f;
import yang.surface.YangSurface;


public class Camera3DControl extends CameraControl {

	final float MAX_BETA = MathConst.PI/2-0.01f;

	//Settings
	public boolean 	mInvertView = false;
	public boolean mOrthogonalProjection = true;
	public float mAngleDelay = 0.4f;
	public char mSwitchPerspectiveKey = 'p';
	public char mInvertViewKey = 'v';

	//State
	public EulerAngles mTarViewValues = new EulerAngles();
	public EulerAngles mViewValues = new EulerAngles();
	public Point3f mFocus = new Point3f();
	public float mVelAlpha = 0, mVelBeta = 0, mVelRoll = 0, mVelZoom = 0;

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
			mTarViewValues.mYaw += mVelAlpha*deltaTime;
			mTarViewValues.mPitch += mVelBeta*deltaTime;
			mTarViewValues.mRoll += mVelRoll*deltaTime;
			mTargetZoom += mVelZoom*deltaTime;
		}
		mViewValues.setDelayed(mTarViewValues,mAngleDelay);
	}

	@Override
	public Camera3D getUpdatedCameraInstance() {
		if(mOrthogonalProjection)
			mCamera.setOrthogonalProjection(-2, 20, mZoom);
		else
			mCamera.setPerspectiveProjection(0.6f,100);
		if(mInvertView)
			mCamera.setLookOutwardsAlphaBeta(mViewValues, mZoom, mFocus);
		else{
			mCamera.setLookAtAlphaBeta(mViewValues, mZoom, mFocus);
		}
		return mCamera;
	}

	public void set(Camera3DControl template) {
		mTarViewValues.set(template.mTarViewValues);
		mViewValues.set(template.mViewValues);
		mFocus.set(template.mFocus);
		mOrthogonalProjection = template.mOrthogonalProjection;
		mAngleDelay = template.mAngleDelay;
		mInvertView = template.mInvertView;
		mInvertViewKey = template.mInvertViewKey;
		mSwitchPerspectiveKey = template.mSwitchPerspectiveKey;
		mVelAlpha = template.mVelAlpha;
		mVelBeta = template.mVelBeta;
		mVelRoll = template.mVelRoll;
		mVelZoom = template.mVelZoom;
		mTargetZoom = template.mTargetZoom;
		mZoom = template.mZoom;
		mZoomDelay = template.mZoomDelay;
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
		mTarViewValues.mYaw = alpha;
		mViewValues.mYaw = alpha;
		mTarViewValues.mPitch = beta;
		mViewValues.mPitch = beta;
	}

	public void setViewAngle(float alpha,float beta,float roll) {
		setViewAngle(alpha,beta);
		mTarViewValues.mRoll = roll;
		mViewValues.mRoll = roll;
	}

	@Override
	public void snap() {
		super.snap();
		mViewValues.set(mTarViewValues);
	}

	@Override
	protected void onShift(float deltaX, float deltaY) {
		mCamera.getRightVector(mCamRight);
		mCamera.getUpVector(mCamUp);
		shiftFocus(mCamRight.mX*deltaX+mCamUp.mX*deltaY, mCamRight.mY*deltaX+mCamUp.mY*deltaY, mCamRight.mZ*deltaX+mCamUp.mZ*deltaY);
	}

	@Override
	protected void onDrag(SurfacePointerEvent event) {
		mTarViewValues.mYaw -= event.mDeltaX*2;
		mTarViewValues.mPitch -= event.mDeltaY;
		if(mTarViewValues.mPitch<-MAX_BETA)
			mTarViewValues.mPitch = -MAX_BETA;
		if(mTarViewValues.mPitch>MAX_BETA)
			mTarViewValues.mPitch = MAX_BETA;
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
		mTarViewValues.mYaw = alpha;
	}

	public void setBeta(float beta) {
		mTarViewValues.mPitch = beta;
	}

	public void setRoll(float roll) {
		mTarViewValues.mRoll = roll;
	}

	@Override
	public String toString() {
		return mViewValues+", zoom="+mZoom+", focus="+mFocus;
	}

	public float getViewAlpha() {
		return mViewValues.mYaw;
	}

	public float getViewBeta() {
		return mViewValues.mPitch;
	}

	public float getViewRoll() {
		return mViewValues.mRoll;
	}

}
