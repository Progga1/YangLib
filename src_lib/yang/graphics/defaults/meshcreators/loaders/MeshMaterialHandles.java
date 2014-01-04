package yang.graphics.defaults.meshcreators.loaders;

import yang.graphics.defaults.programs.subshaders.EmissiveSubShader;
import yang.graphics.defaults.programs.subshaders.SpecularLightBasicSubShader;
import yang.graphics.programs.AbstractProgram;
import yang.graphics.programs.GLProgram;

public class MeshMaterialHandles {

	public int mDiffuseColorHandle;
	public EmissiveSubShader mEmisShader;
	public SpecularLightBasicSubShader mSpecShader;
	
	private EmissiveSubShader tempEmisShader;
	private SpecularLightBasicSubShader tempSpecShader;
	
	public MeshMaterialHandles() {
		tempEmisShader = new EmissiveSubShader(null);
		tempSpecShader = new SpecularLightBasicSubShader(null);
	}
	
	public MeshMaterialHandles(AbstractProgram shader) {
		this();
		refreshHandles(shader);
	}
	
	public MeshMaterialHandles refreshHandles(AbstractProgram shader) {
		GLProgram program = shader.getProgram();
		
		mDiffuseColorHandle = program.getUniformLocation("diffuseColor");
		
		tempSpecShader.initHandles(program);
		mSpecShader = tempSpecShader.handlesOK()?tempSpecShader:null;
		tempEmisShader.initHandles(program);
		mEmisShader = tempEmisShader.handlesOK()?tempEmisShader:null;
			
		return this;
	}
	
	
	
}
