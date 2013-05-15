package yang.util.gui.components.defaultbuttons;

import yang.graphics.FloatColor;
import yang.graphics.font.DrawableString;

public class DefaultRectButton extends DefaultCaptionButton {


	public float mBorder = 0.01f;
	public FloatColor mBorderColor;
	
	public DefaultRectButton() {
		super();
		mBorderColor = FloatColor.GRAY.clone();
	}
	
	@Override
	public void draw() {
		mGraphics2D.bindTexture(null);
		mGraphics2D.setColor(mBorderColor);
		drawRect();
		mGraphics2D.setColor(mColor);
		drawRect(-mBorder);
		drawCaption();
	}
	
}
