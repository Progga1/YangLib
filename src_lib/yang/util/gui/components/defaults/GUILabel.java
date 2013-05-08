package yang.util.gui.components.defaults;

import yang.graphics.FloatColor;
import yang.graphics.font.DrawableAnchoredLines;
import yang.graphics.font.DrawableString;
import yang.util.gui.components.GUIComponent;

public class GUILabel extends GUIComponent {

	public float mFontSize = 0.1f;
	public DrawableString mCaption = null;
	public FloatColor mFontColor;
	
	public GUILabel() {
		mFontColor = FloatColor.BLACK.clone();
	}
	
	public GUILabel(DrawableString caption) {
		this();
		mCaption = caption;
	}
	
	public GUILabel createCaption(String caption) {
		mCaption = new DrawableAnchoredLines(caption);
		return this;
	}
	
	public void draw() {
		if(mCaption!=null) {
			mGUI.mGraphics2D.setColor(mFontColor);
			mCaption.draw(mProjLeft, mProjBottom, mFontSize, 0);
		}
	}

	public GUILabel setAnchors(float horizontalAnchor,float verticalAnchor) {
		mCaption.setAnchors(horizontalAnchor,verticalAnchor);
		return this;
	}
	
}
