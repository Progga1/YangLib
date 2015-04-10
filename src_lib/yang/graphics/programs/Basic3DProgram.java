package yang.graphics.programs;

import yang.graphics.camera.Camera3D;
import yang.math.objects.Point3f;

public class Basic3DProgram extends BasicProgram {

	public int mNormalHandle = -1;
	public int mEyePositionHandle = -1;
	public boolean mHasNormal = false;
	public boolean mHasEyePosition = false;

	public Basic3DProgram(int precPosition,int precTexCoord,int precColor) {
		super(precPosition,precTexCoord,precColor);
	}

	public Basic3DProgram() {
		super();
	}

	@Override
	protected void initHandles() {
		super.initHandles();
		mNormalHandle = mProgram.getAttributeLocation("vNormal");
		mEyePositionHandle = mProgram.getUniformLocation("eyePosition");
	}

	@Override
	public void postInit() {
		super.postInit();
		mHasNormal = (mNormalHandle>=0);
		mHasEyePosition = (mEyePositionHandle>=0);
	}

	public void setCamera(Camera3D camera) {
		if(mHasEyePosition) {
			Point3f eye = camera.getPositionReference();
			mProgram.setUniform4f(mEyePositionHandle, eye.mX,eye.mY,eye.mZ, 0);
		}
	}

}
