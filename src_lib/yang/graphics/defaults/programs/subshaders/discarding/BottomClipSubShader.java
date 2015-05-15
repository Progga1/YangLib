package yang.graphics.defaults.programs.subshaders.discarding;

import yang.graphics.programs.permutations.ShaderDeclarations;
import yang.graphics.programs.permutations.ShaderPermutationsParser;
import yang.graphics.programs.permutations.SubShader;


public class BottomClipSubShader extends SubShader {

	private final float mMinY;

	public BottomClipSubShader(float minY) {
		mMinY = minY;
	}

	@Override
	public void setVariables(ShaderPermutationsParser shaderParser,ShaderDeclarations vsDecl,ShaderDeclarations fsDecl) {
		shaderParser.appendLn(VAR_FS_MAIN,"if(worldPosition.y<="+mMinY+") discard;");
	}

}
