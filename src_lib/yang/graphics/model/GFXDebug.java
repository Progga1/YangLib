package yang.graphics.model;

import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.font.BitmapFont;
import yang.graphics.font.DrawableString;
import yang.graphics.font.StringProperties;
import yang.graphics.translator.GraphicsTranslator;
import yang.model.DebugYang;
import yang.model.PrintInterface;

public class GFXDebug implements PrintInterface {

	public static int FPS = 0;
	public static int SPS = 1<<1;
	public static int POLYGON_COUNT = 1<<2;
	public static int POLYGON_DYNAMIC_STATIC_COUNT = 1<<3;
	public static int POLYGON_BATCH_COUNT = 1<<4;
	public static int DRAW_COUNT = 1<<5;
	public static int DRAW_DYNAMIC_STATIC_COUNT = 1<<6;
	public static int DRAW_BATCH_COUNT = 1<<7;
	public static int TEXBIND_COUNT = 1<<8;
	public static int SHADERSWITCH_COUNT = 1<<9;
	
	public DrawableString mString;
	public int mDebugValuesMask = Integer.MAX_VALUE;
	public float mDebugOffsetX=0.025f,mDebugOffsetY=0.025f;
	public float mDebugColumnWidth=0.1f,mDebugLineHeight=0.05f;
	public FloatColor mFontColor = FloatColor.WHITE;
	public float mFontSize = 0.1f;
	public int mMinKeyChars = 10;
	public int mRefreshEvery = 2;
	public long mRefreshCount = 0;
	
	protected GraphicsTranslator mTranslator;
	protected DefaultGraphics<?> mGraphics;
	
	public GFXDebug(DefaultGraphics<?> graphics,BitmapFont font) {
		mGraphics = graphics;
		mTranslator = mGraphics.mTranslator;
		
		StringProperties properties = new StringProperties(graphics,font);
		properties.mKerningEnabled = true;
		mString = new DrawableString(1024).setProperties(properties).setLeftTopJustified();
	
		reset();
	}
	
	public void reset() {
		mString.reset();
	}
	
	public boolean isActive(int maskVal) {
		return (maskVal|mDebugValuesMask)!=0;
	}
	
	private int mPolygonCount;
	private int mDynamicPolygonCount;
	private int mBatchPolygonCount;
	private int mDrawCount;
	private int mFlushCount;
	private int mBatchCount;
	private int mTexBindCount;
	private int mShaderSwitchCount;
	
	public void printGFXDebugValues() {
		mTranslator.flush();
		
		printDebugValue(FPS,"FPS",mTranslator.mFPS,1,true);
		printDebugValue(DRAW_COUNT,"Draw calls",mDrawCount,0,false);
		if(isActive(DRAW_DYNAMIC_STATIC_COUNT)) {
			mString.appendString(" (");
			mString.appendInt(mFlushCount);
			mString.appendString("+");
			mString.appendInt(mBatchCount);
			mString.appendString(")");
		}
		mString.appendLineBreak();
		printDebugValue(POLYGON_COUNT,"Polygons",mPolygonCount,0,false);
		if(isActive(POLYGON_DYNAMIC_STATIC_COUNT)) {
			mString.appendString(" (");
			mString.appendInt(mDynamicPolygonCount);
			mString.appendString("+");
			mString.appendInt(mBatchPolygonCount);
			mString.appendString(")");
		}
		mString.appendLineBreak();
		printDebugValue(TEXBIND_COUNT,"Texture binds",mTexBindCount,0,true);
		printDebugValue(SHADERSWITCH_COUNT,"Shader activations",mShaderSwitchCount,0,true);
	}
	
	public void printDebugValue(int mask,String key,float value,int fracDigits,boolean lineBreak) {
		if((mDebugValuesMask|mask) != 0)
			printKeyValue(key,value,fracDigits,lineBreak);
	}
	
	public void printKeyValue(String key,float value,int fracDigits,boolean lineBreak) {
		//mString.appendStringRightJustified(key,mMinKeyChars);
		mString.appendString(key);
		mString.appendString(": ");
		mString.appendFloat(value,fracDigits);
		if(lineBreak)
			mString.appendLineBreak();
	}
	
	public void print(Object s) {
		mString.appendString(s.toString());
	}
	
	public void println(Object s) {
		mString.appendString(s.toString());
		mString.appendLineBreak();
	}
	
	public void draw() {		
		if(mString.mMarker==0)
			return;
		mTranslator.flush();
		if(mRefreshCount%mRefreshEvery==0) {
			mPolygonCount = mTranslator.mPolygonCount;
			mDynamicPolygonCount = mTranslator.mPolygonCount;
			mBatchPolygonCount = mTranslator.mBatchPolygonCount;
			mDrawCount = mTranslator.mDrawCount;
			mFlushCount = mTranslator.mFlushCount;
			mBatchCount = mTranslator.mBatchCount;
			mTexBindCount = mTranslator.mTexBindCount;
			mShaderSwitchCount = mTranslator.mShaderSwitchCount;
		}
		mRefreshCount++;
		mGraphics.activate();
		mTranslator.switchZBuffer(false);
		mGraphics.setDefaultProgram();
		mGraphics.switchGameCoordinates(false);
		mGraphics.setColor(mFontColor);
		mGraphics.resetGlobalTransform();
		mString.draw(mGraphics.getScreenLeft()+mDebugOffsetX, mGraphics.getScreenTop()-mDebugOffsetY, mFontSize);
	}

}
