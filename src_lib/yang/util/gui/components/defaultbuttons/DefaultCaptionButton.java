package yang.util.gui.components.defaultbuttons;

import yang.graphics.FloatColor;
import yang.graphics.font.DrawableString;

public class DefaultCaptionButton extends ColoredRectButton {

	public DrawableString mCaption;
	public FloatColor mFontColor;
	public float mFontSize;
	
	public DefaultCaptionButton() {
		mFontColor = FloatColor.BLACK.clone();
		mFontSize = 0.1f;
		mCaption = null;
	}
	
	@Override
	public String propertiesToString() {
		return "caption="+mCaption.createRawString()+"; "+super.propertiesToString();
	}
	
	protected void drawCaption() {
		if(mCaption!=null) {
			mGraphics2D.setColor(mFontColor);
			mCaption.draw(getProjCenterX(), getProjCenterY(), mFontSize, 0);
		}
	}
	
	public DefaultCaptionButton setCaption(DrawableString caption) {
		mCaption = caption;
		return this;
	}
	
	public DefaultCaptionButton createCaption(String caption) {
		return setCaption(((DrawableString)new DrawableString().allocString(caption)).setAnchors(DrawableString.ANCHOR_CENTER, DrawableString.ANCHOR_MIDDLE));
	}
	
	@Override
	public void draw() {
		super.draw();
		drawCaption();
	}
	
}
