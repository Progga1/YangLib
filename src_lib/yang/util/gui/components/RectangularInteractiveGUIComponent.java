package yang.util.gui.components;


public abstract class RectangularInteractiveGUIComponent extends InteractiveGUIComponent {

	public float mWidth,mHeight;

	public RectangularInteractiveGUIComponent(float width,float height) {
		super();
		mLeft = 0;
		mTop = 0;
		mWidth = width;
		mHeight = height;
	}
	
	public RectangularInteractiveGUIComponent setBounds(float left,float top,float right,float bottom) {
		mLeft = left;
		mTop = top;
		mWidth = right-left;
		mHeight = bottom-top;
		return this;
	}
	
	public RectangularInteractiveGUIComponent setBounds(RectangularInteractiveGUIComponent preface) {
		mLeft = preface.mLeft;
		mTop = preface.mTop;
		mWidth = preface.mWidth;
		mHeight = preface.mHeight;
		return this;
	}
	
	protected void drawRect(float offsetX,float offsetY) {
		mGUI.mGraphics2D.drawRect(projX(offsetX+mLeft),projY(offsetY+mTop+mHeight),projX(offsetX+mLeft+mWidth),projY(offsetY+mTop));
	}
	
	protected void drawRect(float offsetX,float offsetY,float border) {
		mGUI.mGraphics2D.drawRect(projX(offsetX+mLeft+border),projY(offsetY+mTop+mHeight-border),projX(offsetX+mLeft+mWidth-border),projY(offsetY+mTop+border));
	}
	
	public RectangularInteractiveGUIComponent setPosAndDimCentered(float centerX, float centerY, float width, float height) {
		return setBounds(centerX-width*0.5f,centerY-height*0.5f,centerX+width*0.5f,centerY+height*0.5f);
	}
	
	public RectangularInteractiveGUIComponent setPosAndExtends(float left, float top, float width, float height) {
		mLeft = left;
		mTop = top;
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
		return (x>=mLeft && x<=mLeft+mWidth && y>=mTop && y<mTop+mHeight);
	}
}
