package yang.graphics.programs.permutations;

import yang.graphics.programs.GLProgram;
import yang.graphics.translator.GraphicsTranslator;

public abstract class SubShader {

	public ShaderPermutations mMainShader;

	public static final String VAR_FRAGCOLOR = "FS_COLOR";
	public static final String VAR_VS_COLOR = "VS_COLOR";
	public static final String VAR_VS_MAIN = "VS_MAIN";
	public static final String VAR_FS_MAIN = "FS_MAIN";

	public static final String OP_ADD = "+";
	public static final String OP_SUB = "-";
	public static final String OP_MULT = "*";
	public static final String OP_DIV = "/";

	public static final int SHADER_VERTEX = 1;
	public static final int SHADER_FRAGMENT = 2;
	public static final int SHADER_BOTH = 3;

	protected GraphicsTranslator mGraphics;

	public abstract void setVariables(ShaderPermutationsParser shaderParser,ShaderDeclarations vsDecl,ShaderDeclarations fsDecl);

	public void setGraphics(GraphicsTranslator graphics) {
		mGraphics = graphics;
	}

	public void initHandles(GLProgram program) {

	}

	public boolean handlesOK() {
		return true;
	}

	public void passData(GLProgram program) {

	}

	public boolean passesData() {
		return false;
	}

	public SubShader[] getInnerShaders() {
		return null;
	}

}
