package yang.graphics.defaults;

import yang.graphics.camera.Camera2D;
import yang.graphics.programs.BasicProgram;
import yang.graphics.translator.GraphicsTranslator;
import yang.graphics.util.LegacyCamera2D;
import yang.math.MatrixOps;
import yang.model.Rect;

public class Default2DGraphics extends DefaultGraphics<BasicProgram>{

	public static final float[] RECT = {
		0.0f, 0.0f, 0,
		1.0f, 0.0f, 0,
		0.0f, 1.0f, 0,
		1.0f, 1.0f, 0,
	};

	private BasicProgram mDefaultProgram;

	//Camera
	protected float mStereoGameDistance = 1.2f;

	public Default2DGraphics(GraphicsTranslator graphics) {
		super(graphics,3);
	}

	@Override
	protected void derivedInit() {
		super.derivedInit();
		mDefaultProgram = new BasicProgram();
		mTranslator.addProgram(mDefaultProgram);
	}

	public boolean inScreen2D(float posX,float posY,float width, float height) {
		if(mWorldTransformEnabled) {
			posX += mWorldTransform.get(12);
			posY += mWorldTransform.get(13);
		}
		if(mCurViewProjTransform==mViewProjectionTransform)
			return posX<=screenRightToGameX() && posY<=screenTopToGameY() && (posX>=screenLeftToGameX()-width) && (posY>=screenBottomToGameY()-height);
		else
			return posX<=mTranslator.mRatioX && posY<=mTranslator.mRatioY && (posX>=-mTranslator.mRatioX-width) && (posY>=-mTranslator.mRatioY-height);
	}

	public void setStereoZDistance(float distance) {
		flush();
		mStereoGameDistance = distance;
		if(mAutoRefreshCameraTransform)
			refreshCameraTransform();
	}

	@Override
	public BasicProgram getDefaultProgram() {
		return mDefaultProgram;
	}

	public void setCamera(float x, float y, float zoom, float rotation) {
		if(Thread.currentThread().getId()==mTranslator.mThreadId)
			mTranslator.flush();
		Camera2D.set(mCamera,x,y,zoom,rotation);
		if(mAutoRefreshCameraTransform)
			refreshCameraTransform();
	}

	public void setCamera(float x, float y, float zoom) {
		setCamera(x,y,zoom,0);
	}

	public void setCamera(LegacyCamera2D camera) {
		setCamera(camera.getX(),camera.getY(),camera.getZoom(),camera.getRotation());
	}

	public float normToGameX(float x,float y) {
		return mInvViewProjectionTransform.mValues[0] * x + mInvViewProjectionTransform.mValues[4] * y + mInvViewProjectionTransform.mValues[12];
	}

	public float normToGameY(float x,float y) {
		return mInvViewProjectionTransform.mValues[1] * x + mInvViewProjectionTransform.mValues[5] * y + mInvViewProjectionTransform.mValues[13];
	}

	/**
	 * Only for non-rotating cam!
	 * @param x
	 * @return
	 */
	public float normToGameX(float x) {
		return mInvViewProjectionTransform.mValues[0]*x + mInvViewProjectionTransform.mValues[12];
	}

	/**
	 * Only for non-rotating cam!
	 * @param x
	 * @return
	 */
	public float normToGameY(float y) {
		return mInvViewProjectionTransform.mValues[5]*y + mInvViewProjectionTransform.mValues[13];
	}

//	public int projScreenX(float gameX,float gameY) {
//		final float x = MatrixOps.applyFloatMatrixX2D(mViewProjectionTransform.mValues, gameX, gameY);
//		return (int)((x+1)*mTranslator.mCurrentSurface.getSurfaceWidth()*0.5f);
//	}
//
//	public int projScreenY(float gameX,float gameY) {
//		final float y = MatrixOps.applyFloatMatrixY2D(mViewProjectionTransform.mValues, gameX, gameY);
//		return (int)((-y+1)*mTranslator.mCurrentSurface.getSurfaceHeight()*0.5f);
//	}

	public float projNormX(float gameX,float gameY) {
		final float x = MatrixOps.applyFloatMatrixX2D(mViewProjectionTransform.mValues, gameX, gameY);
		return x*mTranslator.mCurrentSurface.getSurfaceRatioX();
	}

	public float projNormY(float gameX,float gameY) {
		final float y = MatrixOps.applyFloatMatrixY2D(mViewProjectionTransform.mValues, gameX, gameY);
		return y*mTranslator.mCurrentSurface.getSurfaceRatioY();
	}

	public float screenLeftToGameX() {
		return normToGameX(-mTranslator.mRatioX);
	}

	public float screenRightToGameX() {
		return normToGameX(mTranslator.mRatioX);
	}

	public float screenTopToGameY() {
		return normToGameY(mTranslator.mRatioY);
	}

	public float screenBottomToGameY() {
		return normToGameY(-mTranslator.mRatioY);
	}

	public float screenCenterToGameY() {
		return mInvViewProjectionTransform.mValues[12];
	}

	public float screenCenterToGameX() {
		return mInvViewProjectionTransform.mValues[13];
	}

	public boolean rectInScreen2D(float posX,float posY,Rect mRect) {
		return  posX+mRect.mLeft<=screenRightToGameX() && posY+mRect.mBottom<=screenTopToGameY() && (posX+mRect.mRight>=screenLeftToGameX()) && (posY+mRect.mTop>=screenBottomToGameY());
	}

	public void beginQuad(boolean wireFrames) {
		mCurrentVertexBuffer.beginQuad(wireFrames);
	}

	@Override
	public void onSurfaceSizeChanged(int width, int height) {

	}

	public void resetCamera() {
		setCamera(0,0,1);
	}

	public float getCamZoom() {
		return mCamera.getZ();
	}

	//TODO remove
	public float normToScreenX(float x) {
		return x;
	}

	public float normToScreenY(float y) {
		return y;
	}
}
