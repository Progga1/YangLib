package yang.graphics.defaults.programs.subshaders;

import yang.graphics.defaults.programs.subshaders.properties.SpecularMatProperties;
import yang.graphics.programs.GLProgram;
import yang.graphics.programs.permutations.ShaderDeclarations;
import yang.graphics.programs.permutations.ShaderPermutationsParser;
import yang.graphics.programs.permutations.SubShader;

public class SpecularLightBasicSubShader extends SubShader {

	public SpecularMatProperties mProperties;
	public int mSpecColorHandle;
	public int mSpecExponentHandle;
	public int mSpecTexSampler;
	public int mSpecUseTexHandle;
	public int mTextureLevel;

	public SpecularLightBasicSubShader(SpecularMatProperties properties) {
		mProperties = properties;
	}

	@Override
	public void setVariables(ShaderPermutationsParser shaderParser, ShaderDeclarations vsDecl, ShaderDeclarations fsDecl) {
		fsDecl.addUniform("vec4", "mtSpecColor");
		fsDecl.addUniform("sampler2D", "mtSpecSampler");
		fsDecl.addUniform("bool", "mtSpecUseTex");
		fsDecl.addUniform("float", "mtSpecExponent");
		shaderParser.appendLn(VAR_FS_MAIN,"vec3 specVector = reflect(camDir,normal)");
		if(!fsDecl.localVariableExists("vec4", "specColor"))
			fsDecl.localDeclare("vec4", "specColor");
		shaderParser.appendFragmentMain("if(mtSpecUseTex) {\n"
									+ "specColor = texture2D(mtSpecSampler,texCoord);\n"
									+ "}else{\n"
									+ "specColor = mtSpecColor;\n"
									+ "}");
	}

	@Override
	public void initHandles(GLProgram program) {
		mTextureLevel = program.nextTextureLevel();
		mSpecColorHandle = program.getUniformLocation("mtSpecColor");
		mSpecExponentHandle = program.getUniformLocation("mtSpecExponent");
		mSpecTexSampler = program.getUniformLocation("mtSpecSampler");
		mSpecUseTexHandle = program.getUniformLocation("mtSpecUseTex");
	}

	@Override
	public boolean handlesOK() {
		return mSpecColorHandle>=0 && mSpecExponentHandle>=0 && mSpecTexSampler>=0 && mSpecUseTexHandle>=0;
	}

	@Override
	public final void passData(GLProgram program) {
		if(mProperties.mTexture!=null) {
			program.setUniformInt(mSpecTexSampler, mTextureLevel);
			mGraphics.bindTextureNoFlush(mProperties.mTexture, mTextureLevel);
			program.setUniformInt(mSpecUseTexHandle, 1);
		}else{
			program.setUniform4f(mSpecColorHandle, mProperties.mColor.mValues);
			program.setUniformInt(mSpecUseTexHandle, 0);
		}
		program.setUniformFloat(mSpecExponentHandle, mProperties.mExponent);
	}

	@Override
	public boolean passesData() {
		return mProperties!=null;
	}

}
