package yang.graphics.defaults.programs.subshaders.realistic;

import yang.graphics.programs.GLProgram;
import yang.graphics.programs.permutations.ShaderDeclarations;
import yang.graphics.programs.permutations.ShaderPermutationsParser;
import yang.graphics.programs.permutations.SubShader;
import yang.math.objects.Quadruple;

public class ShadowSubShader extends SubShader {

	public int mDepthTexSampler;
	public int mDepthMapTransformHandle;
	public int mTextureLevel;
	public int mPropertiesHandle;
	public Quadruple mShadowProperties;
	public float[] mDepthMapTransform;

	public ShadowSubShader(float[] depthMapTransform,Quadruple shadowProperties) {
		mDepthMapTransform = depthMapTransform;
		mShadowProperties = shadowProperties;
	}

	@Override
	public void setVariables(ShaderPermutationsParser shaderParser, ShaderDeclarations vsDecl, ShaderDeclarations fsDecl) {
		vsDecl.addUniform("mat4", "depthMapTransform");
		fsDecl.addUniform("sampler2D", "depthSampler");
		fsDecl.addUniform("vec4", "shadowProperties");
		shaderParser.addVarying("vec4", "depthMapPosition");
		shaderParser.appendLn(VAR_VS_MAIN, "depthMapPosition = depthMapTransform * worldPos");
		shaderParser.appendLn(VAR_FS_MAIN,"vec4 depthValue = texture2D(depthSampler, vec2(depthMapPosition.x,depthMapPosition.y))");
		final String shad = "vec4(shadowProperties[0],shadowProperties[0],shadowProperties[0],1.0)";
		if(shaderParser.getVariable("lgt")==null)
			shaderParser.appendLn(VAR_FS_MAIN,"vec4 shad = vec4(1.0,1.0,1.0,1.0)");
		shaderParser.append(VAR_FS_MAIN,"if(depthMapPosition.z < depthValue.r) {\n");
		if(shaderParser.getVariable("lgt")!=null) {
			shaderParser.appendLn(VAR_FS_MAIN, "lgt = min(lgt,"+shad+")");
		}else{
			shaderParser.appendLn(VAR_FS_MAIN,"shad = "+shad);
			//shaderParser.circumOp(VAR_FRAGCOLOR, "min(shad,",")");
			shaderParser.appendOp(VAR_FRAGCOLOR, "shad", "*");
		}
		shaderParser.append(VAR_FS_MAIN,"}\n");

	}

	@Override
	public void initHandles(GLProgram program) {
		mTextureLevel = program.nextTextureLevel();
		mDepthTexSampler = program.getUniformLocation("depthSampler");
		mDepthMapTransformHandle = program.getUniformLocation("depthMapTransform");
		mPropertiesHandle = program.getUniformLocation("shadowProperties");
	}

	@Override
	public boolean passesData() {
		return mDepthMapTransform!=null;
	}

	@Override
	public void passData(GLProgram program) {
		program.setUniformInt(mDepthTexSampler, mTextureLevel);
		program.setUniform4f(mPropertiesHandle, mShadowProperties.mValues);
	}

}
