package yang.samples.statesystem;

import yang.graphics.font.DrawableString;
import yang.graphics.font.StringProperties;
import yang.graphics.translator.Texture;
import yang.model.enums.UpdateMode;
import yang.util.statesystem.YangProgramStateSystem;

public class SampleStateSystem extends YangProgramStateSystem {
	
	public SampleMainMenu mMainMenu;
	
	public Texture mCircleTexture;
	
	public SampleStateSystem() {
		super(true, true);
		super.setUpdateMode(UpdateMode.ASYNCHRONOUS);
	}
	
	public void postInitGraphics() {
		DrawableString.DEFAULT_PROPERTIES = new StringProperties(mGraphics2D,mGFXLoader.loadFont("arial"));
		super.initDebugOutput(DrawableString.DEFAULT_PROPERTIES.mFont);
		mMainMenu = (SampleMainMenu)new SampleMainMenu().init(this);
		mCircleTexture = mGFXLoader.getImage("circle");
		super.setState(mMainMenu);
	}

}
