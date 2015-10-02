package yang.graphics.defaults.programs.subshaders;

import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.model.FloatColor;
import yang.graphics.programs.GLProgram;
import yang.graphics.programs.permutations.ShaderDeclarations;
import yang.graphics.programs.permutations.ShaderPermutationsParser;
import yang.graphics.programs.permutations.SubShader;

public class ColorFactorSubShader extends SubShader {

	public FloatColor mFrontFaceColor;
	public FloatColor mBackFaceColor;
	public int mFrontColorFactorHandle;
	public int mBackColorFactorHandle;
	private boolean mFrontAndBackFace;

	public ColorFactorSubShader(FloatColor frontFaceColor,FloatColor backFaceColor) {
		mFrontFaceColor = frontFaceColor;
		mBackFaceColor = backFaceColor;
		mFrontAndBackFace = frontFaceColor!=backFaceColor;
	}

	public ColorFactorSubShader(FloatColor color) {
		this(color,color);
	}

	public ColorFactorSubShader(float[] color) {
		this(new FloatColor(color,true));
	}

	public ColorFactorSubShader(DefaultGraphics<?> graphics) {
		this(graphics.mColorFactor);
	}

	@Override
	public void setVariables(ShaderPermutationsParser shaderParser, ShaderDeclarations vsDecl, ShaderDeclarations fsDecl) {
		if(mFrontAndBackFace) {
			fsDecl.addUniform("vec4", "frontColorFactor");
			fsDecl.addUniform("vec4", "backColorFactor");
			shaderParser.appendOp(VAR_FRAGCOLOR, "(gl_FrontFacing?frontColorFactor:backColorFactor)","*");
		}else{
			fsDecl.addUniform("vec4", "colorFactor");
			shaderParser.appendOp(VAR_FRAGCOLOR, "colorFactor","*");
		}
	}

	@Override
	public void initHandles(GLProgram program) {
		if(mFrontAndBackFace) {
			mFrontColorFactorHandle = program.getUniformLocation("frontColorFactor");
			mBackColorFactorHandle = program.getUniformLocation("backColorFactor");
		}else{
			mFrontColorFactorHandle = program.getUniformLocation("colorFactor");
			mBackColorFactorHandle = -1;
		}

	}

	@Override
	public void passData(GLProgram program) {
		program.setUniform4f(mFrontColorFactorHandle, mFrontFaceColor.mValues);
		if(mFrontAndBackFace) {
			program.setUniform4f(mBackColorFactorHandle, mBackFaceColor.mValues);
		}
	}

	@Override
	public boolean passesData() {
		return mFrontFaceColor!=null;
	}

}
