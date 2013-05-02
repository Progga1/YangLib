package yang.graphics.defaults.programs;

import yang.graphics.AbstractGFXLoader;
import yang.util.Util;

public class WaterProgram extends ShadowProgram {

	public static int NORMAL_TEXTURE_LEVEL = 2;
	public static int HEIGHT_TEXTURE_LEVEL = 3;
	
	public int mNormalTexHandle;
	public int mHeightTexHandle;
	public int mCameraVectorHandle;
	
	@Override
	protected String getVertexShader(AbstractGFXLoader gfxLoader) {
		return gfxLoader.getShader("water_vertex");
	}

	@Override
	protected String getFragmentShader(AbstractGFXLoader gfxLoader) {
		return gfxLoader.getShader("water_fragment");
	}
	
	@Override
	protected void initHandles() {
		super.initHandles();
		mNormalTexHandle = mProgram.getUniformLocation("normalSampler");
		mHeightTexHandle = mProgram.getUniformLocation("heightSampler");
		mCameraVectorHandle = mProgram.getUniformLocation("camVector");
	}
	
	@Override
	public void activate() {
		super.activate();
		mProgram.setUniformInt(mNormalTexHandle, NORMAL_TEXTURE_LEVEL);
		mProgram.setUniformInt(mHeightTexHandle, HEIGHT_TEXTURE_LEVEL);
	}
	
	public void setCameraVector(float dirX, float dirY, float dirZ, boolean b) {
		if(mCameraVectorHandle>=0) {
			float dist = 1/Util.getDistance(dirX, dirY, dirZ);
			mProgram.setUniform4f(mCameraVectorHandle, dirX*dist, dirY*dist, dirZ*dist, 0);
		}
	}
	
	public void setCameraVector(float dirX,float dirY,float dirZ) {
		if(mCameraVectorHandle>=0)
			mProgram.setUniform4f(mCameraVectorHandle, dirX, dirY, dirZ, 0);
	}
	
}
