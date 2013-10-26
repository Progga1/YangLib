package yang.graphics.programs.permutations;


public class ShaderDeclaration {

	public static final int T_CONST = 0;
	public static final int T_UNIFORM = 1;
	public static final int T_ATTRIBUTE = 2;
	public static final int T_VARYING = 3;

	public int mVariableType;
	public String mType;
	public String mName;
	public String mDeclarationString;

	public ShaderDeclaration(int variableType,String type,String name) {
		set(variableType,type,name);
	}

	public ShaderDeclaration(String declarationString) {
		final String[] split = declarationString.split(" ");
		if(split.length!=3)
			throw new RuntimeException("Invalid number of words: '"+declarationString+"'");
		set(stringToVarType(split[0]),split[1],split[2]);
	}

	public void set(int variableType,String type,String name) {
		mVariableType = variableType;
		mType = type;
		mName = name;
		mDeclarationString = varTypeToString()+" "+mType+" "+mName;
	}

	public static int stringToVarType(String varTypeString) {
		final String varType = varTypeString.trim().toLowerCase();
		if(varType.equals("uniform"))
			return ShaderDeclaration.T_UNIFORM;
		else if(varType.equals("attribute"))
			return ShaderDeclaration.T_ATTRIBUTE;
		else if(varType.equals("varying"))
			return ShaderDeclaration.T_VARYING;
		else if(varType.equals("const"))
			return ShaderDeclaration.T_CONST;
		else
			throw new RuntimeException("Unknown variable type: "+varType);
	}

	public String varTypeToString() {
		switch(mVariableType) {
		case 0: return "const";
		case 1: return "uniform";
		case 2: return "attribute";
		case 3: return "varying";
		default: throw new RuntimeException("Unknown variable type id: "+mVariableType);
		}
	}

	public boolean equals(int varType,String type,String name) {
		return varType==mVariableType && mType==type && mName==name;
	}

}
