package yang.graphics.defaults.programs;

import yang.graphics.AbstractGFXLoader;
import yang.graphics.programs.BasicProgram;

public class AdditiveModulateProgram extends BasicProgram{
	
	public final static String VERTEX_SHADER = 
			"uniform mat4 projTransform;\n" +
			"uniform float time;\n" +
			"attribute vec4 vPosition;\n" +
			"attribute vec2 vTexture;\n" +
			"attribute vec4 vColor;\n" +
			"attribute vec4 vAddColor;" +
			"varying vec2 texCoord;\n" +
			"varying vec4 color;\n" +
			"varying vec4 addColor;\n" +
			"\n" +
			"void main() {\n" +
			"	gl_Position = projTransform * vPosition;\n" +
			"	texCoord = vTexture;\n" +
			"	color = vColor;\n" +
			"	addColor = vAddColor;\n" +
			"}\n";
	
	public final static String FRAGMENT_SHADER = 
			"#ANDROID precision mediump float;\n" +
			"uniform sampler2D texSampler;\n" +
			"uniform vec4 ambientColor;\n" +
			"varying vec2 texCoord;\n" +
			"varying vec4 color;\n" +
			"varying vec4 addColor;\n" +
			"\n" +
			"void main() {\n" +
			"	gl_FragColor = (texture2D(texSampler, vec2(texCoord.x,texCoord.y)) * color) * ambientColor;\n" +
			"}\n";
	
	@Override
	protected String getSuppDataIdentifier() {
		return "vAddColor";
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