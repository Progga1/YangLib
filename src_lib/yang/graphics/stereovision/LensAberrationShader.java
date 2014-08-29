package yang.graphics.stereovision;

import yang.graphics.translator.AbstractGFXLoader;

public class LensAberrationShader extends LensDistortionShader {

	public LensAberrationShader() {
		mScaleToLens = 1.66f;
	}

	@Override
	public void initHandles() {
		super.initHandles();
	}

	@Override
	protected String getFragmentShader(AbstractGFXLoader gfxLoader) {
		return gfxLoader.getShader("lens_aberration.txt");
	}

	@Override
	public void activate() {
		super.activate();
	}

}
