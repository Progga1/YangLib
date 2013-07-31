package yang.graphics.programs.permutations;

import yang.graphics.programs.GLProgram;

public class SubShaderNode extends SubShader {

	public SubShader[] mSubShaders;
	
	public SubShaderNode(SubShader[] subShaders) {
		mSubShaders = subShaders;
	}
	
	@Override
	public void setVariables(ShaderPermutationsParser shaderParser, ShaderDeclarations vsDecl, ShaderDeclarations fsDecl) {
		for(SubShader subShader:mSubShaders) {
			subShader.setVariables(shaderParser, vsDecl, fsDecl);
		}
	}

//	@Override
//	public void initHandles(GLProgram program) {
//		for(SubShader subShader:mSubShaders) {
//			subShader.initHandles(program);
//		}
//	}
	
	@Override
	public SubShader[] getInnerShaders() {
		return mSubShaders;
	}
	
}
