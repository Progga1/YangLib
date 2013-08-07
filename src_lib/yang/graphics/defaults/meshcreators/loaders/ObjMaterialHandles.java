package yang.graphics.defaults.meshcreators.loaders;

import yang.graphics.defaults.programs.subshaders.EmissiveSubShader;
import yang.graphics.defaults.programs.subshaders.SpecularLightBasicSubShader;
import yang.graphics.programs.AbstractProgram;
import yang.graphics.programs.GLProgram;

public class ObjMaterialHandles {

	public int mDiffuseColorHandle;
	public EmissiveSubShader mEmisShader;
	public SpecularLightBasicSubShader mSpecShader;
	
	private EmissiveSubShader tempEmisShader;
	private SpecularLightBasicSubShader tempSpecShader;
	
	public ObjMaterialHandles() {
		tempEmisShader = new EmissiveSubShader(null);
		tempSpecShader = new SpecularLightBasicSubShader(null);
	}
	
	public ObjMaterialHandles(AbstractProgram shader) {
		this();
		refreshHandles(shader);
	}
	
	public ObjMaterialHandles refreshHandles(AbstractProgram shader) {
		GLProgram program = shader.getProgram();
		
		mDiffuseColorHandle = program.getUniformLocation("diffuseColor");
		
		tempSpecShader.initHandles(program);
		mSpecShader = tempSpecShader.handlesOK()?tempSpecShader:null;
		tempEmisShader.initHandles(program);
		mEmisShader = tempEmisShader.handlesOK()?tempEmisShader:null;
			
		return this;
	}
	
	
	
}
