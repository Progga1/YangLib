package yang.samples.statesystem;

import yang.graphics.font.DrawableString;
import yang.graphics.translator.Texture;
import yang.util.statesystem.YangProgramStateSystem;

public class SampleStateSystem extends YangProgramStateSystem {
	
	public SampleMainMenu mMainMenu;
	
	public Texture mCircleTexture;
	
	public SampleStateSystem() {
		super(true, true);
	}
	
	public void postInitGraphics() {
		DrawableString.DEFAULT_FONT = mGFXLoader.loadFont("arial");
		mMainMenu = (SampleMainMenu)new SampleMainMenu().init(this);
		mCircleTexture = mGFXLoader.getImage("circle");
		super.setState(mMainMenu);
	}
	
	public void draw() {
		update();
		super.draw();
	}

}
