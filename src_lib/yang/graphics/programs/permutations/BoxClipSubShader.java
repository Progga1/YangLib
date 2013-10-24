package yang.graphics.programs.permutations;


public class BoxClipSubShader extends SubShader {

	private final float mMinY;

	public BoxClipSubShader(float minY) {
		mMinY = minY;
	}

	@Override
	public void setVariables(ShaderPermutationsParser shaderParser,ShaderDeclarations vsDecl,ShaderDeclarations fsDecl) {
		shaderParser.appendLn(VAR_FS_MAIN,"if(worldPosition.y<="+mMinY+") discard;");
	}

}
