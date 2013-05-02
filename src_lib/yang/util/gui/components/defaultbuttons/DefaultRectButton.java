package yang.util.gui.components.defaultbuttons;

import yang.graphics.FloatColor;

public class DefaultRectButton extends ColoredRectButton {

	public String mCaption;
	public float[] mFontColor;
	public float mFontSize;
	public float mBorder = 0.01f;
	public float[] mBorderCl;
	
	public DefaultRectButton() {
		super();
		mFontColor = FloatColor.BLACK.createArray();
		mBorderCl = FloatColor.GRAY.createArray();
		mFontSize = 0.1f;
		mCaption = "";
	}
	
	public DefaultRectButton setFontColor(float r,float g,float b) {
		mFontColor[0] = r;
		mFontColor[1] = g;
		mFontColor[2] = b;
		return this;
	}

	public DefaultRectButton setCaption(String caption) {
		mCaption = caption;
		return this;
	}
	
	@Override
	public String propertiesToString() {
		return "caption="+mCaption+"; "+super.propertiesToString();
	}
	
	@Override
	public void draw(float offsetX,float offsetY) {
		mGraphics2D.bindTexture(null);
		mGraphics2D.setColor(mBorderCl);
		drawRect(offsetX,offsetY);
		mGraphics2D.setColor(mRGBA);
		drawRect(offsetX,offsetY,mBorder);
		mGraphics2D.setColor(mFontColor);
		mGraphics2D.drawStringLegacyC(projX(offsetX+getRelativeCenterX()), projY(offsetY+getRelativeCenterY()), mFontSize, 0, mCaption);
	}
	
}
