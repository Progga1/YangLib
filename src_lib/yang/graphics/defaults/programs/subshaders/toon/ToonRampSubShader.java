package yang.graphics.defaults.programs.subshaders.toon;

import yang.graphics.programs.GLProgram;
import yang.graphics.programs.permutations.ShaderDeclarations;
import yang.graphics.programs.permutations.ShaderPermutationsParser;
import yang.graphics.programs.permutations.SubShader;
import yang.graphics.translator.Texture;

public class ToonRampSubShader extends SubShader {

	public Texture mRampTex;
	public int mRampSamplerHandle;
	public int mTextureLevel;
	
	public ToonRampSubShader(Texture ramp) {
		mRampTex = ramp;
	}
	
	@Override
	public void setVariables(ShaderPermutationsParser shaderParser, ShaderDeclarations vsDecl, ShaderDeclarations fsDecl) {
		fsDecl.addUniform("sampler2D", "toonRamp");
	}

	@Override
	public void initHandles(GLProgram program) {
		mTextureLevel = program.nextTextureLevel();
		mRampSamplerHandle = program.getUniformLocation("toonRamp");
	}

	@Override
	public void passData(GLProgram program) {
		program.setUniformInt(mRampSamplerHandle,mTextureLevel);
		mGraphics.bindTextureNoFlush(mRampTex,mTextureLevel);
	}
	
	@Override
	public boolean passesData() {
		return mRampTex!=null;
	}
	
}
