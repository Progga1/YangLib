package yang.graphics.defaults.programs.subshaders.discarding;

import yang.graphics.programs.GLProgram;
import yang.graphics.programs.permutations.ShaderDeclarations;
import yang.graphics.programs.permutations.ShaderPermutationsParser;
import yang.graphics.programs.permutations.SubShader;
import yang.math.objects.Point3f;
import yang.math.objects.Vector3f;


public class PlaneClipSubShader extends SubShader {

	public Point3f mPlaneBase;
	public Vector3f mPlaneNormal;

	public int mPlaneBaseHandle,mPlaneNormalHandle;

	public PlaneClipSubShader(Point3f planeBase,Vector3f planeNormal) {
		mPlaneBase = planeBase;
		mPlaneNormal = planeNormal;
	}

	public PlaneClipSubShader() {
		this(new Point3f(0,0,0),new Vector3f(0,1,0));
	}

	@Override
	public void setVariables(ShaderPermutationsParser shaderParser,ShaderDeclarations vsDecl,ShaderDeclarations fsDecl) {
		fsDecl.addUniform("vec3", "planeBase");
		fsDecl.addUniform("vec3", "planeNormal");
		shaderParser.appendLn(VAR_FS_MAIN,"vec3 posVec = worldPosition.xyz-planeBase;");
		shaderParser.appendLn(VAR_FS_MAIN,"if(dot(planeNormal,posVec)<0.0) discard;");
	}

	@Override
	public boolean passesData() {
		return true;
	}

	@Override
	public void initHandles(GLProgram program) {
		mPlaneBaseHandle = program.getUniformLocation("planeBase");
		mPlaneNormalHandle = program.getUniformLocation("planeNormal");
	}

	@Override
	public void passData(GLProgram program) {
		program.setUniform3f(mPlaneBaseHandle, mPlaneBase);
		program.setUniform3f(mPlaneNormalHandle, mPlaneNormal);
	}

}
