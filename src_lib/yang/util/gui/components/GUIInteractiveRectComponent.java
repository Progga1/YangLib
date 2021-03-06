package yang.util.gui.components;

import yang.graphics.model.FloatColor;


public class GUIInteractiveRectComponent extends GUIInteractiveComponent {

	public float mWidth,mHeight;
	public float mProjWidth,mProjHeight;
	public FloatColor mIndividualColor = null;

	public GUIInteractiveRectComponent() {
		super();
		mPosX = 0;
		mPosY = 0;
		mPosZ = 0;
		mWidth = 1;
		mHeight = 1;
	}

	@Override
	public void refreshProjections(float offsetX,float offsetY) {
		mProjLeft = mGUI.mProjShiftX+(mPosX+offsetX)*mGUI.mProjXFactor;
		mProjBottom = mGUI.mProjShiftY+(mPosY+offsetY)*mGUI.mProjYFactor+mHeight*mGUI.mProjShiftYFactor;
		mProjWidth = mWidth*mGUI.mProjWidthFactor;
		mProjHeight = mHeight*mGUI.mProjHeightFactor;
	}

	@Override
	public float getProjCenterX() {
		return mProjLeft+mProjWidth*0.5f;
	}

	@Override
	public float getProjCenterY() {
		return mProjBottom+mProjHeight*0.5f;
	}

	@Override
	public float getProjWidth() {
		return mProjWidth;
	}

	@Override
	public float getProjHeight() {
		return mProjHeight;
	}

	public GUIInteractiveRectComponent setBounds(float left,float top,float right,float bottom) {
		mPosX = left;
		mPosY = top;
		mWidth = right-left;
		mHeight = bottom-top;
		return this;
	}

	public GUIInteractiveRectComponent setBounds(float left,float top,float right) {
		mPosX = left;
		mPosY = top;
		mWidth = right-left;
		return this;
	}

	public GUIInteractiveRectComponent setBounds(GUIInteractiveRectComponent preface) {
		mPosX = preface.mPosX;
		mPosY = preface.mPosY;
		mWidth = preface.mWidth;
		mHeight = preface.mHeight;
		return this;
	}


	public GUIInteractiveRectComponent setPosCentered(float centerX, float centerY) {
		return setBounds(centerX-mWidth*0.5f,centerY-mHeight*0.5f,centerX+mWidth*0.5f,centerY+mHeight*0.5f);
	}

	public GUIInteractiveRectComponent setPosAndExtentsCentered(float centerX, float centerY, float width, float height) {
		return setBounds(centerX-width*0.5f,centerY-height*0.5f,centerX+width*0.5f,centerY+height*0.5f);
	}

	public GUIInteractiveRectComponent setPosAndExtents(float left, float top, float width, float height) {
		mPosX = left;
		mPosY = top;
		mWidth = width;
		mHeight = height;
		return this;
	}

	public GUIInteractiveRectComponent setExtents(float width,float height) {
		mWidth = width;
		mHeight = height;
		return this;
	}

	protected void drawRect() {
		mGUI.mGraphics.drawRect(mProjLeft,mProjBottom,mProjLeft+mProjWidth,mProjBottom+mProjHeight);
	}

	protected void drawRect(float border) {
		mGUI.mGraphics.drawRect(mProjLeft-border,mProjBottom-border,mProjLeft+mProjWidth+border,mProjBottom+mProjHeight+border);
	}

	@Override
	public boolean inArea(float x, float y) {
		return (x>=mPosX && x<=mPosX+mWidth && y>=mPosY && y<mPosY+mHeight);
	}

	@Override
	public GUIInteractiveRectComponent cloneSwallow(boolean ownPassesArray) {
		final GUIInteractiveRectComponent instance = (GUIInteractiveRectComponent)super.cloneSwallow(ownPassesArray);
		instance.mWidth = mWidth;
		instance.mHeight = mHeight;
		return instance;
	}

}
