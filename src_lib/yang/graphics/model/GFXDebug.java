package yang.graphics.model;

import yang.events.InputState;
import yang.events.YangEventQueue;
import yang.events.eventtypes.PointerTracker;
import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.font.BitmapFont;
import yang.graphics.font.DrawableString;
import yang.graphics.font.StringProperties;
import yang.graphics.listeners.DrawListener;
import yang.graphics.textures.TextureCoordinatesQuad;
import yang.graphics.translator.GraphicsTranslator;
import yang.graphics.translator.Texture;
import yang.model.DebugYang;
import yang.model.PrintInterface;
import yang.surface.YangSurface;

public class GFXDebug implements PrintInterface {

	public static final FloatColor[] DEFAULT_PALETTE = {FloatColor.YELLOW,FloatColor.BLUE,FloatColor.GREEN,FloatColor.RED,new FloatColor(1,0.5f,0),new FloatColor(1,0,1),new FloatColor(0,1,1),new FloatColor(1,0.8f,0.4f),new FloatColor(0.5f,0.5f,1),new FloatColor(0.5f,0.5f,0.5f)};

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

	//Properties
	public int mDebugValuesMask = Integer.MAX_VALUE;
	public float mDebugOffsetX=0.025f,mDebugOffsetY=0.025f;
	public float mDebugColumnWidth=0.1f,mDebugLineHeight=0.05f;
	public FloatColor mFontColor = FloatColor.WHITE;
	public FloatColor mStateStringColor = new FloatColor(1,0.8f,0);
	public float mFontSize = 0.08f;
	public int mMinKeyChars = 10;
	public int mRefreshFPSEvery = 2;
	public Texture mPointerTexture = null;
	public boolean mDrawPointerContours = true;
	public float mPointerSize = 0.06f;
	public FloatColor[] mPointerColors = DEFAULT_PALETTE;
	public Texture mDisplayTexture;
	public float mDisplayTextureHeight = 1;
	public FloatColor mDisplayTextureColor = FloatColor.WHITE.clone();
	public boolean mDisplayTextureFlipY = false;

	//Object
	public YangSurface mSurface;
	protected GraphicsTranslator mTranslator;
	protected DefaultGraphics<?> mGraphics;

	//Strings
	public DrawableString mSpeedString;
	public DrawableString mExecMacroString;
	private DrawableString mExceptionString;
	public DrawableString mTempPrintString;
	public DrawableString mStateString;

	//State
	public long mRefreshCount = 0;

	public GFXDebug(YangSurface surface, DefaultGraphics<?> graphics,BitmapFont font) {
		mSurface = surface;
		mGraphics = graphics;
		mTranslator = mGraphics.mTranslator;

		final StringProperties properties = new StringProperties(graphics,font);
		properties.mKerningEnabled = true;
		mTempPrintString = new DrawableString(1024).setProperties(properties).setLeftTopJustified();
		mSpeedString = new DrawableString(32).setProperties(properties).setRightTopJustified();
		mExecMacroString = new DrawableString("Macro...").setProperties(properties).setRightTopJustified().setConstant();

		mStateString = new DrawableString(1600).setProperties(properties);
		mStateString.setLeftBottomJustified();
		reset();
	}

	public void reset() {
		mTempPrintString.reset();
	}

	public boolean isActive(int maskVal) {
		return (maskVal|mDebugValuesMask)!=0;
	}

	private int mPolygonCount;
	private int mDynamicPolygonCount;
	private int mBatchPolygonCount;
	private int mDrawCount;
	private int mStaticDrawCount;
	private int mTexBindCount;
	private int mShaderSwitchCount;

	public void printGFXDebugValues() {
		printDebugValue(FPS,"FPS",mTranslator.mFPS,1,true);
		printDebugValue(DRAW_COUNT,"Draw calls",mDrawCount,0,false);
		if(isActive(DRAW_DYNAMIC_STATIC_COUNT)) {
			mTempPrintString.appendString(" (");
			mTempPrintString.appendInt(mDrawCount-mStaticDrawCount);
			mTempPrintString.appendString("+");
			mTempPrintString.appendInt(mStaticDrawCount);
			mTempPrintString.appendString(")");
		}
		mTempPrintString.appendLineBreak();
		printDebugValue(POLYGON_COUNT,"Polygons",mPolygonCount,0,false);
		if(isActive(POLYGON_DYNAMIC_STATIC_COUNT)) {
			mTempPrintString.appendString(" (");
			mTempPrintString.appendInt(mDynamicPolygonCount);
			mTempPrintString.appendString("+");
			mTempPrintString.appendInt(mBatchPolygonCount);
			mTempPrintString.appendString(")");
		}
		mTempPrintString.appendLineBreak();
		printDebugValue(TEXBIND_COUNT,"Texture binds",mTexBindCount,0,true);
		printDebugValue(SHADERSWITCH_COUNT,"Shader activations",mShaderSwitchCount,0,true);
	}

	public void printDebugValue(int mask,String key,float value,int fracDigits,boolean lineBreak) {
		if((mDebugValuesMask|mask) != 0)
			printKeyValue(key,value,fracDigits,lineBreak);
	}

	public void printKeyValue(String key,float value,int fracDigits,boolean lineBreak) {
		//mString.appendStringRightJustified(key,mMinKeyChars);
		mTempPrintString.appendString(key);
		mTempPrintString.appendString(": ");
		mTempPrintString.appendFloat(value,fracDigits);
		if(lineBreak)
			mTempPrintString.appendLineBreak();
	}

	@Override
	public void print(Object s) {
		mTempPrintString.appendString(s.toString());
	}

	@Override
	public void println(Object s) {
		mTempPrintString.appendString(s.toString());
		mTempPrintString.appendLineBreak();
	}

	public void println(boolean value) {
		mTempPrintString.appendString(value?"true":"false");
		mTempPrintString.appendLineBreak();
	}

	public void draw() {
		if(DebugYang.DEBUG_LEVEL<=0 && mExceptionString==null)
			return;

		final float playSpeed = mSurface.mPlaySpeed;

		if(playSpeed==1 && mTempPrintString.mMarker==0 && (DebugYang.stateString==null || DebugYang.stateString.equals("")) && (mSurface.mMacro==null || !YangSurface.SHOW_MACRO_SIGN || mSurface.mMacro.mFinished) && mExceptionString==null && !DebugYang.DRAW_POINTERS && !DebugYang.DRAW_FPS_BAR && mDisplayTexture==null)
			return;

		final float right = mGraphics.getNormRight()-mDebugOffsetX;

		mTranslator.flush();
		final DrawListener prevDrawer = mTranslator.mCurDrawListener;
		mGraphics.activate();
		mTranslator.switchZBuffer(false);
		mGraphics.setDefaultProgram();
		mGraphics.switchGameCoordinates(false);
		mGraphics.setColor(mFontColor);
		mGraphics.resetGlobalTransform();

		if(YangSurface.SHOW_MACRO_SIGN && mSurface.mMacro!=null && !mSurface.mMacro.mFinished && ((System.currentTimeMillis()/500)%2==0)) {
			mExecMacroString.draw(right, mGraphics.getNormTop()-mDebugOffsetY-mFontSize*2, mFontSize);
		}

		if(mDisplayTexture!=null) {
			mGraphics.setColor(mDisplayTextureColor);
			float r = (float)mDisplayTexture.getWidth()/mDisplayTexture.getHeight();
			float h = mDisplayTextureHeight;
			float sy = mDisplayTextureFlipY?1:0;
			mTranslator.bindTexture(mDisplayTexture);
			mGraphics.drawRect(mGraphics.getNormRight()-r*h, -1, mGraphics.getNormRight(), -1+h, 0,sy,1,1-sy);
		}

		if(playSpeed!=1) {
			mSpeedString.reset();
			if(playSpeed==0)
				mSpeedString.appendString("0");
			else
				if(playSpeed<1) {
					mSpeedString.appendString("x");
					mSpeedString.appendInt((int)(1f/playSpeed));
				}else{
					mSpeedString.appendString("/");
					mSpeedString.appendInt((int)playSpeed);

				}
			mSpeedString.draw(right, mGraphics.getNormTop()-mDebugOffsetY, mFontSize*2f);
		}

		if(DebugYang.stateString!=null && DebugYang.stateString!="") {
			mGraphics.setColor(mStateStringColor);
			String uString = DebugYang.stateString;
			final int c = mStateString.mCapacity;
			if(uString.endsWith("\n"))
				uString = uString.substring(0,uString.length()-1);
			if(uString.length()>=c)
				uString = uString.substring(uString.length()-c, uString.length());
			mStateString.setString(uString);
			mStateString.draw(mGraphics.getNormLeft()+mDebugOffsetX, mGraphics.getNormBottom()+mDebugOffsetY, mFontSize);
		}

		if(mExceptionString==null) {
			if(mTempPrintString.mMarker!=0) {
				if(mRefreshCount%mRefreshFPSEvery==0) {
					mPolygonCount = mTranslator.mPolygonCount;
					mDynamicPolygonCount = mTranslator.mDynamicPolygonCount;
					mBatchPolygonCount = mTranslator.mBatchPolygonCount;
					mDrawCount = mTranslator.mDrawCount;
//					mDynamicDrawCount = mTranslator.mFlushCount;
					mStaticDrawCount = mTranslator.mBatchCount;
					mTexBindCount = mTranslator.mTexBindCount;
					mShaderSwitchCount = mTranslator.mShaderSwitchCount;
				}
				mRefreshCount++;
				mGraphics.setColor(mFontColor);
				mTempPrintString.draw(mGraphics.getNormLeft()+mDebugOffsetX, mGraphics.getNormTop()-mDebugOffsetY, mFontSize);
			}
		}else{
			mGraphics.setColor(FloatColor.RED);
			mExceptionString.draw(mGraphics.getNormLeft()+mDebugOffsetX, mGraphics.getNormTop()-mDebugOffsetY, mFontSize);
		}

		if(DebugYang.DRAW_POINTERS) {
			final YangEventQueue events = mSurface.mEventQueue;
			final InputState inputState = events.mInputState;
			mGraphics.mTranslator.bindTexture(mPointerTexture);
			final int l = inputState.mCurPointerDownCount;
			for(int i=0;i<inputState.mPointerTrackers.length;i++) {
				final double time = events.getTime();
				final PointerTracker tracker = inputState.mPointerTrackers[i];
				if(tracker.mLastMovementRealtime<0)
					continue;

				final float ANIM_TIME = 0.5f;
				final float x = tracker.mX;
				final float y = tracker.mY;
				float f;
				if(i<l)
					f = 2;
				else if(time-tracker.mLastMovementRealtime>ANIM_TIME)
					continue;
				else
					f = (float)(2-(time-tracker.mLastTouchRealtime)*2/ANIM_TIME);

				if(f<0.6f)
					f = 0.6f;

				final float d = f*mPointerSize*0.5f;
				if(mDrawPointerContours) {
					final float c = d*1.2f;
					mGraphics.setColor(FloatColor.BLACK);
					mGraphics.drawQuad(x,y-c, x+c,y, x-c,y, x,y+c, TextureCoordinatesQuad.FULL_TEXTURE);
				}
				mGraphics.setColor(mPointerColors[i%mPointerColors.length]);
				mGraphics.drawQuad(x,y-d, x+d,y, x-d,y, x,y+d, TextureCoordinatesQuad.FULL_TEXTURE);
			}
			assert mGraphics.mTranslator.checkErrorInst("Draw pointers");
		}


		mTranslator.bindTexture(null);

		if(DebugYang.DRAW_FPS_BAR && DebugYang.FPS_BAR_MAX_FRAMES>0) {
			float s = mTranslator.mFPS/DebugYang.FPS_BAR_MAX_FRAMES * mTranslator.mRatioX*2;
			mGraphics.setColor(DebugYang.FPS_BAR_COLOR);
			mGraphics.drawRect(-mTranslator.mRatioX,mTranslator.mRatioY-DebugYang.FPS_BAR_HEIGHT, -mTranslator.mRatioX+s,mTranslator.mRatioY);
			mGraphics.setColor(1,1,1,1);
		}

		prevDrawer.activate();
	}

	public void setErrorString(String error) {
		mExceptionString = new DrawableString(error);
		mExceptionString.setLeftTopJustified();
		mExceptionString.setConstant();
		surfaceChanged();
	}

	public void surfaceChanged() {
		if(mExceptionString!=null)
			mExceptionString.setMaxLineWidth((mGraphics.mTranslator.mRatioX*2-2*mDebugOffsetX)/mFontSize);
	}

}
