package yang.graphics.defaults.programs.subshaders;

import yang.graphics.programs.GLProgram;
import yang.graphics.programs.permutations.ShaderDeclarations;
import yang.graphics.programs.permutations.ShaderPermutationsParser;
import yang.graphics.programs.permutations.SubShader;

public class LightSubShader extends SubShader {

	public int mLightDirHandle;
	public int mLightColorHandle;
	public LightProperties mLightProperties;
	
	@Override
	public void setVariables(ShaderPermutationsParser shaderParser,ShaderDeclarations vsDecl,ShaderDeclarations fsDecl) {
		fsDecl.addUniform("vec4","lightDir");
		fsDecl.addUniform("vec4","lightColor");
		shaderParser.appendLn("FS_MAIN", "vec4 lgt = dot(lightDir,normal)*lightColor");
		//shaderParser.appendOp("COLOR", "lgt", "*");
	}

	@Override
	public void passData(GLProgram program) {
		
	}

	@Override
	public void initHandles() {
		
	}

}
