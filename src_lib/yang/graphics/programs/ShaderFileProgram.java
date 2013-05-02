package yang.graphics.programs;

import yang.graphics.AbstractGFXLoader;

public class ShaderFileProgram extends BasicProgram {

	protected String mVertexShaderFilename;
	protected String mFragmentShaderFilename;
	
	public ShaderFileProgram(String vertexShaderFilename,String fragmentShaderFilename) {
		mVertexShaderFilename = vertexShaderFilename;
		mFragmentShaderFilename = fragmentShaderFilename;
	}
	
	@Override
	protected String getVertexShader(AbstractGFXLoader gfxLoader) {
		return gfxLoader.getShader(mVertexShaderFilename);
	}

	@Override
	protected String getFragmentShader(AbstractGFXLoader gfxLoader) {
		return gfxLoader.getShader(mFragmentShaderFilename);
	}

}
