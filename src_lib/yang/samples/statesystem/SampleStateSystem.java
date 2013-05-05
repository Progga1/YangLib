package yang.samples.statesystem;

import yang.graphics.font.DrawableString;
import yang.util.statesystem.YangProgramStateSystem;

public class SampleStateSystem extends YangProgramStateSystem {
	
	public SampleStateSystem() {
		super(true, true);
	}
	
	public void postInitGraphics() {
		DrawableString.DEFAULT_FONT = mGFXLoader.loadFont("default");
		SampleGUIMenu mainMenu = (SampleGUIMenu)new SampleGUIMenu().init(this);
		super.setState(mainMenu);
	}
	
	public void draw() {
		update();
		super.draw();
	}

}
