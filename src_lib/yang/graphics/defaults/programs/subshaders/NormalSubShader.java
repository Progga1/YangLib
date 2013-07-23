package yang.graphics.defaults.programs.subshaders;

import yang.graphics.programs.permutations.ShaderDeclarations;
import yang.graphics.programs.permutations.ShaderPermutationsParser;
import yang.graphics.programs.permutations.SubShader;

public class NormalSubShader extends SubShader {
	
	public boolean mPhongShading;
	public boolean mNormalize;
	
	public NormalSubShader(boolean phongShading,boolean normalize) {
		mPhongShading = phongShading;
		mNormalize = normalize;
	}
	
	@Override
	public void setVariables(ShaderPermutationsParser shaderParser,ShaderDeclarations vsDecl,ShaderDeclarations fsDecl) {
		vsDecl.addUniform("mat3","normalTransform");
		vsDecl.addAttribute("vec3", "vNormal");
		if(mPhongShading) {
			shaderParser.addVarying("vec3","varNormal");
			shaderParser.appendVertexMain("varNormal = normalTransform * vNormal");
			if(mNormalize)
				shaderParser.appendFragmentMain("vec3 normal = normalize(varNormal)");
			else
				shaderParser.appendFragmentMain("vec3 normal = varNormal");

		}else{
			if(mNormalize)
				shaderParser.appendVertexMain("vec3 normal = normalTransform * normalize(vNormal)");
			else
				shaderParser.appendVertexMain("vec3 normal = normalTransform * vNormal"); //TODO better solution
		}
	}
}
