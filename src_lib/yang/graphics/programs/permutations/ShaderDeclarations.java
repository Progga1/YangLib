package yang.graphics.programs.permutations;

import yang.util.NonConcurrentList;

public class ShaderDeclarations {

	public NonConcurrentList<ShaderDeclaration> mDeclarations;
	
	public ShaderDeclarations() {
		mDeclarations = new NonConcurrentList<ShaderDeclaration>();
	}

	public ShaderDeclaration add(ShaderDeclaration declaration) {
		ShaderDeclaration prev = getDeclaration(declaration.mDeclarationString);
		if(prev==null) {
			mDeclarations.add(declaration);
			return declaration;
		}else
			return prev;
	}
	
	public ShaderDeclaration add(int variableType,String type,String name) {
		return add(new ShaderDeclaration(variableType,type,name));
	}
	
	public ShaderDeclaration addUniform(String type,String name) {
		return add(ShaderDeclaration.T_UNIFORM,type,name);
	}
	
	public ShaderDeclaration addAttribute(String type,String name) {
		return add(ShaderDeclaration.T_ATTRIBUTE,type,name);
	}
	
	public ShaderDeclaration addVarying(String type,String name) {
		return add(ShaderDeclaration.T_VARYING,type,name);
	}
	
	public ShaderDeclaration getDeclaration(String declarationString) {
		for(ShaderDeclaration shaderDecl:mDeclarations) {
			if(shaderDecl.mDeclarationString.equals(declarationString))
				return shaderDecl;
		}
		return null;
	}
	
	public boolean hasDeclaration(String declarationString) {
		return getDeclaration(declarationString)!=null;
	}
	
	public boolean hasDeclaration(int varType,String type,String name) {
		for(ShaderDeclaration shaderDecl:mDeclarations) {
			if(shaderDecl.equals(varType,type,name))
				return true;
		}
		return false;
	}

	public void add(String declaration) {
		add(new ShaderDeclaration(declaration));
	}
	
}
