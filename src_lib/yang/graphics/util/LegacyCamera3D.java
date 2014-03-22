package yang.graphics.util;

import yang.math.Geometry;
import yang.math.MatrixOps;
import yang.math.objects.Vector3f;
import yang.math.objects.YangMatrix;

public class LegacyCamera3D {

	public YangMatrix mViewMatrix = new YangMatrix();
	public YangMatrix mProjectionMatrix = new YangMatrix();

	private float[] mLookVector = new float[4];
	private float[] mLookDirection = new float[4];
	public float mEyeX,mEyeY,mEyeZ;
	public float mLookAtX,mLookAtY,mLookAtZ;
	public float mUpX,mUpY,mUpZ;

	public LegacyCamera3D() {
		super();
		mLookVector[3] = 1;
		mLookDirection[3] = 0;
		reset();
	}

	public void reset() {
		set(0,0,0, 0,0,-1);
	}

	public LegacyCamera3D set(float eyeX,float eyeY,float eyeZ, float lookAtX,float lookAtY,float lookAtZ, float upX,float upY,float upZ) {
		mEyeX = eyeX;
		mEyeY = eyeY;
		mEyeZ = eyeZ;
		mLookAtX = lookAtX;
		mLookAtY = lookAtY;
		mLookAtZ = lookAtZ;
		mUpX = upX;
		mUpY = upY;
		mUpZ = upZ;
		refreshMatrix();
		return this;
	}

	public LegacyCamera3D set(float eyeX,float eyeY,float eyeZ, float lookAtX,float lookAtY,float lookAtZ) {
		return set(eyeX,eyeY,eyeZ, lookAtX,lookAtY,lookAtZ, 0,1,0);
	}

	public LegacyCamera3D set(LegacyCamera3D preface) {
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

	public void refreshMatrix() {
		MatrixOps.setLookAtInverse(mViewMatrix.mValues, mEyeX,mEyeY,mEyeZ, mLookAtX,mLookAtY,mLookAtZ, mUpX,mUpY,mUpZ);
	}

	public YangMatrix getMatrix() {
		return mViewMatrix;
	}

	public Vector3f getRightVector(Vector3f target) {
		float[] mat = mViewMatrix.mValues;
		target.mX = mat[0];
		target.mY = mat[4];
		target.mZ = mat[8];
		return target;
	}

	public Vector3f getUpVector(Vector3f target) {
		float[] mat = mViewMatrix.mValues;
		target.mX = mat[1];
		target.mY = mat[5];
		target.mZ = mat[9];
		return target;
	}

	public Vector3f getForwardVector(Vector3f target) {
		float[] mat = mViewMatrix.mValues;
		target.mX = mat[2];
		target.mY = mat[6];
		target.mZ = mat[10];
		return target;
	}

	public float[] getLookVectorSwallow() {
		mLookVector[0] = mLookAtX-mEyeX;
		mLookVector[1] = mLookAtY-mEyeY;
		mLookVector[2] = mLookAtZ-mEyeZ;
		return mLookVector;
	}

	public float[] getLookDirectionSwallow() {
		float dDist = 1f/Geometry.getDistance(getLookVectorSwallow());
		mLookDirection[0] = mLookVector[0]*dDist;
		mLookDirection[1] = mLookVector[1]*dDist;
		mLookDirection[2] = mLookVector[2]*dDist;
		return mLookDirection;
	}

	public LegacyCamera3D setAlphaBeta(float alpha, float beta, float distance, float focusX,float focusY,float focusZ) {
		return set(focusX+(float)(Math.sin(alpha)*Math.cos(beta))*distance,
				focusY+(float)Math.sin(beta)*distance,
				focusZ+(float)(Math.cos(alpha)*Math.cos(beta))*distance,
				focusX,focusY,focusZ, 0,1,0);
	}

	public LegacyCamera3D setOutwardsAlphaBeta(float alpha, float beta, float distance, float focusX,float focusY,float focusZ) {
		return set(focusX,focusY,focusZ,
				focusX+(float)(Math.sin(alpha)*Math.cos(beta))*distance,
				focusY+(float)Math.sin(beta)*distance,
				focusZ+(float)(Math.cos(alpha)*Math.cos(beta))*distance,
				0,1,0);
	}

	public LegacyCamera3D setAlphaBeta(float alpha, float beta, float distance) {
		return setAlphaBeta(alpha,beta,distance,0,0,0);
	}

	public LegacyCamera3D setAlphaBeta(float alpha, float beta) {
		return setAlphaBeta(alpha,beta,1,0,0,0);
	}

	public void getEyeToPointVector(float x, float y, float z, Vector3f target) {
		target.mX = x-mEyeX;
		target.mY = y-mEyeY;
		target.mZ = z-mEyeZ;
	}

	public void getEyeToPointDirection(float x, float y, float z, Vector3f target) {
		target.mX = x-mEyeX;
		target.mY = y-mEyeY;
		target.mZ = z-mEyeZ;
		target.normalize();
	}

}
