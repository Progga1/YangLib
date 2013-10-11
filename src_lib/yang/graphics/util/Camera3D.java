package yang.graphics.util;

import yang.math.Geometry;
import yang.math.objects.Quaternion;
import yang.math.objects.Vector3f;
import yang.math.objects.matrix.YangMatrix;
import yang.math.objects.matrix.YangMatrixCameraOps;

public class Camera3D {

	private float[] mLookVector = new float[4];
	private float[] mLookDirection = new float[4];
	public float mEyeX,mEyeY,mEyeZ;
	public float mLookAtX,mLookAtY,mLookAtZ;
	public float mUpX,mUpY,mUpZ;
	public YangMatrixCameraOps mViewMatrix;
	
	public Camera3D() {
		mLookVector[3] = 1;
		mLookDirection[3] = 0;
		mViewMatrix = new YangMatrixCameraOps();
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
		refreshMatrix();
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
	
	public void refreshMatrix() {
		mViewMatrix.setLookAt(mEyeX,mEyeY,mEyeZ, mLookAtX,mLookAtY,mLookAtZ, mUpX,mUpY,mUpZ);
	}
	
	public YangMatrix getMatrix() {
		return mViewMatrix;
	}
	
	public Vector3f getRightVector(Vector3f target) {
		float[] mat = mViewMatrix.mMatrix;
		target.mX = mat[0];
		target.mY = mat[4];
		target.mZ = mat[8];
		return target;
	}
	
	public Vector3f getUpVector(Vector3f target) {
		float[] mat = mViewMatrix.mMatrix;
		target.mX = mat[1];
		target.mY = mat[5];
		target.mZ = mat[9];
		return target;
	}
	
	public Vector3f getForwardVector(Vector3f target) {
		float[] mat = mViewMatrix.mMatrix;
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

	public Camera3D setAlphaBeta(float alpha, float beta, float distance, float focusX,float focusY,float focusZ) {
		return set(focusX+(float)(Math.sin(alpha)*Math.cos(beta))*distance,
				focusY+(float)Math.sin(beta)*distance,
				focusZ+(float)(Math.cos(alpha)*Math.cos(beta))*distance,
				focusX,focusY,focusZ, 0,1,0);
	}
	
	public Camera3D setOutwardsAlphaBeta(float alpha, float beta, float distance, float focusX,float focusY,float focusZ) {
		return set(focusX,focusY,focusZ,
				focusX+(float)(Math.sin(alpha)*Math.cos(beta))*distance,
				focusY+(float)Math.sin(beta)*distance,
				focusZ+(float)(Math.cos(alpha)*Math.cos(beta))*distance,
				0,1,0);
	}
	
	public Camera3D setAlphaBeta(float alpha, float beta, float distance) {
		return setAlphaBeta(alpha,beta,distance,0,0,0);
	}
	
	public Camera3D setAlphaBeta(float alpha, float beta) {
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
	
	private Vector3f tempVec1,tempVec2,tempVec3;

	public void mirrorAtPlane(YangMatrix target, float nx,float ny,float nz, float tx,float ty,float tz) {
		if(tempVec1 == null) {
			tempVec1 = new Vector3f();
			tempVec2 = new Vector3f();
			tempVec3 = new Vector3f();
		}
		tempVec1.set(nx,ny,nz);
		tempVec2.set(-mLookDirection[0],-mLookDirection[1],-mLookDirection[2]);
		//tempVec3.cross(-mLookDirection[0],-mLookDirection[1],-mLookDirection[2], nx,ny,nz);
		//target.rotateAround(crossX, crossY, crossZ, 0);
		Quaternion tempQuat1 = new Quaternion();
		Quaternion tempQuat2 = new Quaternion();
		tempQuat1.setFromToRotation(tempVec2.mX,tempVec2.mY,tempVec2.mZ, -tempVec2.mX,-tempVec2.mY,-tempVec2.mZ);
		tempQuat2.setFromToRotation(tempVec1,tempVec2);
		tempQuat1.multRight(tempQuat2);
		tempQuat1.toRotationMatrix(target.mMatrix);
	}
	
}
