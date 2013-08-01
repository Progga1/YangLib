package yang.graphics.defaults.programs.subshaders;

import yang.graphics.defaults.programs.subshaders.properties.EmissiveMatProperties;
import yang.graphics.programs.GLProgram;
import yang.graphics.programs.permutations.ShaderDeclarations;
import yang.graphics.programs.permutations.ShaderPermutationsParser;
import yang.graphics.programs.permutations.SubShader;

public class EmissiveSubShader extends SubShader {

	public EmissiveMatProperties mProperties;
	public int mEmisColorHandle;
	public int mEmisTexSampler;
	public int mEmisUseTexHandle;
	public int mTextureLevel;
	
	public EmissiveSubShader(EmissiveMatProperties properties) {
		mProperties = properties;
	}
	
	@Override
	public void setVariables(ShaderPermutationsParser shaderParser, ShaderDeclarations vsDecl, ShaderDeclarations fsDecl) {
		fsDecl.addUniform("vec4", "mtEmisColor");
		fsDecl.addUniform("sampler2D", "mtEmisSampler");
		fsDecl.addUniform("bool", "mtEmisUseTex");
		shaderParser.appendFragmentMain("vec4 emisColor");
		shaderParser.appendFragmentMain("if(mtEmisUseTex) {");
		shaderParser.appendFragmentMain("emisColor = texture2D(mtEmisSampler,texCoord)*mtEmisColor");
		shaderParser.appendFragmentMain("}else{");
		shaderParser.appendFragmentMain("emisColor = mtEmisColor");
		shaderParser.appendFragmentMain("}");
		//shaderParser.appendFragmentMain("emisColor = vec4(0.0,0.4,0.0,0.0)");
		shaderParser.appendOp(VAR_FRAGCOLOR, "emisColor", OP_ADD);
	}
	
	@Override
	public void initHandles(GLProgram program) {
		mEmisColorHandle = program.getUniformLocation("mtEmisColor");
		mEmisTexSampler = program.getUniformLocation("mtEmisSampler");
		mEmisUseTexHandle = program.getUniformLocation("mtEmisUseTex");
	}
	
	@Override
	public boolean handlesOK() {
		return mEmisColorHandle>=0 && mEmisTexSampler>=0 && mEmisUseTexHandle>=0;
	}
	
	@Override
	public final void passData(GLProgram program) {
		program.setUniform4f(mEmisColorHandle, mProperties.mColor.mValues);
		if(mProperties.mTexture!=null) {
			program.setUniformInt(mEmisTexSampler, mTextureLevel);
			mGraphics.bindTextureNoFlush(mProperties.mTexture, mTextureLevel);
			program.setUniformInt(mEmisUseTexHandle, 1);
		}else{
			program.setUniformInt(mEmisUseTexHandle, 0);
		}
	}
	
	@Override
	public boolean passesData() {
		return mProperties!=null;
	}
	
}
