package yang.graphics.programs.permutations;

import yang.graphics.programs.GLProgram;

public class BasicSubShader extends SubShader {

	private final boolean mWorldTransform;
	private final boolean mUseTexture;
	private final boolean mUseColor;
	public boolean mPassScreenPos;
	public boolean mTwoFacedTextures = false;
	public int mBackTextureHandle;
	private int mBackTextureLevel;

	public BasicSubShader(boolean useWorldTransform,boolean useTexture,boolean useColor,boolean passScreenPos) {
		mWorldTransform = useWorldTransform;
		mUseTexture = useTexture;
		mUseColor = useColor;
		mPassScreenPos = passScreenPos;
	}

	public BasicSubShader(boolean useWorldTransform,boolean useTexture,boolean useColor) {
		this(useWorldTransform,useTexture,useColor,false);
	}

	@Override
	public void setVariables(ShaderPermutationsParser shaderParser,ShaderDeclarations vsDecl,ShaderDeclarations fsDecl) {
		vsDecl.addUniform("mat4","projTransform");
		vsDecl.addAttribute("vec4","vPosition");
		if(mUseTexture) {
			shaderParser.vsDeclaration("attribute vec2 vTexture");
			shaderParser.addVarying("vec2","texCoord");
			fsDecl.addUniform("sampler2D","texSampler");
			if(mTwoFacedTextures) {
				fsDecl.addUniform("sampler2D","texSamplerBack");
				shaderParser.appendLn(VAR_FS_MAIN,"vec4 texCl;\n" +
						"if(gl_FrontFacing)\n" +
						"	texCl = texture2D(texSampler, texCoord);\n" +
						"else\n" +
						"	texCl = texture2D(texSamplerBack, texCoord)");
			}else
				shaderParser.appendLn(VAR_FS_MAIN,"vec4 texCl = texture2D(texSampler, texCoord)");
			shaderParser.appendOp(VAR_FRAGCOLOR, "texCl","*");
			shaderParser.appendLn(VAR_VS_MAIN, "texCoord = vTexture");
		}
		if(mUseColor) {
			shaderParser.vsDeclaration("attribute vec4 vColor");
			shaderParser.appendOp(VAR_VS_COLOR, "vColor","*");
			shaderParser.addVarying("vec4","color");
			shaderParser.appendOp(VAR_FRAGCOLOR, "color","*");
		}
		shaderParser.addVarying("vec4","worldPosition");
		if(mWorldTransform) {
			vsDecl.addUniform("mat4","worldTransform");
			shaderParser.appendVertexMain("vec4 worldPos = worldTransform * vPosition");
			shaderParser.appendVertexMain("worldPosition = worldPos");
			shaderParser.appendVertexMain("gl_Position = projTransform * worldPos");
		}else{
			shaderParser.appendVertexMain("worldPosition = vPosition");
			shaderParser.appendVertexMain("gl_Position = projTransform * vPosition");
		}
		if(mPassScreenPos) {
			shaderParser.addVarying("vec4", "screenPos");
			shaderParser.appendVertexMain("screenPos = gl_Position");
			fsDecl.localDeclare("vec2", "screenTexCoords", "vec2(screenPos.x/screenPos.w*0.5+0.5,screenPos.y/screenPos.w*0.5+0.5)");
		}
	}

	@Override
	public void initHandles(GLProgram program) {
		program.nextTextureLevel();
		mBackTextureHandle = program.getUniformLocation("texSamplerBack");
		mBackTextureLevel = program.nextTextureLevel();
	}

	@Override
	public final void passData(GLProgram program) {
		program.setUniformInt(mBackTextureHandle, mBackTextureLevel);
	}

	@Override
	public boolean passesData() {
		return mTwoFacedTextures;
	}

}
