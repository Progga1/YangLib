package yang.samples.statesystem;

import yang.graphics.font.DrawableString;
import yang.graphics.font.StringProperties;
import yang.graphics.translator.Texture;
import yang.model.DebugYang;
import yang.model.enums.UpdateMode;
import yang.systemdependent.YangSensor;
import yang.util.statesystem.YangProgramStateSystem;

public class SampleStateSystem extends YangProgramStateSystem {
	
	public SampleMainMenu mMainMenu;
	
	public Texture mCircleTexture;
	
	public SampleStateSystem() {
		super(true, true);
		super.setUpdateMode(UpdateMode.SYNCHRONOUS);
		if(isStereoVision())
			DebugYang.DRAW_POINTERS = true;
	}
	
	public void postInitGraphics() {
		super.mSensor.startSensor(YangSensor.TYPE_ROTATION_VECTOR);
		super.mSensor.startSensor(YangSensor.TYPE_GRAVITY);
		DrawableString.DEFAULT_PROPERTIES = new StringProperties(mGraphics2D,mGFXLoader.loadFont("arial"));
		super.initDebugOutput(DrawableString.DEFAULT_PROPERTIES.mFont);
		mMainMenu = (SampleMainMenu)new SampleMainMenu().init(this);
		mCircleTexture = mGFXLoader.getImage("circle");
		super.setState(mMainMenu);
	}

}
