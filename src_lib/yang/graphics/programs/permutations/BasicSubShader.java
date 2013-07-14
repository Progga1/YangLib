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
		shaderParser.setVariable("COLOR", "vec4(1.0,1.0,1.0,1.0)");
		shaderParser.vsDeclaration("uniform mat4 projTransform");
		shaderParser.vsDeclaration("attribute vec4 vPosition");
		if(mUseTexture) {
			shaderParser.vsDeclaration("attribute vec2 vTexture");
			shaderParser.vsDeclaration("varying vec2 texCoords");
			shaderParser.fsDeclaration("uniform sampler texSampler");
			shaderParser.fsDeclaration("varying vec2 texCoords");
		}
		if(mUseColor) {
			shaderParser.vsDeclaration("attribute vec4 vColor");
			shaderParser.vsDeclaration("varying vec4 color");
			shaderParser.fsDeclaration("varying vec4 color");
		}
		if(mWorldTransform) {
			shaderParser.vsDeclaration("uniform mat4 worldTransform");
			shaderParser.appendVariable("VS_MAIN", "gl_Position = projTransform * worldTransform * vPosition;");
		}else
			shaderParser.appendVariable("VS_MAIN", "gl_Position = projTransform * vPosition");
	}

	@Override
	public void passData(GLProgram program) {
		
	}

	
	
}
