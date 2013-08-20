package yang.graphics.defaults.programs.subshaders;

import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.model.FloatColor;
import yang.graphics.programs.GLProgram;
import yang.graphics.programs.permutations.ShaderDeclarations;
import yang.graphics.programs.permutations.ShaderPermutationsParser;
import yang.graphics.programs.permutations.SubShader;

public class ColorFactorSubShader extends SubShader {

	public FloatColor mColor;
	public int mColorFactorHandle;
	
	public ColorFactorSubShader(FloatColor color) {
		mColor = color;
	}
	
	public ColorFactorSubShader(float[] color) {
		mColor = new FloatColor();
		mColor.mValues = color;
	}
	
	public ColorFactorSubShader(DefaultGraphics<?> graphics) {
		this(graphics.mColorFactor);
	}
	
	@Override
	public void setVariables(ShaderPermutationsParser shaderParser, ShaderDeclarations vsDecl, ShaderDeclarations fsDecl) {
		fsDecl.addUniform("vec4", "colorFactor");
		//shaderParser.circumOp(VAR_VS_COLOR, "colorFactor","*");
		shaderParser.appendOp(VAR_FRAGCOLOR, "colorFactor","*");
	}

	@Override
	public void initHandles(GLProgram program) {
		mColorFactorHandle = program.getUniformLocation("colorFactor");
	}

	@Override
	public void passData(GLProgram program) {
		program.setUniform4f(mColorFactorHandle, mColor.mValues);
	}

	@Override
	public boolean passesData() {
		return mColor!=null;
	}
	
}
