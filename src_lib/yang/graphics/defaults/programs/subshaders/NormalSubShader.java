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
		vsDecl.addAttribute("vec4", "vNormal");
		if(mPhongShading) {
			shaderParser.addVarying("vec4","normal");
			shaderParser.appendLn("VS_MAIN", "normal = vNormal");
		}else{
			
		}
	}

	@Override
	public void initHandles() {
		
	}
	
	@Override
	public void passData(GLProgram program) {
		
	}
}
