package yang.graphics.defaults.programs.subshaders;

import yang.graphics.defaults.programs.subshaders.properties.SpecularMatProperties;
import yang.graphics.programs.GLProgram;
import yang.graphics.programs.permutations.ShaderDeclarations;
import yang.graphics.programs.permutations.ShaderPermutationsParser;
import yang.graphics.programs.permutations.SubShader;

public class SpecularLightSubShader extends SubShader {

	public SpecularMatProperties mProperties;
	public int mSpecColorHandle;
	public int mSpecExponentHandle;
	public int mSpecTexSampler;
	public int mSpecUseTexHandle;
	
	public SpecularLightSubShader(SpecularMatProperties properties) {
		mProperties = properties;
	}
	
	@Override
	public void setVariables(ShaderPermutationsParser shaderParser, ShaderDeclarations vsDecl, ShaderDeclarations fsDecl) {
		fsDecl.addUniform("vec4", "mtSpecColor");
		fsDecl.addUniform("sampler2D", "mtSpecSampler");
		fsDecl.addUniform("bool", "hasSpecTex");
		fsDecl.addUniform("float", "mtSpecExponent");
		shaderParser.appendLn(VAR_FS_MAIN,"vec3 specVector = reflect(camDir,normal)");
		shaderParser.appendFragmentMain("vec4 specColor");
		shaderParser.appendFragmentMain("if(hasSpecTex) {");
		shaderParser.appendFragmentMain("specColor = texture2D(mtSpecSampler,texCoord)");
		shaderParser.appendFragmentMain("}else{");
		shaderParser.appendFragmentMain("specColor = mtSpecColor");
		shaderParser.appendFragmentMain("}");
		shaderParser.appendOp(VAR_FRAGCOLOR, "pow(max(0.0,-dot(specVector,lightDir)),mtSpecExponent)*specColor", "+");
		//shaderParser.appendOp(VAR_FRAGCOLOR, "vec4(camDir,1.0)", "*");
	}

	@Override
	public void initHandles(GLProgram program) {
		mSpecColorHandle = program.getUniformLocation("mtSpecColor");
		mSpecExponentHandle = program.getUniformLocation("mtSpecExponent");
		mSpecTexSampler = program.getUniformLocation("mtSpecSampler");
		mSpecUseTexHandle = program.getUniformLocation("mtUseSpecTex");
	}

	@Override
	public final void passData(GLProgram program) {
		if(mProperties.mTexture!=null) {
			program.setUniformInt(mSpecTexSampler, 2);
			program.setUniformInt(mSpecColorHandle, 1);
		}else{
			program.setUniform4f(mSpecColorHandle, mProperties.mColor.mValues);
			program.setUniformInt(mSpecColorHandle, 0);
		}
		program.setUniformFloat(mSpecExponentHandle, mProperties.mExponent);
	}
	
	@Override
	public boolean passesData() {
		return mProperties!=null;
	}

}
