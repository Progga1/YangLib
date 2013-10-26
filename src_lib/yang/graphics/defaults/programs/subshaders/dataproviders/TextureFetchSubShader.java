package yang.graphics.defaults.programs.subshaders.dataproviders;

import yang.graphics.programs.GLProgram;
import yang.graphics.programs.permutations.ShaderDeclarations;
import yang.graphics.programs.permutations.ShaderPermutationsParser;
import yang.graphics.programs.permutations.SubShader;

public class TextureFetchSubShader extends SubShader {

	public int mTexSampler;
	public int mTextureLevel;
	public String mCoordinatesVec;
	public String mOutVariable;
	public float mBlur;
	public String mTexSamplerName;

	public TextureFetchSubShader(String outVariable,String coordinatesVec,float blur) {
		mCoordinatesVec = coordinatesVec;
		mOutVariable = outVariable;
		mBlur = blur;
	}

	public TextureFetchSubShader(String outVariable,String coordinatesVec) {
		this(outVariable,coordinatesVec,0);
	}

	@Override
	public void setVariables(ShaderPermutationsParser shaderParser, ShaderDeclarations vsDecl, ShaderDeclarations fsDecl) {
		mTexSamplerName = shaderParser.getFreeGlobalName("texFetchSampler");
		fsDecl.addUniform("sampler2D", mTexSamplerName);
		fsDecl.localDeclareOrAssign("vec4",mOutVariable,"texture2D("+mTexSamplerName+","+mCoordinatesVec+")");
		if(mBlur>0) {
			fsDecl.localAdd(mOutVariable,"texture2D("+mTexSamplerName+","+mCoordinatesVec+" + vec2("+mBlur+",0.0)"+")");
			fsDecl.localAdd(mOutVariable,"texture2D("+mTexSamplerName+","+mCoordinatesVec+" + vec2(0.0-"+mBlur+",0.0)"+")");
			fsDecl.localAdd(mOutVariable,"texture2D("+mTexSamplerName+","+mCoordinatesVec+" + vec2(0.0,"+mBlur+")"+")");
			fsDecl.localAdd(mOutVariable,"texture2D("+mTexSamplerName+","+mCoordinatesVec+" + vec2(0.0,0.0-"+mBlur+")"+")");
			fsDecl.localMult(mOutVariable,""+1f/5);
		}
	}

	@Override
	public void initHandles(GLProgram program) {
		mTextureLevel = program.nextTextureLevel();
		mTexSampler = program.getUniformLocation(mTexSamplerName);
	}

	@Override
	public boolean passesData() {
		return true;
	}

	@Override
	public void passData(GLProgram program) {
		program.setUniformInt(mTexSampler, mTextureLevel);
	}

}
