package yang.graphics.programs;

import yang.graphics.translator.AbstractGFXLoader;

public class MinimalProgram extends BasicProgram {

	public final static String MINIMAL_VERTEX_SHADER =
			"attribute \\POSITIONP vec4 vPosition;\r\n" +
			"\r\n" +
			"void main() {\r\n" +
			"	gl_Position = vPosition;\r\n" +
			"}\r\n";

	public final static String MINIMAL_FRAGMENT_SHADER =
			"void main() {\r\n" +
			"	gl_FragColor = vec4(1.0,1.0,1.0,1.0);\r\n" +
			"}\r\n";

	@Override
	public void activate() {
		mGraphics.mCurrentProgram = this;
		mProgram.activate();
	}

	@Override
	protected String getVertexShader(AbstractGFXLoader gfxLoader) {
		return MINIMAL_VERTEX_SHADER;
	}

	@Override
	protected String getFragmentShader(AbstractGFXLoader gfxLoader) {
		return MINIMAL_FRAGMENT_SHADER;
	}

}
