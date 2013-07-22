package yang.graphics.defaults.programs.subshaders;

import yang.graphics.programs.GLProgram;
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
		vsDecl.addAttribute("vec3", "vNormal");
		if(mPhongShading) {
			shaderParser.addVarying("vec3","varNormal");
			shaderParser.appendVertexMain("varNormal = vNormal");
			if(mNormalize)
				shaderParser.appendFragmentMain("vec3 normal = normalize(varNormal)");
			else
				shaderParser.appendFragmentMain("vec3 normal = varNormal");

		}else{
			if(mNormalize)
				shaderParser.appendVertexMain("vec3 normal = normalize(vNormal)");
			else
				shaderParser.appendVertexMain("vec3 normal = vNormal"); //TODO better solution
		}
	}

	@Override
	public void initHandles(GLProgram program) {
		
	}
	
	@Override
	public void passData(GLProgram program) {
		
	}
}
