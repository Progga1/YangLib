package yang.graphics.programs.permutations;

import yang.graphics.programs.GLProgram;

public abstract class SubShader {

	public abstract void setVariables(ShaderPermutationsParser shaderParser);
	public abstract void passData(GLProgram program);
	
}
