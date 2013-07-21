package yang.graphics.programs.permutations;

import yang.graphics.programs.GLProgram;

public class BasicSubShader extends SubShader {
	
	private boolean mWorldTransform;
	private boolean mUseTexture;
	private boolean mUseColor;
	
	public BasicSubShader(boolean useWorldTransform,boolean useTexture,boolean useColor) {
		mWorldTransform = useWorldTransform;
		mUseTexture = useTexture;
		mUseColor = useColor;
	}
	
	@Override
	public void setVariables(ShaderPermutationsParser shaderParser,ShaderDeclarations vsDecl,ShaderDeclarations fsDecl) {

		vsDecl.addUniform("mat4","projTransform");
		vsDecl.addAttribute("vec4","vPosition");
		if(mUseTexture) {
			shaderParser.vsDeclaration("attribute vec2 vTexture");
			shaderParser.addVarying("vec2","texCoord");
			shaderParser.fsDeclaration("uniform sampler2D texSampler");
			shaderParser.appendLn("FS_MAIN","vec4 texCl=texture2D(texSampler, texCoord)");
			shaderParser.appendOp("COLOR", "texCl","*");
			shaderParser.appendLn("VS_MAIN", "texCoord=vTexture");
		}
		if(mUseColor) {
			shaderParser.vsDeclaration("attribute vec4 vColor");
			shaderParser.addVarying("vec4","color");
			shaderParser.appendOp("COLOR", "color","*");
			shaderParser.appendLn("VS_MAIN", "color=vColor");
		}
		if(mWorldTransform) {
			shaderParser.vsDeclaration("uniform mat4 worldTransform");
			shaderParser.appendLn("VS_MAIN", "gl_Position = projTransform * worldTransform * vPosition;");
		}else
			shaderParser.appendLn("VS_MAIN", "gl_Position = projTransform * vPosition");
		
		
	}

	@Override
	public void passData(GLProgram program) {
		
	}

	@Override
	public void initHandles() {
		
	}

	
	
}
