package yang.graphics.defaults;

import yang.graphics.programs.GLProgram;

public abstract class DefaultPCGLProgram extends GLProgram {

	@Override
	protected String evaluateMacro(String key,String value) {
		if(key.equals("PC") || key.equals("LOWP") || key.equals("MEDIUMP") || key.equals("HIGHP"))
			return value;
		else
			return null;
	}

}
