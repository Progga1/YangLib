package yang.graphics.programs.permutations;

import yang.graphics.programs.GLProgram;

public abstract class SubShader {

	public abstract void setVariables(ShaderPermutationsParser shaderParser,ShaderDeclarations vsDecl,ShaderDeclarations fsDecl);
	public abstract void initHandles();
	public abstract void passData(GLProgram program);
	
}
