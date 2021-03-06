package yang.samples.statesystem;

import yang.graphics.font.DrawableString;
import yang.graphics.font.StringProperties;
import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.translator.Texture;
import yang.model.DebugYang;
import yang.model.enums.UpdateMode;
import yang.util.statesystem.YangProgramStateSystem;

public class SampleStateSystem extends YangProgramStateSystem {

	public SampleMainMenu mMainMenu;

	public Texture mCircleTexture;
	public Texture mCubeTexture;

	public SampleStateSystem() {
		super(true, true);

		super.setUpdateMode(UpdateMode.SYNCHRONOUS);
		if(isStereoVision())
			DebugYang.DRAW_POINTERS = true;
		super.mExitOnEsc = false;
		super.mAutoApplySensorToCamera = true;
	}

	@Override
	public void postInitGraphics() {
		mGraphics.setMaxFPS(0);
		DrawableString.DEFAULT_PROPERTIES = new StringProperties(mGraphics2D,mGFXLoader.loadFont("arial"));
		super.initDebugOutput(DrawableString.DEFAULT_PROPERTIES.mFont);
		mMainMenu = (SampleMainMenu)new SampleMainMenu().init(this);
		mCircleTexture = mGFXLoader.getImage("circle");
		mCubeTexture = mGFXLoader.getImage("cube",TextureFilter.LINEAR_MIP_LINEAR);
		super.setState(mMainMenu);
	}

}
