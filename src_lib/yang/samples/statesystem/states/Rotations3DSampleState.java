package yang.samples.statesystem.states;

import yang.graphics.model.FloatColor;
import yang.graphics.translator.Texture;
import yang.graphics.translator.glconsts.GLMasks;
import yang.math.objects.Point3f;
import yang.math.objects.Quaternion;
import yang.math.objects.Vector3f;
import yang.math.objects.matrix.YangMatrix;
import yang.samples.statesystem.SampleStateCameraControl;

public class Rotations3DSampleState extends SampleStateCameraControl {

	private YangMatrix mTrafo = new YangMatrix();
	private Point3f mPosition1 = new Vector3f(1.5f,1.2f,1.3f);
	private Vector3f mRotVector1 = new Vector3f();
	private Vector3f mRotVectorAround = new Vector3f();
	private Point3f mPosition2 = new Vector3f(-1.0f,1.0f,-0.9f);
	private Vector3f mRotVector2 = new Vector3f();
	private Point3f mPosition3 = new Vector3f(1,1,-0.5f);
	private Vector3f mRotVector3 = new Vector3f();
	private Vector3f mQuatVector1 = new Vector3f();
	private Quaternion mQuaternion1 = new Quaternion();
	private Quaternion mQuaternion2 = new Quaternion();
	private Quaternion mTempQuaternion = new Quaternion();
	private Vector3f mTempVec = new Vector3f();
	private float mQuatAngle = 0;
	
	private Texture mCubeTex;
	
	@Override
	public void initGraphics() {
		mRotVector1.setNormalized(1, 0.5f, 0.8f);
		mRotVectorAround.setNormalized(1, 1, 0.1f);
		mRotVector2.setNormalized(1, -1, 0.5f);
		mRotVector3.setNormalized(0.5f, 0.5f, -1);
		mQuatVector1.set(mRotVector1);

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
		float time = (float)mStateTimer;
		mGraphics.bindTexture(null);
		mGraphics3D.setWhite();
		mGraphics3D.activate();
		mGraphics.clear(0,0,0.1f,GLMasks.DEPTH_BUFFER_BIT);
		
		mGraphics.switchZBuffer(true);
		mGraphics.switchCulling(false);
		super.setCamera();
		
		mQuaternion1.setFromAxis(mQuatVector1, time*0.5f);

		//mRotVector2.setNormalized((float)Math.sin(time*0.1f), (float)Math.cos(time*0.1f), 0);
		
		mTempQuaternion.setFromAxis(mRotVectorAround, time*0.6f);
		mTempVec.applyQuaternion(mTempQuaternion,mRotVector2);
		mGraphics3D.drawDebugCoordinateAxes();
		mGraphics3D.drawDebugVector(mPosition1, mRotVector1, FloatColor.YELLOW, 0.9f);
		mGraphics3D.drawDebugVector(mPosition2, mTempVec, FloatColor.YELLOW, 0.9f);
		mGraphics3D.drawDebugVector(mPosition2, mRotVectorAround, FloatColor.GRAY, 1);
		mGraphics3D.drawDebugVector(mPosition3, mRotVector3, FloatColor.YELLOW, 0.9f);
		
		mGraphics.bindTexture(mCubeTex);
		
		
		mTrafo.setTranslation(mPosition1);
		mTrafo.pointToDirection(mRotVector1);
		mTrafo.rotateY(time);
		mTrafo.scale(0.7f);
		mGraphics3D.drawCubeCentered(mTrafo);
		
		mTrafo.setTranslation(mPosition2);
		mTrafo.scale(0.8f);
		mTrafo.rotateAround(mTempVec,(float)mStateTimer);
		
		mGraphics3D.drawCubeCentered(mTrafo);
		
		mTrafo.setTranslation(mPosition3);
		mQuaternion1.setFromAxis(mRotVector3, time);
		mQuaternion2.setFromAxis(1, 0, 0, mQuatAngle);
		mTempQuaternion.setConcat(mQuaternion2, mQuaternion1);
		mTrafo.multiplyQuaternionRight(mTempQuaternion);
		mTrafo.scale(0.9f);
		mGraphics3D.drawCubeCentered(mTrafo);
	}

	@Override
	public void keyUp(int code) {
		super.keyUp(code);
		if(code=='1') {
			mCamera.getCameraInstance().getForwardVector(mRotVector1);
		}
		if(code=='2') {
			mCamera.getCameraInstance().getForwardVector(mRotVectorAround);
		}
		if(code=='3') {
			mCamera.getCameraInstance().getForwardVector(mRotVector3);
		}
		if(code=='r')
			mQuatAngle += 0.1f;
		if(code=='l')
			mQuatAngle -= 0.1f;
	}
	
}
