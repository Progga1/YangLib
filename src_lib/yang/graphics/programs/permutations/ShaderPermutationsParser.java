package yang.graphics.programs.permutations;

import java.util.HashMap;

import yang.util.NonConcurrentList;

public class ShaderPermutationsParser {

	private ShaderPermutations mPermutations;
	public HashMap<String,String> mVariables;
	public ShaderDeclarations mVSDeclarations;
	public ShaderDeclarations mFSDeclarations;
	
	public ShaderPermutationsParser(ShaderPermutations permutations) {
		mPermutations = permutations;
		mVariables = new HashMap<String,String>(32);
		mVSDeclarations = new ShaderDeclarations();
		mFSDeclarations = new ShaderDeclarations();
	}
	
	public void setVariable(String key,String value) {
		mVariables.put(key,value);
	}
	
	public void appendLn(String key,String value) {
		String var = mVariables.get(key);
		if(var==null)
			mVariables.put(key, value+ShaderPermutations.LINE_END);
		else
			mVariables.put(key, var+value+ShaderPermutations.LINE_END);
	}
	
	public void appendOp(String key,String value,String op) {
		String var = mVariables.get(key);
		if(var==null)
			mVariables.put(key, value);
		else
			mVariables.put(key, var+op+value);
	}
	
	public void incVariable(String key,int value) {
		String var = mVariables.get(key);
		if(var==null || var.trim().equals(""))
			mVariables.put(key, ""+value);
		else{
			int val = Integer.parseInt(var);
			mVariables.put(key, ""+(value+val));
		}
			
	}
	
	public String getVariable(String key,String defaultVal) {
		String var = mVariables.get(key);
		if(var==null)
			return defaultVal;
		else
			return var;
	}
	
	public String getVariable(String key) {
		return getVariable(key,"");
	}
	
	public void vsDeclaration(String declaration) {
		mVSDeclarations.add(declaration);
	}
	
	public void fsDeclaration(String declaration) {
		mFSDeclarations.add(declaration);
	}
	
}
