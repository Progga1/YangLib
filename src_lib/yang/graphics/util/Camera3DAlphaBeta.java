package yang.graphics.util;

public class Camera3DAlphaBeta {

	protected Camera3D mCamera;
	
	public float mViewAlpha,mViewBeta;
	public float mZoom;
	public float mFocusX,mFocusY,mFocusZ;
	
	public Camera3DAlphaBeta() {
		mCamera = new Camera3D();
	}
	
	public Camera3D getUpdatedCamera() {
		mCamera.setAlphaBeta(mViewAlpha,mViewBeta, mZoom, mFocusX,mFocusY,mFocusZ);
		return mCamera;
	}
	

	public Camera3D getCameraInstance() {
		return mCamera;
	}	
	
}
