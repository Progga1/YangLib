package yang.graphics.defaults.programs.subshaders;

import yang.graphics.programs.GLProgram;
import yang.graphics.programs.permutations.ShaderDeclarations;
import yang.graphics.programs.permutations.ShaderPermutationsParser;
import yang.graphics.programs.permutations.SubShader;
import yang.graphics.util.Camera3D;

public class LegacyCameraPerVertexVectorSubShader extends SubShader {

	public Camera3D mCamera;
	public int mCamPosHandle;
	
	public LegacyCameraPerVertexVectorSubShader(Camera3D camera) {
		mCamera = camera;
	}
	
	@Override
	public void setVariables(ShaderPermutationsParser shaderParser, ShaderDeclarations vsDecl, ShaderDeclarations fsDecl) {
		vsDecl.addUniform("vec3", "camPos");
		shaderParser.addVarying("vec3", "camDir");
		shaderParser.appendVertexMain("camDir = normalize(camPos-vec3(worldPosition.x,worldPosition.y,worldPosition.z))");
	}

	@Override
	public void initHandles(GLProgram program) {
		mCamPosHandle = program.getUniformLocation("camPos");
	}

	@Override
	public void passData(GLProgram program) {
		program.setUniform3f(mCamPosHandle, mCamera.mEyeX,mCamera.mEyeY,mCamera.mEyeZ);
	}

	@Override
	public boolean passesData() {
		return mCamera!=null;
	}
	
}
