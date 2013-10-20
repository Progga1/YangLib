package yang.samples.statesystem.states;

import yang.events.eventtypes.SurfacePointerEvent;
import yang.graphics.model.FloatColor;
import yang.graphics.translator.Texture;
import yang.graphics.translator.glconsts.GLMasks;
import yang.math.objects.Point3f;
import yang.math.objects.Quaternion;
import yang.math.objects.Vector3f;
import yang.math.objects.matrix.YangMatrix;
import yang.samples.statesystem.SampleStateCameraControl;

public class Rotations3DSampleState extends SampleStateCameraControl {

	private final YangMatrix mEulerMatrix = new YangMatrix();
	private final YangMatrix mTrafo = new YangMatrix();
	private final Point3f mPosition1 = new Point3f(1.5f,1.2f,1.3f);
	private final Vector3f mRotVector1 = new Vector3f();
	private final Vector3f mRotVectorAround = new Vector3f();
	private final Point3f mPosition2 = new Point3f(-1.0f,1.0f,-0.9f);
	private final Vector3f mRotVector2 = new Vector3f();
	private final Point3f mPosition3 = new Point3f(1,1,-0.5f);
	private final Vector3f mRotVector3 = new Vector3f();
	private final Vector3f mQuatVector1 = new Vector3f();
	private final Quaternion mQuaternion1 = new Quaternion();
	private final Quaternion mQuaternion2 = new Quaternion();

	private final Point3f mFromToPosition = new Point3f(-1,1,1.2f);
	private final Vector3f mToVector1 = new Vector3f();
	private final Vector3f mToVector2 = new Vector3f();

	private final Vector3f mRotatingVec = new Vector3f();
	private final Quaternion mTempQuaternion1 = new Quaternion();
	private final Quaternion mTempQuaternion2 = new Quaternion();

	private float mQuatAngle = 0;

	private Texture mCubeTex;

	@Override
	public void initGraphics() {
		mRotVector1.setNormalized(1, 0.5f, 0.8f);
		mRotVectorAround.setNormalized(1, 1, 0.1f);
		mRotVector2.setNormalized(1, -1, 0.5f);
		mRotVector3.setNormalized(0.5f, 0.5f, -1);
		mToVector1.setNormalized(-1, -0.2f, 0.5f);
		mToVector2.setNormalized(1, 1, 0);
//		mToVector1.set(Vector3f.UP);
//		mToVector2.set(Vector3f.DOWN);
		mQuatVector1.set(mRotVector1);
		mCamera.mMoveCameraAlternativeButton = SurfacePointerEvent.BUTTON_LEFT;
		mCamera.setZoom(3);
		mCubeTex = mGFXLoader.getImage("cube");
		refreshCamera();
	}

	@Override
	protected void step(float deltaTime) {
		super.step(deltaTime);
	}

	@Override
	protected void draw() {
		final float time = (float)mStateTimer;
		mGraphics.bindTexture(null);
		mGraphics3D.setWhite();
		mGraphics3D.activate();
		mGraphics.clear(0,0,0.1f,GLMasks.DEPTH_BUFFER_BIT);

		mGraphics.switchZBuffer(true);
		mGraphics.switchCulling(false);
		super.setCamera();

		mQuaternion1.setFromAxis(mQuatVector1, time*0.5f);

		//mRotVector2.setNormalized((float)Math.sin(time*0.1f), (float)Math.cos(time*0.1f), 0);

		mTempQuaternion1.setFromAxis(mRotVectorAround, time*0.6f);
		mRotatingVec.applyQuaternion(mTempQuaternion1,mRotVector2);
		mGraphics3D.drawDebugCoordinateAxes();
		mGraphics3D.drawDebugVector(mPosition1, mRotVector1, FloatColor.YELLOW, 1, 0.9f, null);
		mGraphics3D.drawDebugVector(mPosition2, mRotatingVec, FloatColor.YELLOW, 1.0f, 0.9f, null);
		mGraphics3D.drawDebugVector(mPosition2, mRotVectorAround, FloatColor.GRAY, 1.25f, 1, null);
		mGraphics3D.drawDebugVector(mPosition3, mRotVector3, FloatColor.YELLOW, 1, 0.9f, null);
		mGraphics3D.drawDebugVector(mFromToPosition, mToVector1, FloatColor.YELLOW, 1.0f, 0.9f, null);
		mGraphics3D.drawDebugVector(mFromToPosition, mToVector2, FloatColor.YELLOW, 1.0f, 0.9f, null);

		mGraphics.bindTexture(mCubeTex);

		mEulerMatrix.loadIdentity();
		mEulerMatrix.rotateY(time);
		mTempQuaternion1.setFromMatrix(mEulerMatrix.mValues);
		mTrafo.setTranslation(0, -1, 0);
		mTrafo.multiplyQuaternionRight(mTempQuaternion1);
		mTrafo.scale(0.5f, 0.8f, 0.6f);
		mGraphics3D.drawCubeCentered(mTrafo);

		mTrafo.setTranslation(mPosition1);
		mTrafo.pointToDirection(mRotVector1);
		mTrafo.rotateY(time);
		mTrafo.scale(0.3f,0.9f,0.3f);
		mGraphics3D.drawCubeCentered(mTrafo);

		mTrafo.setTranslation(mPosition2);
		mTrafo.scale(0.8f);
		mTrafo.rotateAround(mRotatingVec,(float)mStateTimer);
		mGraphics3D.drawCubeCentered(mTrafo);

		mTrafo.setTranslation(mPosition3);
		mQuaternion1.setFromAxis(mRotVector3, time);
		mQuaternion2.setFromAxis(1, 0, 0, mQuatAngle);
		mTempQuaternion1.setConcat(mQuaternion2, mQuaternion1);
		mTrafo.multiplyQuaternionRight(mTempQuaternion1);
		mTrafo.scale(0.9f);
		mGraphics3D.drawCubeCentered(mTrafo);

		mTrafo.setTranslation(mFromToPosition);
		mTempQuaternion1.setFromToRotation(Vector3f.UP, mToVector1);
		mTempQuaternion2.setFromToRotation(Vector3f.UP, mToVector2);
		mTempQuaternion1.slerp(mTempQuaternion2, (float)Math.sin(time*1.2f)*0.5f+0.5f);
		mTrafo.multiplyQuaternionRight(mTempQuaternion1);
		mTrafo.scale(0.2f,1,0.2f);
		mTrafo.translate(0, 0.5f);
		mGraphics3D.drawCubeCentered(mTrafo);

	}

	@Override
	public void keyUp(int code) {
		super.keyUp(code);
		if(code=='1')
			mCamera.getCameraInstance().getForwardVector(mRotVector1);
		if(code=='2')
			mCamera.getCameraInstance().getForwardVector(mRotVectorAround);
		if(code=='3')
			mCamera.getCameraInstance().getForwardVector(mRotVector3);
		if(code=='4')
			mCamera.getCameraInstance().getForwardVector(mToVector1);
		if(code=='5')
			mCamera.getCameraInstance().getForwardVector(mToVector2);
		if(code=='r')
			mQuatAngle += 0.1f;
		if(code=='l')
			mQuatAngle -= 0.1f;
	}

}
