package yang.graphics.defaults.programs.subshaders.dataproviders;

import yang.graphics.programs.GLProgram;


public class TimeSubShader extends UniformSubShader {

	public TimeSubShader(int inShader) {
		super("float","time",inShader);
	}

	public TimeSubShader() {
		this(SHADER_BOTH);
	}

	@Override
	public boolean passesData() {
		return true;
	}

	@Override
	public void passData(GLProgram program) {
		program.setUniformFloat(mUniformHandle, mGraphics.mShaderTimer);
	}

}
