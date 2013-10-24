package yang.math.objects.matrix;

import yang.math.objects.Point3f;
import yang.math.objects.Vector3f;


public class YangMatrixCameraOps extends YangMatrix {

	private Vector3f mVec0 = null;
	private final Vector3f mVec1 = new Vector3f();
	private final Vector3f mVec2 = new Vector3f();
	private Vector3f mVec3;
	private Vector3f mVec4;

	public void setLookAt(float eyeX, float eyeY, float eyeZ, float lookAtX, float lookAtY, float lookAtZ, float upX, float upY, float upZ) {
		if(mVec0==null) {
			mVec0 = new Vector3f();
			mVec3 = new Vector3f();
			mVec4 = new Vector3f();
		}
		mVec0.set(eyeX,eyeY,eyeZ);
		mVec3.set(eyeX-lookAtX,eyeY-lookAtY,eyeZ-lookAtZ);
		mVec4.set(upX,upY,upZ);

		float dist = mVec3.magn();
		if(dist==0) {
			mVec3.mZ = 1;
			dist = 1;
		}
		mVec3.scale(1/dist);
		mVec1.cross(mVec3, mVec4);
		float rightDist = mVec1.magn();
		if(rightDist == 0) {
			mVec1.mX = 1;
			rightDist = 1;
		}
		mVec1.scale(1/rightDist);
		mVec2.cross(mVec1,mVec3);
		mVec1.scale(-1);

		setRow(0,mVec1);
		setRow(1,mVec2);
		setRow(2,mVec3);
		setColumn(3,-mVec0.dot(mVec1),-mVec0.dot(mVec2),-mVec0.dot(mVec3));
	}

	public void setOrthogonalProjection(float left, float right, float top, float bottom, float near, float far) {
		final float dx = 1/(right - left);
		final float dy = 1/(top - bottom);
		final float dz = 1/(far - near);

		setRow(0, 2*dx,    0,    0, -(right+left)*dx);
		setRow(1, 0,    2*dy,    0, -(top+bottom)*dy);
		setRow(2, 0,    0, -2*dz, -(far+near)*dz);
		setRow(3, 0,    0,    0,    1);
	}

	public void setOrthogonalProjection(float left, float right, float top, float bottom) {
		setOrthogonalProjection(left,right,top,bottom,DEFAULT_NEAR,DEFAULT_FAR);
	}

	public void setPerspectiveProjection(float right,float top,float near, float far) {
		this.setRow(0, near/right, 0, 0, 0);
		this.setRow(1, 0, near/top, 0, 0);
		this.setRow(2, 0,0,-(far+near)/(far-near),-2*far*near/(far-near));
		this.setRow(3, 0,0,-1,0);
	}

	public void setPerspectiveProjectionFovy(float fovy,float ratioX,float ratioY,float near, float far) {
		final float tan = (float)Math.tan(fovy);
		setPerspectiveProjection(tan*near*ratioX,tan*near*ratioY,near,far);
	}

	public void setPerspectiveProjectionFovy(float fovy,float ratioX,float near, float far) {
		setPerspectiveProjectionFovy(fovy,ratioX,1,near,far);
	}

	public void setLookAtAlphaBeta(float focusX, float focusY, float focusZ, float alpha, float beta,float distance,Point3f outPosition) {
		final float eyeX = focusX+(float)(Math.sin(alpha)*Math.cos(beta))*distance;
		final float eyeY = focusY+(float)Math.sin(beta)*distance;
		final float eyeZ = focusZ+(float)(Math.cos(alpha)*Math.cos(beta))*distance;
		setLookAt(eyeX,eyeY,eyeZ, focusX,focusY,focusZ, 0,1,0);
		if(outPosition!=null)
			outPosition.set(eyeX,eyeY,eyeZ);
	}
//
//	public float getProjectedPositionAndRadius(Camera3D camera, float x, float y, float z, float radius,Vector3f target) {
//		MatrixOps.applyFloatMatrix3D(mMatrix,x,y,z,target);
//		camera.getEyeToPointVector(x,y,z,mVec1);
//		mVec2.createOrthoVec(mVec1);
//		MatrixOps.applyFloatMatrix3DNormalized(mMatrix,x+mVec2.mX*radius,y+mVec2.mY*radius,z+mVec2.mZ*radius,mTempMat1,0);
//		return target.getDistance(mTempMat1);
//	}

}
