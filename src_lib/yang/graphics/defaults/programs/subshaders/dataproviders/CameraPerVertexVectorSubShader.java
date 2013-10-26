package yang.graphics.defaults.programs.subshaders.dataproviders;

import yang.graphics.defaults.Default3DGraphics;
import yang.graphics.programs.GLProgram;
import yang.graphics.programs.permutations.ShaderDeclarations;
import yang.graphics.programs.permutations.ShaderPermutationsParser;
import yang.graphics.programs.permutations.SubShader;
import yang.math.objects.Point3f;

public class CameraPerVertexVectorSubShader extends SubShader {

	public Point3f mCameraPosition;
	public int mCamPosHandle;

	public CameraPerVertexVectorSubShader(Point3f cameraPosReference) {
		mCameraPosition = cameraPosReference;
	}

	public CameraPerVertexVectorSubShader(Default3DGraphics graphics3D) {
		this(graphics3D.getCameraPosition());
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
		program.setUniform3f(mCamPosHandle, mCameraPosition);
	}

	@Override
	public boolean passesData() {
		return mCameraPosition!=null;
	}

}
