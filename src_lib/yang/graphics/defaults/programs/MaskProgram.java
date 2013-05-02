package yang.graphics.defaults.programs;

import yang.graphics.AbstractGFXLoader;
import yang.graphics.programs.Basic3DProgram;

public class MaskProgram extends Basic3DProgram {

	public int mOffsetHandle;
	
	@Override
	protected void initHandles() {
		mPositionHandle = mProgram.getAttributeLocation("vPosition");
		mProjHandle = mProgram.getUniformLocation("projTransform");
		mAmbientHandle = mProgram.getUniformLocation("ambientColor");
		mOffsetHandle = mProgram.getUniformLocation("offset");
	}
	
	public final static String VERTEX_SHADER = 
			"uniform mat4 projTransform;\r\n" +
			"attribute vec4 vPosition;\r\n" +
			"uniform vec4 offset;\r\n" +
			"\r\n" +
			"void main() {\r\n" +
			"	gl_Position = projTransform * vPosition + offset;\r\n" +
			"}\r\n";
	
	public final static String FRAGMENT_SHADER = 
			"#ANDROID precision mediump float;\r\n" +
			"uniform vec4 ambientColor;\r\n" +
			"\r\n" +
			"void main() {\r\n" +
			"	gl_FragColor = ambientColor;\r\n" +
			"}\r\n";
	

	@Override
	protected String getVertexShader(AbstractGFXLoader gfxLoader) {
		return VERTEX_SHADER;
	}

	@Override
	protected String getFragmentShader(AbstractGFXLoader gfxLoader) {
		return FRAGMENT_SHADER;
	}
	
	public void setZOffset(float offset) {
		//mProgram.setUniformFloat(mOffsetHandle, offset);
		mProgram.setUniform4f(mOffsetHandle, 0,0,offset,0);
	}
	
}
