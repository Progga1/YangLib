package yang.graphics.defaults.programs.subshaders.realistic;

import yang.graphics.defaults.programs.subshaders.SpecularLightBasicSubShader;
import yang.graphics.defaults.programs.subshaders.properties.SpecularMatProperties;
import yang.graphics.programs.permutations.ShaderDeclarations;
import yang.graphics.programs.permutations.ShaderPermutationsParser;

public class SpecularLightSubShader extends SpecularLightBasicSubShader {

	public SpecularLightSubShader(SpecularMatProperties properties) {
		super(properties);
	}
	
	@Override
	public void setVariables(ShaderPermutationsParser shaderParser, ShaderDeclarations vsDecl, ShaderDeclarations fsDecl) {
		super.setVariables(shaderParser, vsDecl, fsDecl);
		shaderParser.appendOp(VAR_FRAGCOLOR, "pow(max(0.0,-dot(specVector,lightDir)),mtSpecExponent)*specColor", "+");
	}

}
