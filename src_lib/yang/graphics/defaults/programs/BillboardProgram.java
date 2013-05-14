package yang.graphics.defaults.programs;

import yang.graphics.translator.AbstractGFXLoader;

public class BillboardProgram extends ShadowProgram {
	
	public int mScaleHandle;
	
	@Override
	protected void initHandles() {
		super.initHandles();
		mScaleHandle = mProgram.getUniformLocation("scale");
	}
	
	@Override
	protected String getVertexShader(AbstractGFXLoader gfxLoader) {
		return gfxLoader.getShader("shadow_billboard_vertex");
	}

	@Override
	protected String getFragmentShader(AbstractGFXLoader gfxLoader) {
		return gfxLoader.getShader("shadow_billboard_fragment");
	}

	public void setScale(float scaleX,float scaleY) {
		mProgram.setUniform2f(mScaleHandle, scaleX,scaleY*mGraphics.mRatioX);
	}
	
}
