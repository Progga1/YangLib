package yang.graphics.defaults.programs.subshaders.toon;

import yang.graphics.programs.permutations.ShaderDeclarations;
import yang.graphics.programs.permutations.ShaderPermutationsParser;
import yang.graphics.programs.permutations.SubShader;

public class ToonDiffuseSubShader extends SubShader {

	@Override
	public void setVariables(ShaderPermutationsParser shaderParser, ShaderDeclarations vsDecl, ShaderDeclarations fsDecl) {
		fsDecl.addUniform("sampler2D", "toonRamp");
		fsDecl.localDeclareOrAssign("vec4","lgt","texture2D(toonRamp,vec2(lightIntens,0.25))*vec4(lightDiffuse,1.0)");

		shaderParser.appendOp(VAR_FRAGCOLOR, "lgt", "*");
	}

}
