package yang.graphics.defaults.programs.subshaders;

import yang.graphics.programs.GLProgram;
import yang.graphics.programs.permutations.ShaderDeclarations;
import yang.graphics.programs.permutations.ShaderPermutationsParser;
import yang.graphics.programs.permutations.SubShader;

public class NormalSubShader extends SubShader {
	
	public boolean mPhongShading;
	
	public NormalSubShader(boolean phongShading) {
		mPhongShading = phongShading;
	}
	
	@Override
	public void setVariables(ShaderPermutationsParser shaderParser,ShaderDeclarations vsDecl,ShaderDeclarations fsDecl) {
		vsDecl.addAttribute("vec3", "vNormal");
		if(mPhongShading) {
			shaderParser.addVarying("vec3","normal");
			shaderParser.appendVertexMain("normal = vNormal");
			//shaderParser.appendFragmentMain("normal = normalize(normal)");
		}else{
			
		}
	}

	@Override
	public void initHandles(GLProgram program) {
		
	}
	
	@Override
	public void passData(GLProgram program) {
		
	}
}
