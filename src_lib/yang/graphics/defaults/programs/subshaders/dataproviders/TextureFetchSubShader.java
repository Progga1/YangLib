package yang.graphics.defaults.programs.subshaders.dataproviders;

import yang.graphics.programs.GLProgram;
import yang.graphics.programs.permutations.ShaderDeclarations;
import yang.graphics.programs.permutations.ShaderPermutationsParser;
import yang.graphics.programs.permutations.SubShader;

public class TextureFetchSubShader extends SubShader {

	public int mTexSampler;
	public int mTextureLevel;
	public String mCoordinatesVec;
	public String mTexSamplerName;

	public TextureFetchSubShader(String coordinatesVec) {
		mCoordinatesVec = coordinatesVec;
	}

	@Override
	public void setVariables(ShaderPermutationsParser shaderParser, ShaderDeclarations vsDecl, ShaderDeclarations fsDecl) {
		fsDecl.addUniform("sampler2D", "texFetchSampler");
		shaderParser.appendLn(VAR_FS_MAIN,"vec4 screenTexCl = texture2D(texFetchSampler,"+mCoordinatesVec+")");
	}

	@Override
	public void initHandles(GLProgram program) {
		mTextureLevel = program.nextTextureLevel();
		mTexSampler = program.getUniformLocation("texFetchSampler");
	}

	@Override
	public boolean passesData() {
		return true;
	}

	@Override
	public void passData(GLProgram program) {
		program.setUniformInt(mTexSampler, mTextureLevel);
	}

}
