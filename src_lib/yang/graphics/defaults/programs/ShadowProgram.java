package yang.graphics.defaults.programs;

import yang.graphics.translator.AbstractGFXLoader;

public class ShadowProgram extends LightProgram implements ShadowInterface{
	
	public int mDepthTexHandle;
	public int mDepthMapTransformHandle;
	
	@Override
	protected String getVertexShader(AbstractGFXLoader gfxLoader) {
		return gfxLoader.getShader("shadow_vertex");
	}

	@Override
	protected String getFragmentShader(AbstractGFXLoader gfxLoader) {
		return gfxLoader.getShader("shadow_fragment");
	}

	@Override
	protected void initHandles() {
		super.initHandles();
		mDepthTexHandle = mProgram.getUniformLocation("depthSampler");
		mDepthMapTransformHandle = mProgram.getUniformLocation("depthMapTransform");
	}
	
	@Override
	public void activate() {
		super.activate();
		mProgram.setUniformInt(mDepthTexHandle, DEPTH_TEXTURE_LEVEL);
	}
	
	public void setDepthMapProjection(float[] depthTransformMatrix) {
		mProgram.setUniformMatrix(mDepthMapTransformHandle, depthTransformMatrix);
	}
	
}
