package yang.graphics.util;

import yang.math.Geometry;

public class Camera3D {

	private float[] mLookVector = new float[4];
	private float[] mLookDirection = new float[4];
	public float mEyeX,mEyeY,mEyeZ;
	public float mLookAtX,mLookAtY,mLookAtZ;
	public float mUpX,mUpY,mUpZ;
	
	public Camera3D() {
		mLookVector[3] = 1;
		mLookDirection[3] = 0;
	}
	
	public Camera3D set(float eyeX,float eyeY,float eyeZ, float lookAtX,float lookAtY,float lookAtZ, float upX,float upY,float upZ) {
		mEyeX = eyeX;
		mEyeY = eyeY;
		mEyeZ = eyeZ;
		mLookAtX = lookAtX;
		mLookAtY = lookAtY;
		mLookAtZ = lookAtZ;
		mUpX = upX;
		mUpY = upY;
		mUpZ = upZ;
		return this;
	}
	
	public Camera3D set(float eyeX,float eyeY,float eyeZ, float lookAtX,float lookAtY,float lookAtZ) {
		return set(eyeX,eyeY,eyeZ, lookAtX,lookAtY,lookAtZ, 0,1,0);
	}
	
	public Camera3D set(Camera3D preface) {
		mEyeX = preface.mEyeX;
		mEyeY = preface.mEyeY;
		mEyeZ = preface.mEyeZ;
		mLookAtX = preface.mLookAtX;
		mLookAtY = preface.mLookAtY;
		mLookAtZ = preface.mLookAtZ;
		mUpX = preface.mUpX;
		mUpY = preface.mUpY;
		mUpZ = preface.mUpZ;
		return this;
	}
	
	public float[] getLookVector() {
		mLookVector[0] = mLookAtX-mEyeX;
		mLookVector[1] = mLookAtY-mEyeY;
		mLookVector[2] = mLookAtZ-mEyeZ;
		return mLookVector;
	}
	
	public float[] getLookDirection() {
		float dDist = 1f/Geometry.getDistance(getLookVector());
		mLookDirection[0] = mLookVector[0]*dDist;
		mLookDirection[1] = mLookVector[1]*dDist;
		mLookDirection[2] = mLookVector[2]*dDist;
		return mLookDirection;
	}

	public Camera3D setAlphaBeta(float alpha, float beta, float distance, float focusX,float focusY,float focusZ) {
		return set(focusX+(float)(Math.sin(alpha)*Math.cos(beta))*distance,
				focusY+(float)Math.sin(beta)*distance,
				focusZ+(float)(Math.cos(alpha)*Math.cos(beta))*distance,
				focusX,focusY,focusZ, 0,1,0);
	}
	
	public Camera3D setAlphaBeta(float alpha, float beta, float distance) {
		return setAlphaBeta(alpha,beta,distance,0,0,0);
	}
	
	public Camera3D setAlphaBeta(float alpha, float beta) {
		return setAlphaBeta(alpha,beta,1,0,0,0);
	}
	
}
