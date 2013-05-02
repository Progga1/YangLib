package yang.graphics.defaults;

import yang.graphics.programs.GLProgram;

public abstract class DefaultPCGLProgram extends GLProgram {

	protected String evaluateMacro(String key,String value) {
		if(key.equals("PC"))
			return value;
		else
			return null;
	}
	
}
