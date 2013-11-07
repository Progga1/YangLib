package yang.graphics.programs.permutations;

import java.util.HashMap;

import yang.util.YangList;

public class ShaderDeclarations {

	public YangList<ShaderDeclaration> mDeclarations;
	protected ShaderPermutationsParser mParser;
	protected String mParserKey;
	public HashMap<String,String> mLocalDeclarations;

	public ShaderDeclarations(ShaderPermutationsParser parser,String parserKey) {
		mDeclarations = new YangList<ShaderDeclaration>();
		mLocalDeclarations = new HashMap<String,String>(32);
		mParserKey = parserKey;
		mParser = parser;
	}

	public boolean localVariableExists(String name) {
		return mLocalDeclarations.containsKey(name);
	}

	public boolean localVariableExists(String type,String name) {
		final String t = mLocalDeclarations.get(name);
		if(t==null)
			return false;
		if(t!=type)
			throw new RuntimeException("Local variable "+name+" has different type: "+type+"!="+t);
		return true;
	}

	public void localDeclare(String type,String name,String initValue) {
		if(mLocalDeclarations.containsKey(name))
			throw new RuntimeException("Local variable already exists: "+name);
		mLocalDeclarations.put(name,type);
		if(initValue==null)
			mParser.appendLn(mParserKey, type+" "+name);
		else
			mParser.appendLn(mParserKey, type+" "+name+" = "+initValue);
	}

	public void localDeclare(String type,String name) {
		localDeclare(type,name,null);
	}

	public void localDeclareOrChange(String type,String name,String op,String value) {
		final String prevType = mLocalDeclarations.get(name);
		if(prevType==null)
			localDeclare(type,name,value);
		else{
			if(!prevType.equals(type))
				throw new RuntimeException("Usage of local var with different types: "+name+" ("+prevType+"!="+type+")");
			mParser.appendLn(mParserKey, name+" "+op+" "+value);
		}
	}

	public void localChange(String name,String op,String value) {
		final String prevType = mLocalDeclarations.get(name);
		if(prevType==null)
			throw new RuntimeException("Local variable does not exist: "+name);
		mParser.appendLn(mParserKey, name+" "+op+" "+value);
	}

	public void localDeclareOrAssign(String type,String name,String value) {
		localDeclareOrChange(type,name,"=",value);
	}

	public void localDeclareOrAdd(String type,String name,String value) {
		localDeclareOrChange(type,name,"+=",value);
	}

	public void localDeclareOrMult(String type,String name,String value) {
		localDeclareOrChange(type,name,"*=",value);
	}

	public void localAssign(String name,String value) {
		localChange(name,"=",value);
	}

	public void localAdd(String name,String value) {
		localChange(name,"+=",value);
	}

	public void localMult(String name,String value) {
		localChange(name,"*=",value);
	}

	public String getLocalVariableType(String name) {
		return mLocalDeclarations.get(name);
	}

	public ShaderDeclaration add(ShaderDeclaration declaration) {

		final ShaderDeclaration prev = getDeclaration(declaration.mDeclarationString);
		if(prev==null) {
			mParser.addGlobalVariable(declaration);
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
		for(final ShaderDeclaration shaderDecl:mDeclarations) {
			if(shaderDecl.mDeclarationString.equals(declarationString))
				return shaderDecl;
		}
		return null;
	}

	public boolean hasDeclaration(String declarationString) {
		return getDeclaration(declarationString)!=null;
	}

	public boolean hasDeclaration(int varType,String type,String name) {
		for(final ShaderDeclaration shaderDecl:mDeclarations) {
			if(shaderDecl.equals(varType,type,name))
				return true;
		}
		return false;
	}

	public void add(String declaration) {
		add(new ShaderDeclaration(declaration));
	}

	@Override
	public String toString() {
		return "----DECLARATIONS OF '"+mParserKey+"'----\n\n----Global----\n"+mDeclarations+"\n\n"+"----Local----\n"+mLocalDeclarations;
	}

}
