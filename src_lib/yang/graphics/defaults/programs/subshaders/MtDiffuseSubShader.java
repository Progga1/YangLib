package yang.graphics.defaults.programs.subshaders;

import yang.graphics.model.FloatColor;
import yang.graphics.programs.GLProgram;
import yang.graphics.programs.permutations.ShaderDeclarations;
import yang.graphics.programs.permutations.ShaderPermutationsParser;
import yang.graphics.programs.permutations.SubShader;

public class MtDiffuseSubShader extends SubShader{

	public FloatColor mColor;
	public int mDiffuseColorHandle;
	
	public MtDiffuseSubShader(FloatColor color) {
		mColor = color;
	}
	
	@Override
	public void setVariables(ShaderPermutationsParser shaderParser, ShaderDeclarations vsDecl, ShaderDeclarations fsDecl) {
		vsDecl.addUniform("vec4", "diffuseColor");
		shaderParser.appendOp(VAR_VS_COLOR, "diffuseColor", "*");
	}

	@Override
	public void initHandles(GLProgram program) {
		mDiffuseColorHandle = program.getUniformLocation("diffuseColor");
	}

	@Override
	public void passData(GLProgram program) {
		program.setUniform4f(mDiffuseColorHandle, mColor.mValues);
	}
	
	@Override
	public boolean passesData() {
		return mColor!=null;
	}
	
}
