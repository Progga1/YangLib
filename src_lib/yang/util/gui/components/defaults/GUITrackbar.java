package yang.util.gui.components.defaults;

import yang.events.eventtypes.YangPointerEvent;
import yang.graphics.font.DrawableString;
import yang.graphics.font.StringProperties;
import yang.graphics.model.FloatColor;
import yang.util.gui.GUIPointerEvent;
import yang.util.gui.components.GUIInteractiveRectComponent;
import yang.util.gui.components.defaultdrawers.GUICaptionDrawer;
import yang.util.gui.components.defaultdrawers.GUIOutlineDrawer;
import yang.util.gui.components.defaultdrawers.GUITrackbarDrawer;

public class GUITrackbar extends GUIInteractiveRectComponent {

	private final DrawableString mCaption;
	protected final GUICaptionDrawer mCaptionDrawer;
	public float mMinValue = 0;
	public float mMaxValue = 1;
	public int mFracDigits = 0;
	public float mValue;
	//public static GUINinePatchDrawer ninePatchDrawer = MasterGUI.createNinePatchDrawer(0.5f,0,0.5f,0.5f);

	protected void preparePasses() {
		final GUITrackbarDrawer trackbar = new GUITrackbarDrawer();
		trackbar.mBGColor = new FloatColor(0.5f);
		setPasses(trackbar,null,mCaptionDrawer,new GUIOutlineDrawer());
	}

	public GUITrackbar(float minValue,float maxValue,int fracDigits,String formatString,StringProperties captionProperties) {
		mMinValue = minValue;
		mMaxValue = maxValue;
		mFracDigits = fracDigits;
		mCaptionDrawer = new GUICaptionDrawer();
		mCaptionDrawer.mCaption = (DrawableString)new DrawableString().setProperties(captionProperties).allocFormatString(formatString);
		mCaptionDrawer.mFontColor = FloatColor.WHITE.clone();
		mCaption = mCaptionDrawer.mCaption;
		setValue((mMinValue+mMaxValue)*0.5f);

		preparePasses();
	}

	@Override
	public boolean isPressable() {
		return true;
	}

	public void setValue(float value) {
		if(value<mMinValue)
			value = mMinValue;
		if(value>mMaxValue)
			value = mMaxValue;
		mValue = value;
		mCaption.appendFloatAtMark(0, value, mFracDigits);
	}

	public float getNormValue() {
		return (mValue-mMinValue)/(mMaxValue-mMinValue);
	}

	public void setNormValue(float val) {
		setValue(mMinValue + (val)*(mMaxValue-mMinValue));
	}

	@Override
	public void guiPointerDown(float x,float y,GUIPointerEvent event) {
		if(event.mButton==YangPointerEvent.BUTTON_LEFT)
			setNormValue(event.mX/mWidth);
	}

	@Override
	public void guiFocusedDrag(GUIPointerEvent event) {
		if(event.mButton==YangPointerEvent.BUTTON_LEFT)
			setNormValue(event.mX/mWidth);
	}

}
