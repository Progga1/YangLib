package yang.graphics.programs.permutations;

import java.util.HashMap;

import yang.util.NonConcurrentList;

public class ShaderPermutationsParser {

	private ShaderPermutations mPermutations;
	public HashMap<String,String> mVariables;
	public NonConcurrentList<String> mVSDeclarations;
	public NonConcurrentList<String> mFSDeclarations;
	
	public ShaderPermutationsParser(ShaderPermutations permutations) {
		mPermutations = permutations;
		mVariables = new HashMap<String,String>(32);
		mVSDeclarations = new NonConcurrentList<String>();
		mFSDeclarations = new NonConcurrentList<String>();
	}
	
	public void setVariable(String key,String value) {
		mVariables.put(key,value);
	}
	
	public void appendVariable(String key,String value) {
		String var = mVariables.get(key);
		if(var==null)
			mVariables.put(key, value);
		else
			mVariables.put(key, var+value);
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
	
	public String getVariable(String key) {
		String var = mVariables.get(key);
		if(var==null)
			return "";
		else
			return var;
	}

	public boolean hasVSDeclaration(String declaration) {
		for(String vsDec:mVSDeclarations) {
			if(vsDec.equals(declaration))
				return true;
		}
		return false;
	}
	
	public boolean hasFSDeclaration(String declaration) {
		for(String fsDec:mFSDeclarations) {
			if(fsDec.equals(declaration))
				return true;
		}
		return false;
	}
	
	public void vsDeclaration(String declaration) {
		if(!hasVSDeclaration(declaration))
			mVSDeclarations.add(declaration);
	}
	
	public void fsDeclaration(String declaration) {
		if(!hasFSDeclaration(declaration))
			mFSDeclarations.add(declaration);
	}
	
}
