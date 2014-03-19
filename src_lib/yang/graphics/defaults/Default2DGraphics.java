package yang.graphics.defaults;

import yang.graphics.programs.BasicProgram;
import yang.graphics.translator.GraphicsTranslator;
import yang.graphics.util.Camera2D;
import yang.math.MatrixOps;
import yang.math.objects.matrix.YangMatrixCameraOps;
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
	private float mCamX;
	private float mCamY;
	private float mZoom;
	private float mCamRot;
	protected float mOrthoLeft;
	protected float mOrthoRight;
	protected float mOrthoTop;
	protected float mOrthoBottom;
	private int mOrthoWidth;
	private int mOrthoHeight;
	protected float mStereoGameDistance = 1.2f;
	public float mGameNear = YangMatrixCameraOps.DEFAULT_NEAR;
	public float mGameFar = YangMatrixCameraOps.DEFAULT_FAR;

	public Default2DGraphics(GraphicsTranslator graphics) {
		super(graphics,3);
	}

	@Override
	protected void derivedInit() {
		super.derivedInit();
		mDefaultProgram = new BasicProgram();
		mTranslator.addProgram(mDefaultProgram);
		mCamX = 0;
		mCamY = 0;
		mZoom = 1;
		mCamRot = 0;
	}

	public boolean inScreen2D(float posX,float posY,float width, float height) {
		if(mWorldTransformEnabled) {
			posX += mWorldTransform.get(12);
			posY += mWorldTransform.get(13);
		}
		if(mCurProjTransform==mProjectionTransform)
			return posX<=screenRightToGameX() && posY<=screenTopToGameY() && (posX>=screenLeftToGameX()-width) && (posY>=screenBottomToGameY()-height);
		else
			return posX<=mTranslator.mRatioX && posY<=mTranslator.mRatioY && (posX>=-mTranslator.mRatioX-width) && (posY>=-mTranslator.mRatioY-height);
	}

	public void setStereoZDistance(float distance) {
		flush();
		mStereoGameDistance = distance;
		refreshCamera();
	}

	@Override
	public BasicProgram getDefaultProgram() {
		return mDefaultProgram;
	}

	protected void refreshCamera() {
		final float ratioX = mTranslator.mCurrentSurface.getSurfaceRatioX();
		final float ratioY = mTranslator.mCurrentSurface.getSurfaceRatioY();
		float shift = 0;
		if(mTranslator.isStereo())
			shift = get2DStereoShift(mStereoGameDistance);
		mOrthoLeft = -ratioX * mZoom + mCamX + shift;
		mOrthoRight = ratioX * mZoom + mCamX + shift;
		mOrthoTop = ratioY * mZoom + mCamY;
		mOrthoBottom = -ratioY * mZoom + mCamY;
		if(mCamRot==0) {
			mOrthoWidth = (int)(Math.ceil(mOrthoRight - mOrthoLeft));
			mOrthoHeight = (int)(Math.ceil(mOrthoTop - mOrthoBottom));

			mProjectionTransform.setOrthogonalProjection(mOrthoLeft, mOrthoRight, mOrthoTop, mOrthoBottom, mGameFar, mGameNear);
		}else{
			mProjectionTransform.setOrthogonalProjection(
					-mTranslator.mCurrentSurface.getSurfaceRatioX() * mZoom, ratioX * mZoom,
					 mTranslator.mCurrentSurface.getSurfaceRatioY() * mZoom, -ratioY * mZoom
					);
			mProjectionTransform.rotateZ(mCamRot);
			mProjectionTransform.translate(-mCamX, -mCamY);
		}
		mProjectionTransform.asInverted(invGameProjection);
	}

	public int getOrthoWidth() {
		return mOrthoWidth;
	}

	public int getOrthoHeight() {
		return mOrthoHeight;
	}

	public void setCamera(float x, float y, float zoom, float rotation) {
		if(Thread.currentThread().getId()==mTranslator.mThreadId)
			mTranslator.flush();
		mCamX = x;
		mCamY = y;
		mZoom = zoom;
		mCamRot = rotation;
		refreshCamera();
	}

	public void setCamera(float x, float y, float zoom) {
		setCamera(x,y,zoom,mCamRot);
	}

	public void setCamera(Camera2D camera) {
		setCamera(camera.getX(),camera.getY(),camera.getZoom(),camera.getRotation());
	}

	public float normToScreenX(float x) {
		return x;
	}

	public float normToScreenY(float y) {
		return y;
	}

	public float normToGameX(float x,float y) {
		return invGameProjection[0] * mTranslator.mInvRatioX * x + invGameProjection[4] * y + invGameProjection[12];
	}

	public float normToGameY(float x,float y) {
		return invGameProjection[1] * mTranslator.mInvRatioY * x + invGameProjection[5] * y + invGameProjection[13];
	}

	/**
	 * Only for non-rotating cam!
	 * @param x
	 * @return
	 */
	public float normToGameX(float x) {
		return invGameProjection[0]/mTranslator.mCurrentSurface.getSurfaceRatioX()*x + invGameProjection[12];
	}

	/**
	 * Only for non-rotating cam!
	 * @param x
	 * @return
	 */
	public float normToGameY(float y) {
		return invGameProjection[5]/mTranslator.mCurrentSurface.getSurfaceRatioY()*y + invGameProjection[13];
	}

	public int projScreenX(float gameX,float gameY) {
		final float x = MatrixOps.applyFloatMatrixX2D(mProjectionTransform.mValues, gameX, gameY);
		return (int)((x+1)*mTranslator.mCurrentSurface.getSurfaceWidth()*0.5f);
	}

	public int projScreenY(float gameX,float gameY) {
		final float y = MatrixOps.applyFloatMatrixY2D(mProjectionTransform.mValues, gameX, gameY);
		return (int)((-y+1)*mTranslator.mCurrentSurface.getSurfaceHeight()*0.5f);
	}

	public float projNormX(float gameX,float gameY) {
		final float x = MatrixOps.applyFloatMatrixX2D(mProjectionTransform.mValues, gameX, gameY);
		return x*mTranslator.mCurrentSurface.getSurfaceRatioX();
	}

	public float projNormY(float gameX,float gameY) {
		final float y = MatrixOps.applyFloatMatrixY2D(mProjectionTransform.mValues, gameX, gameY);
		return y*mTranslator.mCurrentSurface.getSurfaceRatioY();
	}

	public float screenLeftToGameX() {
		return mOrthoLeft;
	}

	public float screenRightToGameX() {
		return mOrthoRight;
	}

	public float screenTopToGameY() {
		return mOrthoTop;
	}

	public float screenBottomToGameY() {
		return mOrthoBottom;
	}

	public float screenCenterToGameY() {
		return (mOrthoBottom+mOrthoTop)/2f;
	}

	public float screenCenterToGameX() {
		return (mOrthoLeft+mOrthoRight)/2f;
	}

	public float applyWorldTransformX(float x,float y) {
		return x;
	}

	public float applyWorldTransformY(float x,float y) {
		return y;
	}

	@Override
	public void refreshViewTransform() {
		mCameraProjectionMatrix.set(mCurProjTransform);
	}

	public boolean rectInScreen2D(float posX,float posY,Rect mRect) {
		return  posX+mRect.mLeft<=screenRightToGameX() && posY+mRect.mBottom<=screenTopToGameY() && (posX+mRect.mRight>=screenLeftToGameX()) && (posY+mRect.mTop>=screenBottomToGameY());
	}

	public void beginQuad(boolean wireFrames) {
		mCurrentVertexBuffer.beginQuad(wireFrames);
	}

	@Override
	public void onSurfaceSizeChanged(int width, int height) {
		refreshCamera();
	}

	public void resetCamera() {
		setCamera(0,0,1);
	}

	public float getCamX() {
		return mCamX;
	}

	public float getCamY() {
		return mCamY;
	}

	public float getCamZoom() {
		return mZoom;
	}

	public float getCamRot() {
		return mCamRot;
	}
}
