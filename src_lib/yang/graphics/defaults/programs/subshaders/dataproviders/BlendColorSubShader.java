package yang.graphics.defaults.programs.subshaders.dataproviders;

import yang.graphics.programs.GLProgram;
import yang.graphics.programs.permutations.ShaderDeclarations;
import yang.graphics.programs.permutations.ShaderPermutationsParser;
import yang.graphics.programs.permutations.SubShader;
import yang.math.objects.Quadruple;

public class BlendColorSubShader extends SubShader {

	public int mPropertiesHandle;
	public Quadruple mProperties;
	public String mBlendInputColor;

	public BlendColorSubShader(String blendInputColor,Quadruple properties) {
		mBlendInputColor = blendInputColor;
		if(properties==null)
			properties = new Quadruple(1,1,1,1);
		mProperties = properties;
	}

	public BlendColorSubShader(String blendInputColor) {
		this(blendInputColor,null);
	}

	@Override
	public void setVariables(ShaderPermutationsParser shaderParser, ShaderDeclarations vsDecl, ShaderDeclarations fsDecl) {
		fsDecl.addUniform("vec4", "blendProperties");
		shaderParser.appendLn(VAR_FS_MAIN, "float blendAlpha = "+mBlendInputColor+".a*blendProperties[0]");
		shaderParser.appendOp(VAR_FRAGCOLOR, "(1.0-blendAlpha)", "*");
		shaderParser.appendOp(VAR_FRAGCOLOR, mBlendInputColor+"*blendAlpha", "+");
	}

	@Override
	public void initHandles(GLProgram program) {
		mPropertiesHandle = program.getUniformLocation("blendProperties");
	}

	@Override
	public boolean passesData() {
		return true;
	}

	@Override
	public void passData(GLProgram program) {
		program.setUniform4f(mPropertiesHandle, mProperties.mValues);
	}

}
