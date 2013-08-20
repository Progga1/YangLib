package yang.samples.statesystem.states;

import yang.graphics.model.FloatColor;
import yang.graphics.translator.Texture;
import yang.graphics.translator.glconsts.GLMasks;
import yang.math.objects.Quaternion;
import yang.math.objects.Vector3f;
import yang.math.objects.matrix.YangMatrix;
import yang.samples.statesystem.SampleStateCameraControl;

public class Rotations3DSampleState extends SampleStateCameraControl {

	private YangMatrix mTrafo = new YangMatrix();
	private Quaternion mQuaternion1 = new Quaternion();
	private Vector3f mPosVector1 = new Vector3f(1.5f,1.2f,1.3f);
	private Vector3f mRotVector1 = new Vector3f();
	private Vector3f mQuatVector1 = new Vector3f();
	private Vector3f mPosVector2 = new Vector3f(-1.0f,1.0f,-0.9f);
	private Vector3f mRotVector2 = new Vector3f();
	private Texture mCubeTex;
	
	@Override
	public void initGraphics() {
		mRotVector1.setNormalized(1, 1, 0.5f);
		mRotVector2.setNormalized(1, -1, 0.5f);
		mQuatVector1.set(mRotVector1);

		mCamera.mZoom = 3;
		mCubeTex = mGFXLoader.getImage("cube");
		refreshCamera();
	}
	
	@Override
	protected void step(float deltaTime) {
		
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
		
		mQuaternion1.set(mQuatVector1, time*0.5f);

		//mRotVector2.setNormalized((float)Math.sin(time*0.1f), (float)Math.cos(time*0.1f), 0);
		
		mGraphics3D.drawDebugCoordinateAxes();
		mGraphics3D.drawDebugVector(mPosVector1, mRotVector1, FloatColor.YELLOW, 0.9f);
		mGraphics3D.drawDebugVector(mPosVector2, mRotVector2, FloatColor.YELLOW, 0.9f);
		
		mTrafo.loadIdentity();
		mTrafo.translate(mPosVector1);
		mTrafo.pointTo(mRotVector1);
		mTrafo.rotateY(time);
		mTrafo.scale(0.8f);
		mGraphics.bindTexture(mCubeTex);
		mGraphics3D.drawCubeCentered(mTrafo);
		
		mTrafo.loadIdentity();
		mTrafo.translate(mPosVector2);
		mTrafo.scale(0.8f);
		mTrafo.rotateAround(mRotVector2,(float)mStateTimer);
		mGraphics.bindTexture(mCubeTex);
		mGraphics3D.drawCubeCentered(mTrafo);
	}

	@Override
	public void keyUp(int code) {
		super.keyUp(code);
		if(code=='1') {
			mCamera.getCameraInstance().getForwardVector(mRotVector1);
		}
		if(code=='2') {
			mCamera.getCameraInstance().getForwardVector(mRotVector2);
		}
	}
	
}
