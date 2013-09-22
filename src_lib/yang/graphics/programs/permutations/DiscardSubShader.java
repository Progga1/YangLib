package yang.graphics.programs.permutations;


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
