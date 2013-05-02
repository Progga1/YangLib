package yang.graphics.defaults.programs;

import yang.graphics.AbstractGFXLoader;
import yang.graphics.programs.BasicProgram;

public class OnlyTextureProgram extends BasicProgram{
	
	public final static String VERTEX_SHADER = 
			"uniform mat4 projTransform;\r\n" +
			"uniform float time;\r\n" +
			"uniform vec4 ambientColor;\r\n" +
			"attribute vec4 vPosition;\r\n" +
			"attribute vec2 vTexture;\r\n" +
			"\r\n" +
			"varying vec2 texCoord;\r\n" +
			"\r\n" +
			"void main() {\r\n" +
			"	gl_Position = projTransform * vPosition;\r\n" +
			"	texCoord = vTexture;\r\n" +
			"}\r\n";
	
	public final static String FRAGMENT_SHADER = 
			"#ANDROID precision mediump float;\r\n" +
			"uniform sampler2D texSampler;\r\n" +
			"varying vec2 texCoord;\r\n" +
			"\r\n" +
			"void main() {\r\n" +
			"	gl_FragColor = texture2D(texSampler, vec2(texCoord.x,texCoord.y));\r\n" +
			"}\r\n";
	
	@Override
	protected String getVertexShader(AbstractGFXLoader gfxLoader) {
		return VERTEX_SHADER;
	}

	@Override
	protected String getFragmentShader(AbstractGFXLoader gfxLoader) {
		return FRAGMENT_SHADER;
	}

}