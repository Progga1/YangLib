package yang.graphics.defaults.programs.subshaders.realistic;

import yang.graphics.programs.GLProgram;
import yang.graphics.programs.permutations.ShaderDeclarations;
import yang.graphics.programs.permutations.ShaderPermutationsParser;
import yang.graphics.programs.permutations.SubShader;
import yang.math.objects.Quadruple;

public class ScreenCoordTextureSubShader extends SubShader {

	public int mScreenTexSampler;
	public int mTextureLevel;
	public int mPropertiesHandle;
	public Quadruple mProperties;

	public ScreenCoordTextureSubShader(Quadruple properties) {
		if(properties==null)
			properties = new Quadruple(1,1,1,1);
		mProperties = properties;
	}

	public ScreenCoordTextureSubShader() {
		this(null);
	}

	@Override
	public void setVariables(ShaderPermutationsParser shaderParser, ShaderDeclarations vsDecl, ShaderDeclarations fsDecl) {
		fsDecl.addUniform("sampler2D", "screenTexSampler");
		fsDecl.addUniform("vec4", "screenTexProperties");
//		shaderParser.appendLn(VAR_FS_MAIN, );
		shaderParser.appendLn(VAR_FS_MAIN,"vec4 screenTexCl = texture2D(screenTexSampler,vec2(screenPos.x/screenPos.w*0.5+0.5,screenPos.y/screenPos.w*0.5+0.5))");
		shaderParser.appendLn(VAR_FS_MAIN, "float screenTexAlpha = screenTexCl.a*screenTexProperties[0]");
		shaderParser.appendOp(VAR_FRAGCOLOR, "(1.0-screenTexAlpha)", "*");
		shaderParser.appendOp(VAR_FRAGCOLOR, "screenTexCl*screenTexAlpha", "+");
	}

	@Override
	public void initHandles(GLProgram program) {
		mTextureLevel = program.nextTextureLevel();
		mScreenTexSampler = program.getUniformLocation("screenTexSampler");
		mPropertiesHandle = program.getUniformLocation("screenTexProperties");
	}

	@Override
	public boolean passesData() {
		return true;
	}

	@Override
	public void passData(GLProgram program) {
		program.setUniformInt(mScreenTexSampler, mTextureLevel);
		program.setUniform4f(mPropertiesHandle, mProperties.mValues);
	}

}
