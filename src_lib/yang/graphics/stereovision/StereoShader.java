package yang.graphics.stereovision;

import yang.graphics.programs.AbstractProgram;
import yang.graphics.translator.AbstractGFXLoader;

public class StereoShader extends AbstractProgram {

	public int mTexSamplerHandle;
	public int mPositionHandle;
	public int mTexCoordsHandle;
	
	public final static String VERTEX_SHADER = 
			"attribute vec4 vPosition;\r\n" +
			"attribute vec2 vTexture;\r\n" +	
			"varying vec2 texCoord;\r\n" +
			"\r\n" +
			"void main() {\r\n" +
			"	gl_Position = vPosition;\r\n" +
			"	texCoord = vTexture;\r\n" +
			"}\r\n";
	
	public final static String FRAGMENT_SHADER = 
			"#ANDROID precision mediump float;\r\n" +
			"uniform sampler2D texSampler;\r\n" +
			"varying vec2 texCoord;\r\n" +
			"\r\n" +
			"void main() {\r\n"+
			"	gl_FragColor = texture2D(texSampler, texCoord);\r\n" +
			"}\r\n";
	
	@Override
	protected void initHandles() {
		mTexSamplerHandle = mProgram.getUniformLocation("texSampler");
		mPositionHandle = mProgram.getAttributeLocation("vPosition");
		mTexCoordsHandle = mProgram.getAttributeLocation("vTexture");
	}
	
	@Override
	protected String getVertexShader(AbstractGFXLoader gfxLoader) {
		return VERTEX_SHADER;
	}

	@Override
	protected String getFragmentShader(AbstractGFXLoader gfxLoader) {
		return FRAGMENT_SHADER;
	}

}
