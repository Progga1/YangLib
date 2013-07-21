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
	
	public SpecularLightSubShader(SpecularMatProperties properties) {
		mProperties = properties;
	}
	
	@Override
	public void setVariables(ShaderPermutationsParser shaderParser, ShaderDeclarations vsDecl, ShaderDeclarations fsDecl) {
		fsDecl.addUniform("vec4", "mtSpecColor");
		fsDecl.addUniform("float", "mtSpecExponent");
		shaderParser.appendLn(VAR_FS_MAIN,"vec3 specVector = reflect(camDir,normal)");
		shaderParser.appendOp(VAR_FRAGCOLOR, "max(0.0,pow(-dot(specVector,lightDir),mtSpecExponent))*mtSpecColor", "+");
		//shaderParser.appendOp(VAR_FRAGCOLOR, "vec4(camDir,1.0)", "*");
	}

	@Override
	public void initHandles(GLProgram program) {
		mSpecColorHandle = program.getUniformLocation("mtSpecColor");
		mSpecExponentHandle = program.getUniformLocation("mtSpecExponent");
	}

	@Override
	public final void passData(GLProgram program) {
		if(mProperties!=null) {
			program.setUniform4f(mSpecColorHandle, mProperties.mColor.mValues);
			program.setUniformFloat(mSpecExponentHandle, mProperties.mExponent);
		}
	}

}
