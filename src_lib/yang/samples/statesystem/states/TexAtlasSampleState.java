package yang.samples.statesystem.states;

import yang.graphics.textures.TextureData;
import yang.graphics.textures.TextureSettings;
import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.textures.enums.TextureWrap;
import yang.graphics.translator.Texture;
import yang.samples.statesystem.SampleState;

public class TexAtlasSampleState extends SampleState {

	private Texture mAtlasTex;
	
	@Override
	public void initGraphics() {
		TextureData atlasData = mGFXLoader.loadImageData("atlas");
		atlasData.createBiasBorder(4, 4, 120, 120, 4, TextureWrap.REPEAT, TextureWrap.REPEAT);
		atlasData.createBiasBorder(132, 4, 120, 120, 4, TextureWrap.CLAMP, TextureWrap.CLAMP);
		atlasData.createBiasBorder(4, 132, 120, 120, 4, TextureWrap.MIRROR, TextureWrap.MIRROR);
		atlasData.createBiasBorder(132, 132, 120, 120, 4, TextureWrap.REPEAT, TextureWrap.CLAMP);
		mAtlasTex = mGraphics.createTexture(atlasData, new TextureSettings(TextureFilter.LINEAR_MIP_LINEAR));
	}
	
	@Override
	protected void step(float deltaTime) {
		
	}

	@Override
	protected void draw() {
		mGraphics.clear(0,0,0);
		mGraphics2D.activate();
		mGraphics.bindTexture(mAtlasTex);
		mGraphics2D.setWhite();
		mGraphics2D.drawRect(mGraphics2D.getScreenRight()-0.7f,mGraphics2D.getScreenTop()-0.7f,mGraphics2D.getScreenRight(),mGraphics2D.getScreenTop());
	}

}
