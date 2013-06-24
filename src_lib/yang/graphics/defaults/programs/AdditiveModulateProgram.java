package yang.graphics.defaults.programs;

import yang.graphics.programs.BasicProgram;
import yang.graphics.translator.AbstractGFXLoader;

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
			"   color = vec4(color.r*color.a,color.g*color.a,color.b*color.a,color.a);\n" +
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
			"#NOPREMULT gl_FragColor = texture2D(texSampler, vec2(texCoord.x,texCoord.y)) * color + addColor;\n" +
			"#PREMULT vec4 texCl = texture2D(texSampler, vec2(texCoord.x,texCoord.y));\n" +
			"#PREMULT gl_FragColor = (texCl * color + addColor*texCl.a) * ambientColor;\n" +
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