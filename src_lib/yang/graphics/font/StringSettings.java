package yang.graphics.font;

import yang.graphics.defaults.DefaultGraphics;

public class StringSettings {
	
	public static DefaultGraphics<?> DEFAULT_GRAPHICS;
	
	public DefaultGraphics<?> mGraphics;
	
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
	
	@Override
	public StringSettings clone() {
		StringSettings result = new StringSettings(mGraphics,mFont);
		result.mLineHeight = mLineHeight;
		result.mKerningEnabled = mKerningEnabled;
		result.mAdditionalSpacing = mAdditionalSpacing;
		result.mTabs = mTabs;
		result.mIgnoreSpaceAtLineStart = mIgnoreSpaceAtLineStart;
		result.mAutoPutColors = mAutoPutColors;
		return result;
	}
	
}
