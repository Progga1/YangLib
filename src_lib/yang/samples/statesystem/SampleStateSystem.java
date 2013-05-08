package yang.samples.statesystem;

import yang.graphics.font.DrawableString;
import yang.util.statesystem.YangProgramStateSystem;

public class SampleStateSystem extends YangProgramStateSystem {
	
	public SampleGUIMenu mMainMenu;
	
	public SampleStateSystem() {
		super(true, true);
	}
	
	public void postInitGraphics() {
		DrawableString.DEFAULT_FONT = mGFXLoader.loadFont("default");
		mMainMenu = (SampleGUIMenu)new SampleGUIMenu().init(this);
		super.setState(mMainMenu);
	}
	
	public void draw() {
		update();
		super.draw();
	}

}
