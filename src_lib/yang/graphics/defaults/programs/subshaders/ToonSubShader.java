package yang.graphics.defaults.programs.subshaders;

import yang.graphics.programs.GLProgram;
import yang.graphics.programs.permutations.ShaderDeclarations;
import yang.graphics.programs.permutations.ShaderPermutationsParser;
import yang.graphics.programs.permutations.SubShader;
import yang.graphics.translator.GraphicsTranslator;
import yang.graphics.translator.Texture;

public class ToonSubShader extends SubShader {

	public Texture mRampTex;
	public int mRampSamplerHandle;
	private GraphicsTranslator mGraphics;
	
	public ToonSubShader(GraphicsTranslator graphics,Texture ramp) {
		mRampTex = ramp;
		mGraphics = graphics;
	}
	
	@Override
	public void setVariables(ShaderPermutationsParser shaderParser, ShaderDeclarations vsDecl, ShaderDeclarations fsDecl) {
		fsDecl.addUniform("sampler2D", "toonRamp");
		shaderParser.appendFragmentMain("vec4 lgt = texture2D(toonRamp,vec2(lightIntens,0))*vec4(lightDiffuse,1.0)");
		shaderParser.appendOp(VAR_FRAGCOLOR, "lgt", "*");
	}

	@Override
	public void initHandles(GLProgram program) {
		mRampSamplerHandle = program.getUniformLocation("toonRamp");
	}

	@Override
	public void passData(GLProgram program) {
		program.setUniformInt(mRampSamplerHandle,1);
		mGraphics.bindTextureNoFlush(mRampTex,1);
	}
	
	@Override
	public boolean passesData() {
		return mRampTex!=null;
	}

}
