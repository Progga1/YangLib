package yang.graphics.programs.permutations;

import yang.graphics.programs.GLProgram;

public abstract class SubShader {

	public static final String VAR_FRAGCOLOR = "FS_COLOR";
	public static final String VAR_VS_COLOR = "VS_COLOR";
	public static final String VAR_VS_MAIN = "VS_MAIN";
	public static final String VAR_FS_MAIN = "FS_MAIN";
	
	public abstract void setVariables(ShaderPermutationsParser shaderParser,ShaderDeclarations vsDecl,ShaderDeclarations fsDecl);
	public abstract void initHandles(GLProgram program);
	public abstract void passData(GLProgram program);
	
}
