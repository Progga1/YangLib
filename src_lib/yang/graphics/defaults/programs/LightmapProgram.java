package yang.graphics.defaults.programs;

import yang.graphics.AbstractGFXLoader;

public class LightmapProgram extends LightProgram{

	public static final int LIGHT_TEXTURE_LEVEL = 1;
	public int mLightSamplerHandle;
	
	@Override
	protected void initHandles() {
		super.initHandles();
		mLightSamplerHandle = mProgram.getUniformLocation("lightSampler");
	}
	
	@Override
	protected String getVertexShader(AbstractGFXLoader gfxLoader) {
		return gfxLoader.getShader("lightmap_vertex");
	}

	@Override
	protected String getFragmentShader(AbstractGFXLoader gfxLoader) {
		return gfxLoader.getShader("lightmap_fragment");
	}
	
	@Override
	public void activate() {
		super.activate();
		mProgram.setUniformInt(mLightSamplerHandle, LIGHT_TEXTURE_LEVEL);
	}

}
