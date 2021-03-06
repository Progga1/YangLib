package yang.util.gui.components.defaultdrawers;

import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.font.DrawableAnchoredLines;
import yang.graphics.font.DrawableString;
import yang.graphics.font.StringProperties;
import yang.graphics.model.FloatColor;
import yang.util.Util;
import yang.util.gui.components.GUIComponentDrawPass;
import yang.util.gui.components.GUIMultipassComponent;

public class GUICaptionDrawer extends GUIComponentDrawPass<GUIMultipassComponent> {

	public DrawableString mCaption;
	public FloatColor mFontColor;
	public FloatColor mDisabledFontColor;
	public float mFontSize = 0.1f;
	public int mColorId = -1;
	public float mComponentAnchor = DrawableString.ANCHOR_CENTER;

	public GUICaptionDrawer() {
		mFontColor = FloatColor.BLACK.clone();
		mDisabledFontColor = FloatColor.GRAY.clone();
	}

	@Override
	public void draw(DefaultGraphics<?> graphics, GUIMultipassComponent component) {
		if(mCaption!=null) {
			//mCaption.mShiftZ = 0.02f;
			if(mColorId>=0 && component.mColors!=null)
				graphics.setColor(component.mColors[mColorId]);
			else
				graphics.setColor(component.mEnabled?mFontColor:mDisabledFontColor);
			mCaption.draw(component.mProjLeft+mComponentAnchor*component.getProjWidth(), component.getProjCenterY(), mFontSize);
		}
	}

	@Override
	public String toString() {
		return "caption="+mCaption.createRawString()+"; fontColor="+Util.arrayToString(mFontColor.mValues,",",0);
	}

	public GUICaptionDrawer setCaption(DrawableString caption) {
		mCaption = caption;
		return this;
	}

	public GUICaptionDrawer createCaption(String caption) {
		mCaption = new DrawableAnchoredLines(caption).setAnchors(DrawableString.ANCHOR_CENTER, DrawableString.ANCHOR_MIDDLE);
		mCaption.setConstant();
		return this;
	}

	public GUICaptionDrawer createCaption(String caption, StringProperties properties) {
		mCaption = new DrawableAnchoredLines(caption).setAnchors(DrawableString.ANCHOR_CENTER, DrawableString.ANCHOR_MIDDLE);
		mCaption.mProperties = properties;
		return this;
	}

	public GUICaptionDrawer setAnchors(float horizontalAnchor,float verticalAnchor) {
		mCaption.setAnchors(horizontalAnchor,verticalAnchor);
		return this;
	}

	public void setComponentAnchor(float anchor) {
		mComponentAnchor = anchor;
	}

}
