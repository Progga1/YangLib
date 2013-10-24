package yang.samples.statesystem.states;

import yang.graphics.model.FloatColor;
import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.translator.Texture;
import yang.graphics.translator.glconsts.GLMasks;
import yang.math.objects.matrix.YangMatrix;
import yang.samples.statesystem.SampleStateCameraControl;

public class StereoCalibrationState extends SampleStateCameraControl {

	public float mBorder = 0.1f;
	public float mCrossSize = 0.1f;
	//public boolean mDrawCube = true;
	public boolean mDrawCross = false;
	public YangMatrix mTransform = new YangMatrix();
	public Texture mCubeTex;
	public boolean mUseDebugTransform = true;

	public StereoCalibrationState() {

	}

	@Override
	protected void initGraphics() {
		super.mOrthogonalProjection = false;
		mCamera.setZoom(3);
		mCubeTex = mGFXLoader.getImage("cube",TextureFilter.LINEAR_MIP_LINEAR);
	}

	@Override
	protected void draw() {
		final float GRAY = 0.15f;
		mGraphics.clear(GRAY,GRAY,GRAY+0.25f, GLMasks.DEPTH_BUFFER_BIT);
		mGraphics3D.activate();
		mGraphics.bindTexture(null);

		setCamera();
		mGraphics3D.switchGameCoordinates(true);
		mGraphics.switchZBuffer(true);
		mGraphics.switchZWriting(true);

		mTransform.loadIdentity();
		mGraphics3D.drawDebugCoordinateAxes();
		if(mUseDebugTransform) {
			mTransform.set(mGraphics.mSensorOrientationMatrix);
		}else{
			mTransform.rotateY((float)mStateTimer);
			//mTransform.scale(0.5f);
		}
		//mTransform.scale(1,1,0.5f);

		mGraphics3D.setWhite();
		mGraphics.bindTexture(mCubeTex);

		if(!mDrawCross)
			mGraphics3D.drawCubeCentered(mTransform);
		mGraphics3D.flush();
		mGraphics.switchZBuffer(false);

		mGraphics3D.switchGameCoordinates(false);
		mGraphics.bindTexture(null);
		if(mDrawCross) {
			mGraphics3D.setColor(FloatColor.YELLOW);
			final float crossWidth = mCrossSize*0.2f;
			mGraphics3D.drawLine(-mCrossSize,0, mCrossSize,0, crossWidth);
			mGraphics3D.drawLine(0,-mCrossSize, 0, mCrossSize, crossWidth);
			mGraphics3D.setColor(FloatColor.BLACK);
			mGraphics3D.drawLine(-mCrossSize+crossWidth*0.25f,0, mCrossSize-crossWidth*0.25f,0, crossWidth*0.5f);
			mGraphics3D.drawLine(0,-mCrossSize+crossWidth*0.25f, 0, mCrossSize-crossWidth*0.25f, crossWidth*0.5f);
		}

		mGraphics3D.switchGameCoordinates(false);
		final float width = mGraphics.getSurfaceRatioX()-mBorder;
		final float height = mGraphics.getSurfaceRatioY()-mBorder;
		mGraphics3D.setColor(FloatColor.YELLOW);
		mGraphics3D.drawLineRect(-width, -height, width, height, 0.08f, null);


	}

	@Override
	public void keyDown(int code) {
		super.keyDown(code);
		if(code=='c')
			mDrawCross ^= true;
//		if(code=='d')
//			mDrawCube ^= true;
	}

	@Override
	public void stop() {
		mGraphics3D.switchGameCoordinates(true);
	}

}
