package yang.samples.statesystem.states;

import yang.graphics.font.DrawableAnchoredLines;
import yang.graphics.font.DrawableString;
import yang.graphics.font.defaultanimatedfonts.RotatingLettersString;
import yang.samples.statesystem.SampleState;

public class StringSampleState extends SampleState {

	DrawableString mSimpleString;
	DrawableString mNumberString;
	DrawableString mFormatString;
	RotatingLettersString mAnimatedString;
	DrawableString mFromStringXML;
	DrawableString mAutoLineBreakString;
	float mMaxLineWidth = 10;
	
	@Override
	protected void initGraphics() {
		//Create strings
		mSimpleString = new DrawableString("A string");
		mSimpleString.setHorizontalAnchor(DrawableString.ANCHOR_CENTER).setConstant();
		
		mFromStringXML = mStrings.createDrawableString("testText",DrawableString.ANCHOR_CENTER);
		
		mNumberString = new DrawableString(16);		//16 = maximum char count
		mNumberString.setAnchors(DrawableString.ANCHOR_RIGHT,DrawableString.ANCHOR_TOP);
		mNumberString.setSettings(DrawableString.DEFAULT_SETTINGS.clone());
		mNumberString.mSettings.mKerningEnabled = false;
		
		mFormatString = new DrawableString();
		mFormatString.allocFormatString("A number: %6\nThe word '%8'");
		
		mAnimatedString = new RotatingLettersString(3.2f,0.8f,0.36f);
		mAnimatedString.allocString("Animated");
		
		mAutoLineBreakString = new DrawableAnchoredLines();
		mAutoLineBreakString.allocString("A long Text with auto´matic line breaks..\nas well as manual line breaks.");
	}
	
	@Override
	public void step(float deltaTime) {
		
	}

	@Override
	public void draw() {

		mGraphics.clear(0,0,0.14f);
		mGraphics2D.switchGameCoordinates(false);
		
		//Update strings
		mNumberString.setFloat(mStateTimer, 2);
		
		mFormatString.appendIntAtMark(0, 312);
		mFormatString.appendStringAtMark(1, "Hello");
		
		//Draw strings with given positions, scale and rotation
		mSimpleString.draw(-0.8f, 0.6f, 0.15f);
		mGraphics2D.setColor(1, 0.8f, 0);
		mFromStringXML.draw(-0.2f, 0.3f, 0.2f, 0.3f);
		mGraphics2D.setColor(0,0.7f,0);
		mNumberString.draw(mGraphics2D.getScreenRight(),mGraphics2D.getScreenTop(), 0.2f);
		mGraphics2D.setColor(1,0,0);
		mAnimatedString.draw(-1.2f, -0.3f, 0.2f, -0.3f);
		mGraphics2D.setWhite();
		mFormatString.draw(-0.3f,-0.5f, 0.2f);
		
		//Draw text with automatic line break
		final float FONT_SIZE = 0.07f;
		final float LEFT = 0.6f;
		final float TOP = 0.5f;
		mGraphics2D.setColor(0.8f);
		mMaxLineWidth = (float)Math.sin(mStateTimer*0.5f)*4.2f+8.5f;
		mAutoLineBreakString.setMaxLineWidth(mMaxLineWidth);
		mAutoLineBreakString.draw(LEFT, TOP, FONT_SIZE);
		mAutoLineBreakString.setHorizontalAnchor(DrawableString.ANCHOR_LEFT);
		//Show line break bar
		mGraphics.bindTexture(null);
		mGraphics2D.setColor(0.8f);
		mGraphics2D.drawLine(LEFT+0.01f+mMaxLineWidth*FONT_SIZE, TOP, LEFT+0.01f+mMaxLineWidth*FONT_SIZE, TOP-mAutoLineBreakString.mRecentStringHeight*FONT_SIZE, 0.02f);
	}
	
}
