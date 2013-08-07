package yang.graphics.defaults.programs.subshaders.toon;

import yang.graphics.defaults.programs.subshaders.SpecularLightBasicSubShader;
import yang.graphics.defaults.programs.subshaders.properties.SpecularMatProperties;
import yang.graphics.programs.permutations.ShaderDeclarations;
import yang.graphics.programs.permutations.ShaderPermutationsParser;

public class ToonSpecularLightSubShader extends SpecularLightBasicSubShader {

	public ToonSpecularLightSubShader(SpecularMatProperties properties) {
		super(properties);
	}

	@Override
	public void setVariables(ShaderPermutationsParser shaderParser, ShaderDeclarations vsDecl, ShaderDeclarations fsDecl) {
		super.setVariables(shaderParser, vsDecl, fsDecl);
		shaderParser.appendOp(VAR_FRAGCOLOR, "texture2D(toonRamp,vec2(pow(max(0.0,-dot(specVector,lightDir)),mtSpecExponent),0.75))*specColor", "+");
	}
	
}
