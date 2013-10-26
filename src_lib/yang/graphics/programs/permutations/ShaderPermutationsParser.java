package yang.graphics.programs.permutations;

import java.util.HashMap;

public class ShaderPermutationsParser {

	private final ShaderPermutations mPermutations;
	public HashMap<String,String> mVariables;
	public ShaderDeclarations mVSDeclarations;
	public ShaderDeclarations mFSDeclarations;
	private final HashMap<String,ShaderDeclaration> mGlobalDeclarations;

	public ShaderPermutationsParser(ShaderPermutations permutations) {
		mPermutations = permutations;
		mVariables = new HashMap<String,String>(32);
		mGlobalDeclarations = new HashMap<String,ShaderDeclaration>(32);
		mVSDeclarations = new ShaderDeclarations(this,SubShader.VAR_VS_MAIN);
		mFSDeclarations = new ShaderDeclarations(this,SubShader.VAR_FS_MAIN);
	}

	public void setVariable(String key,String value) {
		mVariables.put(key,value);
	}


	public void append(String key,String value) {
		final String var = mVariables.get(key);
		if(var==null)
			mVariables.put(key, value);
		else
			mVariables.put(key, var+value);
	}

	public void appendLn(String key, String value) {
		append(key,value+ShaderPermutations.LINE_END);
	}

	public void appendOp(String key,String value,String op) {
		String var = mVariables.get(key);
		if(var==null)
			mVariables.put(key, value);
		else{
			if(op!="+" && op!="-") {
				var = "("+var+")";
			}
			mVariables.put(key, var+op+value);
		}
	}

	public void circumOp(String key, String prefix, String postfix) {
		final String var = mVariables.get(key);
		if(mVariables==null)
			throw new RuntimeException("Operation variable empty: "+key);
		mVariables.put(key, prefix+var+postfix);
	}

	public void incVariable(String key,int value) {
		final String var = mVariables.get(key);
		if(var==null || var.trim().equals(""))
			mVariables.put(key, ""+value);
		else{
			final int val = Integer.parseInt(var);
			mVariables.put(key, ""+(value+val));
		}

	}

	public String getVariable(String key,String defaultVal) {
		final String var = mVariables.get(key);
		if(var==null)
			return defaultVal;
		else
			return var;
	}

	public String getVariable(String key) {
		return getVariable(key,null);
	}

	public boolean hasVariable(String key) {
		return mVariables.containsKey(key);
	}

	public void vsDeclaration(String declaration) {
		mVSDeclarations.add(declaration);
	}

	public void fsDeclaration(String declaration) {
		mFSDeclarations.add(declaration);
	}

	public void addVarying(String type, String name) {
		mVSDeclarations.addVarying(type, name);
		mFSDeclarations.addVarying(type, name);
	}

	public void appendVertexMain(String string) {
		appendLn(SubShader.VAR_VS_MAIN,string);
	}

	public void appendFragmentMain(String string) {
		appendLn(SubShader.VAR_FS_MAIN,string);
	}

	public void addGlobalVariable(ShaderDeclaration declaration) {
		mGlobalDeclarations.put(declaration.mName,declaration);
	}

	public boolean hasGlobalVariable(String name) {
		return mGlobalDeclarations.containsKey(name);
	}

	public String getFreeGlobalName(String name) {
		String newName = name;
		int i=0;
		while(mGlobalDeclarations.containsKey(newName)) {
			i++;
			newName = name+i;
		}
		return newName;
	}

}
