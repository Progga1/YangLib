package yang.graphics.defaults;

import yang.graphics.camera.Camera2D;
import yang.graphics.programs.BasicProgram;
import yang.graphics.translator.GraphicsTranslator;
import yang.graphics.util.Camera2DSmooth;
import yang.math.MatrixOps;

public class Default2DGraphics extends DefaultGraphics<BasicProgram>{

	public static final float[] RECT = {
		0.0f, 0.0f, 0,
		1.0f, 0.0f, 0,
		0.0f, 1.0f, 0,
		1.0f, 1.0f, 0,
	};

	private BasicProgram mDefaultProgram;

	//Camera
	protected Camera2D mCamera2D;
	protected float mStereoGameDistance = 1.2f;

	public Default2DGraphics(GraphicsTranslator graphics) {
		super(graphics,3);
	}

	@Override
	protected void derivedInit() {
		super.derivedInit();
		mCamera2D = new Camera2D();
		mDefaultProgram = new BasicProgram();
		mTranslator.addProgram(mDefaultProgram);
	}

	//TODO does nothing
	public void setStereoZDistance(float distance) {
		flush();
		mStereoGameDistance = distance;
		if(mAutoRefreshCameraTransform)
			mCameraProjection.copyFrom(mCamera2D);
	}

	@Override
	public BasicProgram getDefaultProgram() {
		return mDefaultProgram;
	}

	public void setCamera(float x, float y, float zoom, float rotation) {
		mTranslator.flush();
		mCamera2D.set(x,y,zoom,rotation);
		if(mAutoRefreshCameraTransform)
			mCameraProjection.copyFrom(mCamera2D);
	}

	public void setCamera(float x, float y, float zoom) {
		setCamera(x,y,zoom,0);
	}

	public void setCamera(Camera2DSmooth camera) {
		setCamera(camera.getX(),camera.getY(),camera.getZoom(),camera.getRotation());
	}

	public float normToWorldX(float x,float y) {
		return mInvViewProjectionTransform.mValues[0] * x + mInvViewProjectionTransform.mValues[4] * y + mInvViewProjectionTransform.mValues[12];
	}

	public float normToWorldY(float x,float y) {
		return mInvViewProjectionTransform.mValues[1] * x + mInvViewProjectionTransform.mValues[5] * y + mInvViewProjectionTransform.mValues[13];
	}

	/**
	 * Only for non-rotating cam!
	 * @param normX
	 * @return
	 */
	public float normToWorldX(float normX) {
		return mInvViewProjectionTransform.mValues[0]*normX + mInvViewProjectionTransform.mValues[12];
	}

	/**
	 * Only for non-rotating cam!
	 * @param x
	 * @return
	 */
	public float normToWorldY(float normY) {
		return mInvViewProjectionTransform.mValues[5]*normY + mInvViewProjectionTransform.mValues[13];
	}

	public float worldToNormX(float worldX,float worldY) {
		final float x = MatrixOps.applyFloatMatrixX2D(mViewProjectionTransform.mValues, worldX, worldY);
		return x*mTranslator.mCurrentSurface.getSurfaceRatioX();
	}

	public float worldToNormY(float worldX,float worldY) {
		final float y = MatrixOps.applyFloatMatrixY2D(mViewProjectionTransform.mValues, worldX, worldY);
		return y*mTranslator.mCurrentSurface.getSurfaceRatioY();
	}

	public float normLeftToWorldX() {
		return normToWorldX(-mTranslator.mRatioX);
	}

	public float normRightToWorldX() {
		return normToWorldX(mTranslator.mRatioX);
	}

	public float normTopToWorldY() {
		return normToWorldY(mTranslator.mRatioY);
	}

	public float normBottomToWorldY() {
		return normToWorldY(-mTranslator.mRatioY);
	}

	public float normCenterToWorldY() {
		return mInvViewProjectionTransform.mValues[12];
	}

	public float normCenterToWorldX() {
		return mInvViewProjectionTransform.mValues[13];
	}

	@Override
	public void onSurfaceSizeChanged(int width, int height) {

	}

	public void resetCamera() {
		setCamera(0,0,1);
	}

	public float getCamZoom() {
		return mCameraProjection.getZ();
	}
}
