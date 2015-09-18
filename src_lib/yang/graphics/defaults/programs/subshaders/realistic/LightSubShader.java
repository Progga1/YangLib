package yang.graphics.defaults.programs.subshaders.realistic;

import yang.graphics.defaults.programs.subshaders.properties.LightProperties;
import yang.graphics.programs.GLProgram;
import yang.graphics.programs.permutations.ShaderDeclarations;
import yang.graphics.programs.permutations.ShaderPermutationsParser;
import yang.graphics.programs.permutations.SubShader;

public class LightSubShader extends SubShader {

	public int mLightDirHandle;
	public int mLightDiffuseHandle;
	public LightProperties mLightProperties;
	public float mAddValue = 0.8f;

	public LightSubShader(LightProperties lightProperties) {
		mLightProperties = lightProperties;
	}

	@Override
	public void setVariables(ShaderPermutationsParser shaderParser,ShaderDeclarations vsDecl,ShaderDeclarations fsDecl) {
		fsDecl.addUniform("vec3","lightDir");
		fsDecl.addUniform("vec3","lightDiffuse");
		shaderParser.appendLn(VAR_FS_MAIN, "float lightIntens = dot(lightDir,normal)+"+mAddValue);
	}

	@Override
	public void initHandles(GLProgram program) {
		mLightDirHandle = program.getUniformLocation("lightDir");
		mLightDiffuseHandle = program.getUniformLocation("lightDiffuse");
	}


	@Override
	public void passData(GLProgram program) {
		program.setUniform3f(mLightDirHandle, mLightProperties.mDirection);
		program.setUniform3f(mLightDiffuseHandle, mLightProperties.mDiffuse.mValues);
	}

	@Override
	public boolean passesData() {
		return mLightProperties!=null;
	}

}
