package yang.util.gui.components;


public abstract class RectangularInteractiveGUIComponent extends InteractiveGUIComponent {

	public float mWidth,mHeight;
	public float mProjWidth,mProjHeight;

	public RectangularInteractiveGUIComponent(float width,float height) {
		super();
		mPosX = 0;
		mPosY = 0;
		mWidth = width;
		mHeight = height;
	}
	
	@Override
	public void refreshProjections(float offsetX,float offsetY) {
		mProjLeft = mGUI.mProjShiftX+(mPosX+offsetX)*mGUI.mProjXFactor;
		mProjBottom = mGUI.mProjShiftY+(mPosY+offsetY)*mGUI.mProjYFactor+mHeight*mGUI.mProjShiftYFactor;
		mProjWidth = mWidth*mGUI.mProjWidthFactor;
		mProjHeight = mHeight*mGUI.mProjHeightFactor;
	}
	
	public RectangularInteractiveGUIComponent setBounds(float left,float top,float right,float bottom) {
		mPosX = left;
		mPosY = top;
		mWidth = right-left;
		mHeight = bottom-top;
		return this;
	}
	
	public RectangularInteractiveGUIComponent setBounds(RectangularInteractiveGUIComponent preface) {
		mPosX = preface.mPosX;
		mPosY = preface.mPosY;
		mWidth = preface.mWidth;
		mHeight = preface.mHeight;
		return this;
	}
	
	protected void drawRect(float offsetX,float offsetY) {
		mGUI.mGraphics2D.drawRect(projX(offsetX+mPosX),projY(offsetY+mPosY+mHeight),projX(offsetX+mPosX+mWidth),projY(offsetY+mPosY));
	}
	
	protected void drawRect(float offsetX,float offsetY,float border) {
		mGUI.mGraphics2D.drawRect(projX(offsetX+mPosX+border),projY(offsetY+mPosY+mHeight-border),projX(offsetX+mPosX+mWidth-border),projY(offsetY+mPosY+border));
	}
	
	public RectangularInteractiveGUIComponent setPosAndDimCentered(float centerX, float centerY, float width, float height) {
		return setBounds(centerX-width*0.5f,centerY-height*0.5f,centerX+width*0.5f,centerY+height*0.5f);
	}
	
	public RectangularInteractiveGUIComponent setPosAndExtends(float left, float top, float width, float height) {
		mPosX = left;
		mPosY = top;
		mWidth = width;
		mHeight = height;
		return this;
	}
	
	public RectangularInteractiveGUIComponent setExtends(float width,float height) {
		mWidth = width;
		mHeight = height;
		return this;
	}

	@Override
	public boolean inArea(float x, float y) {
		return (x>=mPosX && x<=mPosX+mWidth && y>=mPosY && y<mPosY+mHeight);
	}
}
