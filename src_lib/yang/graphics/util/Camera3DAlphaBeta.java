package yang.graphics.util;

public class Camera3DAlphaBeta {

	protected Camera3D mCamera;
	
	public float mViewAlpha,mViewBeta;
	public float mZoom;
	public float mFocusX,mFocusY,mFocusZ;
	public boolean 	mInvertView = false;
	
	public Camera3DAlphaBeta() {
		mCamera = new Camera3D();
	}
	
	public Camera3D getUpdatedCamera() {
		if(mInvertView)
			mCamera.setOutwardsAlphaBeta(mViewAlpha,mViewBeta, mZoom, mFocusX,mFocusY,mFocusZ);
		else
			mCamera.setAlphaBeta(mViewAlpha,mViewBeta, mZoom, mFocusX,mFocusY,mFocusZ);
		return mCamera;
	}
	

	public Camera3D getCameraInstance() {
		return mCamera;
	}	
	
	public void setFocus(float x,float y,float z) {
		mFocusX = x;
		mFocusY = y;
		mFocusZ = z;
	}
	
	public void shiftFocus(float dx, float dy, float dz) {
		mFocusX += dx;
		mFocusY += dy;
		mFocusZ += dz;
	}
	
}
