package yang.graphics.defaults.programs.subshaders.dataproviders;

import yang.graphics.programs.GLProgram;
import yang.graphics.programs.permutations.ShaderDeclarations;
import yang.graphics.programs.permutations.ShaderPermutationsParser;
import yang.graphics.programs.permutations.SubShader;

public class UniformSubShader extends SubShader {

	public int mUniformHandle;
	public String mVarName,mVarType;
	public int mInShader;

	public UniformSubShader(String varType,String varName,int inShader) {
		mVarType = varType;
		mVarName = varName;
		mInShader = inShader;
	}

	@Override
	public void setVariables(ShaderPermutationsParser shaderParser, ShaderDeclarations vsDecl, ShaderDeclarations fsDecl) {
		if(mInShader%2==1)
			vsDecl.addUniform(mVarType,mVarName);
		if(mInShader/2==1)
			fsDecl.addUniform(mVarType,mVarName);
	}

	@Override
	public void initHandles(GLProgram program) {
		mUniformHandle = program.getUniformLocation(mVarName);
	}

}
