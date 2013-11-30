package yang.graphics.defaults.programs.subshaders;

import yang.graphics.programs.permutations.ShaderDeclarations;
import yang.graphics.programs.permutations.ShaderPermutationsParser;
import yang.graphics.programs.permutations.SubShader;

public class SkinningSubShader extends SubShader {

	@Override
	public void setVariables(ShaderPermutationsParser shaderParser, ShaderDeclarations vsDecl, ShaderDeclarations fsDecl) {
		vsDecl.addUniform("mat4[24]", "skinningTransforms");
	}



}
