package yang.graphics.font;

import java.util.HashMap;

import yang.graphics.FloatColor;
import yang.graphics.defaults.DefaultGraphics;

public class StringSettings {
	
	public static StringStyle DEFAULT_STYLE;
	public static DefaultGraphics<?> DEFAULT_GRAPHICS;
	
	public DefaultGraphics<?> mGraphics;
	
	public StringStyle mStyle;
	public float mLineHeight = 1;
	public boolean mKerningEnabled = true;
	public BitmapFont mFont;
	public boolean mHasZComponent;
	public float mAdditionalSpacing = 0;
	public int mTabs = 4;
	public boolean mIgnoreSpaceAtLineStart = true;
	public boolean mAutoPutColors = true;
	public int mPosDim;
	
	public StringSettings(DefaultGraphics<?> graphics,BitmapFont font) {
		setGraphics(graphics);
		setFont(font);
		if(DEFAULT_STYLE==null) {
			DEFAULT_STYLE = new StringStyle();
			DEFAULT_STYLE.initPalette(64);
			FloatColor defCl = new FloatColor();
			defCl.mValues = graphics.mCurColor;
			DEFAULT_STYLE.addColor("DEFAULT",defCl);
			DEFAULT_STYLE.addColor("WHITE",FloatColor.WHITE.clone());
			DEFAULT_STYLE.addColor("BLACK",FloatColor.BLACK.clone());
			DEFAULT_STYLE.addColor("GRAY",FloatColor.GRAY.clone());
			DEFAULT_STYLE.addColor("RED",FloatColor.RED.clone());
			DEFAULT_STYLE.addColor("GREEN",FloatColor.GREEN.clone());
			DEFAULT_STYLE.addColor("BLUE",FloatColor.BLUE.clone());
			DEFAULT_STYLE.addColor("YELLOW",FloatColor.YELLOW.clone());
		}
		mStyle = DEFAULT_STYLE;
	}
	
	public StringSettings(BitmapFont font) {
		this(DEFAULT_GRAPHICS,font);
	}
	
	public StringSettings setGraphics(DefaultGraphics<?> graphics) {
		if(DEFAULT_GRAPHICS==null)
			DEFAULT_GRAPHICS = graphics;
		mGraphics = graphics;
		mPosDim = graphics.mPositionDimension;
		if(mGraphics!=null)
			mHasZComponent = mGraphics.mPositionDimension==3;
		return this;
	}
	
	public StringSettings setFont(BitmapFont font) {
		mFont = font;
		return this;
	}
	
	public StringSettings clone(boolean clonePalette) {
		StringSettings result = new StringSettings(mGraphics,mFont);
		result.mLineHeight = mLineHeight;
		result.mKerningEnabled = mKerningEnabled;
		result.mAdditionalSpacing = mAdditionalSpacing;
		result.mTabs = mTabs;
		result.mIgnoreSpaceAtLineStart = mIgnoreSpaceAtLineStart;
		result.mAutoPutColors = mAutoPutColors;
		if(clonePalette){
			result.mStyle = mStyle.clone();
		}else
			result.mStyle = mStyle;
		return result;
	}

	@Override
	public StringSettings clone() {
		return clone(false);
	}

	public FloatColor getColorByKey(String key) {
		return mStyle.getColorByKey(key);
	}

	public FloatColor getColor(int index) {
		return mStyle.mPalette[index];
	}
}
