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
	public void setVariables(ShaderPermutationsParser shaderParser) {

		shaderParser.vsDeclaration("uniform mat4 projTransform");
		shaderParser.vsDeclaration("attribute vec4 vPosition");
		if(mUseTexture) {
			shaderParser.vsDeclaration("attribute vec2 vTexture");
			shaderParser.vsDeclaration("varying vec2 texCoord");
			shaderParser.fsDeclaration("uniform sampler2D texSampler");
			shaderParser.fsDeclaration("varying vec2 texCoord");
			shaderParser.appendLn("FS_MAIN","vec4 texCl=texture2D(texSampler, texCoord)");
			shaderParser.appendOp("COLOR", "texCl","*");
			shaderParser.appendLn("VS_MAIN", "texCoord=vTexture");
		}
		if(mUseColor) {
			shaderParser.vsDeclaration("attribute vec4 vColor");
			shaderParser.vsDeclaration("varying vec4 color");
			shaderParser.fsDeclaration("varying vec4 color");
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

	
	
}
