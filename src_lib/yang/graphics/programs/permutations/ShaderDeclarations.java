package yang.graphics.programs.permutations;

import yang.util.NonConcurrentList;

public class ShaderDeclarations {

	public NonConcurrentList<ShaderDeclaration> mDeclarations;
	
	public ShaderDeclarations() {
		mDeclarations = new NonConcurrentList<ShaderDeclaration>();
	}

	public ShaderDeclaration add(ShaderDeclaration declaration) {
		mDeclarations.add(declaration);
		return declaration;
	}
	
	public ShaderDeclaration add(int variableType,String type,String name) {
		ShaderDeclaration decl = new ShaderDeclaration(variableType,type,name);
		mDeclarations.add(decl);
		return decl;
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
	
	public boolean hasDeclaration(String declarationString) {
		for(ShaderDeclaration shaderDecl:mDeclarations) {
			if(shaderDecl.mDeclarationString.equals(declarationString))
				return true;
		}
		return false;
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
