package yang.graphics.defaults.programs.subshaders;

import yang.graphics.programs.permutations.ShaderDeclarations;
import yang.graphics.programs.permutations.ShaderPermutationsParser;
import yang.graphics.programs.permutations.SubShader;

public class DiffuseLightSubShader extends SubShader {

	@Override
	public void setVariables(ShaderPermutationsParser shaderParser,ShaderDeclarations vsDecl,ShaderDeclarations fsDecl) {
		fsDecl.localDeclareOrMult("vec4","lgt","vec4(lightIntens*lightDiffuse,1.0)");
		shaderParser.appendOp(VAR_FRAGCOLOR, "lgt", "*");
	}

}
