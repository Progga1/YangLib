package yang.graphics.defaults.programs.subshaders;

import yang.graphics.programs.GLProgram;
import yang.graphics.programs.permutations.ShaderDeclarations;
import yang.graphics.programs.permutations.ShaderPermutationsParser;
import yang.graphics.programs.permutations.SubShader;
import yang.model.wrappers.FloatWrapper;

public class ToonOutlineSubShader extends SubShader {

	public int mThresholdHandle;
	public FloatWrapper mThreshold;
	
	public ToonOutlineSubShader(FloatWrapper threshold) {
		mThreshold = threshold;
	}
	
	@Override
	public void setVariables(ShaderPermutationsParser shaderParser, ShaderDeclarations vsDecl, ShaderDeclarations fsDecl) {
		fsDecl.addUniform("float", "toonOutlineThreshold");
		shaderParser.appendFragmentMain("vec4 contour = (pow(clamp(dot(camDir,normal)+toonOutlineThreshold,0.0,1.0),16.0)+0.2)*vec4(1.0,1.0,1.0,1.0)");
		shaderParser.appendFragmentMain("contour.a = 1.0");
		shaderParser.appendOp(VAR_FRAGCOLOR, "contour", "*");
	}

	@Override
	public void initHandles(GLProgram program) {
		mThresholdHandle = program.getUniformLocation("toonOutlineThreshold");
	}

	@Override
	public void passData(GLProgram program) {
		program.setUniformFloat(mThresholdHandle,1.0f-mThreshold.mValue);
		
	}
	
	@Override
	public boolean passesData() {
		return mThreshold!=null;
	}

}
