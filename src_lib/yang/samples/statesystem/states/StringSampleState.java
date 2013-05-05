package yang.samples.statesystem.states;

import yang.graphics.font.DrawableString;
import yang.graphics.font.defaultanimatedfonts.RotatingLettersString;
import yang.util.statesystem.YangProgramState;
import yang.util.statesystem.YangProgramStateSystem;

public class StringSampleState extends YangProgramState<YangProgramStateSystem> {

	DrawableString mSimpleString;
	DrawableString mNumberString;
	DrawableString mFormatString;
	RotatingLettersString mAnimatedString;
	DrawableString mFromStringXML;
	
	@Override
	protected void initGraphics() {
		//Create strings
		mSimpleString = new DrawableString("A string");
		mSimpleString.setHorizontalAnchor(DrawableString.ANCHOR_CENTER).setConstant();
		
		mFromStringXML = mStrings.createDrawableString("testText",DrawableString.ANCHOR_CENTER);
		
		mNumberString = new DrawableString(16);		//16 = maximum char count
		mNumberString.setAnchors(DrawableString.ANCHOR_RIGHT,DrawableString.ANCHOR_TOP);
		mNumberString.mKerningEnabled = false;
		
		mFormatString = new DrawableString();
		mFormatString.allocFormatString("A number: %6\nThe word '%8'");
		
		mAnimatedString = new RotatingLettersString(3.2f,0.8f,0.36f);
		mAnimatedString.allocString("Animated");
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
	}
	
}
