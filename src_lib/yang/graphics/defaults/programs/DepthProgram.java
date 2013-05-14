package yang.graphics.defaults.programs;

import yang.graphics.programs.Basic3DProgram;
import yang.graphics.translator.AbstractGFXLoader;

public class DepthProgram extends Basic3DProgram {

	public static String VERTEX_SHADER = 
			"uniform mat4 projTransform;\n"
			+"\n"
			+"attribute vec4 vPosition;\n"
			+"\n"
			+"varying float depth;\n"
			+"\n"
			+"void main() {\n"
			+"	gl_Position = projTransform * vPosition;\n"
			+"	depth = (-gl_Position.z/gl_Position.w+1.0)*0.5;\n"
			+"}";
	
	public static String FRAGMENT_SHADER = 
			"#ANDROID precision mediump float;\n"
			+"\n"
			+"varying float depth;\n"
			+"\n"
			+"void main()\n"
			+"{\n"
			+"	gl_FragColor = vec4(depth,depth,depth,1.0);\n"
			+"}";
	
	@Override
	protected String getVertexShader(AbstractGFXLoader gfxLoader) {
		return VERTEX_SHADER;
	}

	@Override
	protected String getFragmentShader(AbstractGFXLoader gfxLoader) {
		return FRAGMENT_SHADER;
	}
	
	@Override
	public void initHandles() {
		mPositionHandle = mProgram.getAttributeLocation("vPosition");
		mProjHandle = mProgram.getUniformLocation("projTransform");
	}

	@Override
	public void activate() {
		mProgram.activate();
	}
	
}
