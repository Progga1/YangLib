package yang.graphics.defaults.programs.subshaders;

import yang.graphics.programs.GLProgram;
import yang.graphics.programs.permutations.ShaderDeclarations;
import yang.graphics.programs.permutations.ShaderPermutationsParser;
import yang.graphics.programs.permutations.SubShader;

public class LightSubShader extends SubShader {

	public int mLightDirHandle;
	public int mLightColorHandle;
	public LightProperties mLightProperties;
	
	public LightSubShader(LightProperties lightProperties) {
		mLightProperties = lightProperties;
	}
	
	@Override
	public void setVariables(ShaderPermutationsParser shaderParser,ShaderDeclarations vsDecl,ShaderDeclarations fsDecl) {
		fsDecl.addUniform("vec3","lightDir");
		fsDecl.addUniform("vec3","lightColor");
		shaderParser.appendLn(VAR_FS_MAIN, "vec4 lgt = vec4(dot(lightDir,normal)*lightColor,1.0)");
		//shaderParser.appendLn(VAR_FS_MAIN, "lgt.a = 1.0");
		shaderParser.appendOp(VAR_FRAGCOLOR, "lgt", "*");
	}

	@Override
	public void initHandles(GLProgram program) {
		mLightDirHandle = program.getUniformLocation("lightDir");
		mLightColorHandle = program.getUniformLocation("lightColor");
	}

	
	@Override
	public void passData(GLProgram program) {
		program.setUniform3f(mLightDirHandle, mLightProperties.mDirection.mValues);
		program.setUniform3f(mLightColorHandle, mLightProperties.mColor.mValues);
	}
}
