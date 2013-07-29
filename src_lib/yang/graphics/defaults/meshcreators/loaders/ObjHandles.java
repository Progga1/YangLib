package yang.graphics.defaults.meshcreators.loaders;

import yang.graphics.programs.AbstractProgram;
import yang.graphics.programs.GLProgram;

public class ObjHandles {

	public int mDiffuseColorHandle;
	public int mSpecColorHandle;
	public int mSpecUseTexHandle;
	public int mSpecTexSampler;
	public int mSpecExponentHandle;
	
	public ObjHandles() {
		
	}
	
	public ObjHandles(AbstractProgram shader) {
		setHandles(shader);
	}
	
	public ObjHandles setHandles(AbstractProgram shader) {
		GLProgram program = shader.getProgram();
		mDiffuseColorHandle = program.getUniformLocation("diffuseColor");
		mSpecColorHandle = program.getUniformLocation("mtSpecColor");
		mSpecExponentHandle = program.getUniformLocation("mtSpecExponent");
		mSpecUseTexHandle = program.getUniformLocation("mtSpecUseTex");
		mSpecTexSampler = program.getUniformLocation("mtSpecSampler");
		return this;
	}
	
}
