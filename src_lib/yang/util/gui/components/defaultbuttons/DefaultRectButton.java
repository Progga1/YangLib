package yang.util.gui.components.defaultbuttons;

import yang.graphics.FloatColor;
import yang.graphics.font.DrawableString;

public class DefaultRectButton extends ColoredRectButton {

	public DrawableString mCaption;
	public FloatColor mFontColor;
	public float mFontSize;
	public float mBorder = 0.01f;
	public FloatColor mBorderColor;
	
	public DefaultRectButton() {
		super();
		mFontColor = FloatColor.BLACK.clone();
		mBorderColor = FloatColor.GRAY.clone();
		mFontSize = 0.1f;
		mCaption = null;
	}

	public DefaultRectButton setCaption(DrawableString caption) {
		mCaption = caption;
		return this;
	}
	
	public DefaultRectButton createCaption(String caption) {
		return setCaption(((DrawableString)new DrawableString().allocString(caption)).setAnchors(DrawableString.ANCHOR_CENTER, DrawableString.ANCHOR_MIDDLE));
	}
	
	@Override
	public String propertiesToString() {
		return "caption="+mCaption.createRawString()+"; "+super.propertiesToString();
	}
	
	@Override
	public void draw() {
		mGraphics2D.bindTexture(null);
		mGraphics2D.setColor(mBorderColor);
		drawRect();
		mGraphics2D.setColor(mRGBA);
		drawRect(-mBorder);
		if(mCaption!=null) {
			mGraphics2D.setColor(mFontColor);
			mCaption.draw(getProjCenterX(), getProjCenterY(), mFontSize, 0);
		}
	}

	public DrawableString getCaption() {
		return mCaption;
	}
	
}
