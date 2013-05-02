package yang.graphics.programs;

import yang.graphics.util.Camera3D;

public class Basic3DProgram extends BasicProgram {

	public int mNormalHandle = -1;
	public int mEyePositionHandle = -1;
	public boolean mHasNormal = false;
	public boolean mHasEyePosition = false;
	
	protected void initHandles() {
		super.initHandles();
		mNormalHandle = mProgram.getAttributeLocation("vNormal");
		mEyePositionHandle = mProgram.getUniformLocation("eyePosition");
	}
	
	public void postInit() {
		super.postInit();
		mHasNormal = (mNormalHandle>=0);
		mHasEyePosition = (mEyePositionHandle>=0);
	}
	
	public void setCamera(Camera3D camera) {
		if(mHasEyePosition)
			mProgram.setUniform4f(mEyePositionHandle, camera.mEyeX, camera.mEyeY, camera.mEyeZ, 0);
	}
	
}
