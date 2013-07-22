package yang.graphics.defaults.programs.subshaders;

import yang.graphics.programs.GLProgram;
import yang.graphics.programs.permutations.ShaderDeclarations;
import yang.graphics.programs.permutations.ShaderPermutationsParser;
import yang.graphics.programs.permutations.SubShader;

public class CameraVectorSubShader extends SubShader {

	public float[] mCameraMatrix;
	public int mCamDirHandle;
	
	public CameraVectorSubShader(float[] cameraMatrix) {
		mCameraMatrix = cameraMatrix;
	}
	
	@Override
	public void setVariables(ShaderPermutationsParser shaderParser, ShaderDeclarations vsDecl, ShaderDeclarations fsDecl) {
		fsDecl.addUniform("vec3", "camDir");
	}

	@Override
	public void initHandles(GLProgram program) {
		mCamDirHandle = program.getUniformLocation("camDir");
	}

	@Override
	public void passData(GLProgram program) {
		program.setUniform3f(mCamDirHandle, mCameraMatrix[2], mCameraMatrix[6], mCameraMatrix[10]);
	}

	@Override
	public boolean passesData() {
		return mCameraMatrix!=null;
	}
	
}
