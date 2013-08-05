package yang.graphics.defaults;

import yang.graphics.defaults.programs.AdditiveModulateProgram;
import yang.graphics.font.LegacyAbstractFont;
import yang.graphics.programs.BasicProgram;
import yang.graphics.translator.GraphicsTranslator;
import yang.graphics.util.Camera2D;
import yang.math.MatrixOps;
import yang.model.Rect;

public class Default2DGraphics extends DefaultGraphics<BasicProgram>{
	
	public LegacyAbstractFont mLegacyDefaultFont;
	
	public static final float[] RECT = {
		0.0f, 0.0f, 0,
		1.0f, 0.0f, 0,
		0.0f, 1.0f, 0,
		1.0f, 1.0f, 0,
	};
	
	private BasicProgram mDefaultProgram;
	
	//State
	protected LegacyAbstractFont mCurrentLegacyFont;
	
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
	
	@Override
	public BasicProgram getDefaultProgram() {
		return mDefaultProgram;
	}

	private float drawChar(float x, float lineHeight, int c) {
		float charWidth = mCurrentLegacyFont.getFontW(c);
		float charHeight = mCurrentLegacyFont.getFontHeight();
		float sWidth = charWidth * lineHeight / charHeight;
		//TODO GC!!!!!!
		float fix[] = {
			mCurrentLegacyFont.getFontFix(c, 0) * lineHeight / charHeight,
			mCurrentLegacyFont.getFontFix(c, 1) * lineHeight / charHeight,
			mCurrentLegacyFont.getFontFix(c, 2) * lineHeight / charHeight,
			mCurrentLegacyFont.getFontFix(c, 3) * lineHeight / charHeight
		};
		mInterTransf2.setRect(x - fix[1], -fix[3], x + sWidth + fix[2], lineHeight + fix[0]);
		mInterTransf2.multiplyLeft(mInterTransf1);
		drawQuad(mInterTransf2, mCurrentLegacyFont.getTexTransformation(c));

		return sWidth;
	}

	@Deprecated
	private void drawStringLegacy(float lineHeight, float anchorX, float anchorY, float angle, float charDistance,String s) {
		int sLength = s.length();
		if (angle != 0.0f)
			mInterTransf1.rotateZ(angle);
		mInterTransf1.translate((-(anchorX + 1) * 0.5f) * stringWidth(lineHeight, charDistance, s), (-(anchorY + 1) * 0.5f) * lineHeight);
		
		mTranslator.bindTexture(mCurrentLegacyFont.getTexture(),0);
		
		float x = 0;
		for (int i = 0; i < sLength; ++i) {
			if(charDistance<0)
				x += drawChar(x, lineHeight, s.codePointAt(i));
			else{
				drawChar(x, lineHeight, s.codePointAt(i));
				x+=charDistance;
			}
		}
	}

	@Deprecated
	/**
	 * Draw a String left justified in game coordinates.
	 * 
	 * @param lineHeight
	 *            vertical space between the baselines of two consecutive lines
	 *            of text
	 * @param anchorX
	 *            left = -1 , center = 0, right = 1
	 * @param anchorY
	 *            top = -1 , center = 0, bottom = 1
	 */
	public void drawStringLegacy(float x, float y, float lineHeight, float anchorX, float anchorY, float angle, float charDistance,String s) {
		mInterTransf1.loadIdentity();
		mInterTransf1.translate(x, y);
		drawStringLegacy(lineHeight, anchorX, anchorY, angle, charDistance, s);
	}
	
	@Deprecated
	public void drawTextLegacy(float x, float y, float lineHeight, float lineDistance, float anchorX, float anchorY, String[] text) {
		float yPos = y;
		for(String s:text) {
			drawStringLegacy(x,yPos,lineHeight,anchorX,anchorY,0,-1,s);
			yPos -= lineDistance;
		}
	}
	
	@Deprecated
	public void drawTextLegacy(float x, float y, float lineHeight, float anchorX, float anchorY, String[] text) {
		drawTextLegacy(x,y,lineHeight,lineHeight*1.2f,anchorX,anchorY,text);
	}
	
	@Deprecated
	public void drawStringLegacy(float x, float y, float lineHeight, float anchorX, float anchorY, float angle, String s) {
		drawStringLegacy(x,y,lineHeight,anchorX,anchorY,angle,-1,s);
	}

	@Deprecated
	/** Draw a String left justified in game coordinates. */
	public void drawStringLegacyL(float x, float y, float lineHeight, String s) {
		drawStringLegacy(x, y, lineHeight, -1, -1, 0, s);
	}

	@Deprecated
	/** Draw a String centered in game coordinates. */
	public void drawStringLegacyC(float x, float y, float lineHeight, String s) {
		drawStringLegacy(x, y, lineHeight, 0, 0, 0, s);
	}

	@Deprecated
	/** Draw a String right justified in game coordinates. */
	public void drawStringLegacyR(float x, float y, float lineHeight, String s) {
		drawStringLegacy(x, y, lineHeight, 1, -1, 0, s);
	}

	@Deprecated
	/** Draw a String centered in game coordinates with a rotation angle. */
	public void drawStringLegacyC(float x, float y, float lineHeight, float angle, String s) {
		drawStringLegacy(x, y, lineHeight, 0, 0, angle, s);
	}

	@Deprecated
	/** Draw a String left justified in lineHeight coordinates. */
	public void drawStringLegacyConsole(float x, float y, float lineHeight, String s) {
		x = x * lineHeight - 1.0f;
		y = y * lineHeight - 1.0f;
		drawStringLegacyL(x, y, lineHeight, s);
	}
	
	@Deprecated
	/** Calculate width of a String. */
	public float stringWidth(float lineHeight, String s) {
		return mCurrentLegacyFont.stringWidth(lineHeight, s);
	}
	
	@Deprecated
	public float stringWidth(float lineHeight, float charDistance, String s) {
		if(charDistance<0)
			return stringWidth(lineHeight,s);
		else
			return charDistance*s.length();
	}

	protected void refreshCamera() {
		mOrthoLeft = -mTranslator.mCurrentScreen.getSurfaceRatioX() * mZoom + mCamX;
		mOrthoRight = mTranslator.mCurrentScreen.getSurfaceRatioX() * mZoom + mCamX;
		mOrthoTop = mTranslator.mCurrentScreen.getSurfaceRatioY() * mZoom + mCamY;
		mOrthoBottom = -mTranslator.mCurrentScreen.getSurfaceRatioY() * mZoom + mCamY;
		if(mCamRot==0) {
			mOrthoWidth = (int)(Math.ceil(mOrthoRight - mOrthoLeft));
			mOrthoHeight = (int)(Math.ceil(mOrthoTop - mOrthoBottom));
	
			mProjectionTransform.setOrthogonalProjection(mOrthoLeft, mOrthoRight, mOrthoTop, mOrthoBottom);
		}else{
			mProjectionTransform.setOrthogonalProjection(
					-mTranslator.mCurrentScreen.getSurfaceRatioX() * mZoom, mTranslator.mCurrentScreen.getSurfaceRatioX() * mZoom,
					 mTranslator.mCurrentScreen.getSurfaceRatioY() * mZoom, -mTranslator.mCurrentScreen.getSurfaceRatioY() * mZoom
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
		return invGameProjection[0] * mTranslator.mInvRatioX * x + invGameProjection[12];
	}
	
	/**
	 * Only for non-rotating cam!
	 * @param x
	 * @return
	 */
	public float normToGameY(float y) {
		return invGameProjection[5] * mTranslator.mInvRatioY * y + invGameProjection[13];
	}
	
	public int projScreenX(float gameX,float gameY) {
		float x = MatrixOps.applyFloatMatrixX2D(mCurProjTransform.mMatrix, gameX, gameY);
		return (int)((x+1)*mTranslator.mScreenWidth*0.5f);
	}
	
	public int projScreenY(float gameX,float gameY) {
		float y = MatrixOps.applyFloatMatrixY2D(mCurProjTransform.mMatrix, gameX, gameY);
		return (int)((-y+1)*mTranslator.mScreenHeight*0.5f);
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

	public void switchZBuffer(boolean enabled) {
		mTranslator.switchZBuffer(enabled);
	}

	public void beginQuad(boolean wireFrames) {
		mCurrentVertexBuffer.beginQuad(wireFrames);
	}
	
	@Deprecated
	public void setLegacyFont(LegacyAbstractFont newFont) {
		mCurrentLegacyFont = newFont;
	}
	
	@Deprecated
	public void setDefaultLegacyFont() {
		setLegacyFont(mLegacyDefaultFont);
	}

	@Override
	public void onSurfaceSizeChanged(int width, int height) {
		refreshCamera();
	}
	
}
