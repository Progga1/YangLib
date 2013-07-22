package yang.graphics.defaults.programs.subshaders;

import yang.graphics.model.FloatColor;
import yang.graphics.programs.GLProgram;
import yang.graphics.programs.permutations.ShaderDeclarations;
import yang.graphics.programs.permutations.ShaderPermutationsParser;
import yang.graphics.programs.permutations.SubShader;

public class AmbientSubShader extends SubShader {

	public FloatColor mColor;
	public int mAmbientColorHandle;
	
	public AmbientSubShader(FloatColor color) {
		mColor = color;
	}
	
	@Override
	public void setVariables(ShaderPermutationsParser shaderParser, ShaderDeclarations vsDecl, ShaderDeclarations fsDecl) {
		fsDecl.addUniform("vec4", "ambientColor");
		//shaderParser.circumOp(VAR_FRAGCOLOR, "max(ambientColor,",")");
		shaderParser.appendFragmentMain("lgt = max(ambientColor,lgt)");
	}

	@Override
	public void initHandles(GLProgram program) {
		mAmbientColorHandle = program.getUniformLocation("ambientColor");
	}

	@Override
	public void passData(GLProgram program) {
		program.setUniform4f(mAmbientColorHandle, mColor.mValues);
	}

	@Override
	public boolean passesData() {
		return mColor!=null;
	}
	
}
