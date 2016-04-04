package yang.graphics.util;

import yang.graphics.camera.Camera3D;

public class Camera3DAlphaBeta extends Camera3D {

	public float mViewAlpha,mViewBeta;
	public float mZoom;
	public float mFocusX,mFocusY,mFocusZ;
	public boolean 	mInvertView = false;

	public Camera3DAlphaBeta() {

	}

	public Camera3DAlphaBeta update() {
		if(mInvertView)
			setLookOutwardsAlphaBeta(mViewAlpha,mViewBeta,0, mZoom, mFocusX,mFocusY,mFocusZ);
		else
			setLookAtAlphaBeta(mViewAlpha,mViewBeta, mZoom, mFocusX,mFocusY,mFocusZ);
		return this;
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
