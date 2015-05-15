package yang.graphics.defaults.programs.subshaders.discarding;

import yang.graphics.programs.permutations.ShaderDeclarations;
import yang.graphics.programs.permutations.ShaderPermutationsParser;
import yang.graphics.programs.permutations.SubShader;


public class DiscardSubShader extends SubShader {

	private float mThreshold;
	
	public DiscardSubShader(float threshold) {
		mThreshold = threshold;
	}
	
	@Override
	public void setVariables(ShaderPermutationsParser shaderParser,ShaderDeclarations vsDecl,ShaderDeclarations fsDecl) {
		shaderParser.appendLn(VAR_FS_MAIN,"if(texCl.a<="+mThreshold+") discard;");
	}
	
}
