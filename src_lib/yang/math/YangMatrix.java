package yang.math;

import javax.vecmath.Vector3f;


public class YangMatrix {

	public static float DEFAULT_NEAR = 1;
	public static float DEFAULT_FAR = -1;
	
	//Indices: Row-Column
	public static final int M00 = 0;
	public static final int M01 = 4;
	public static final int M02 = 8;
	public static final int M03 = 12;
	public static final int M10 = 1;
	public static final int M11 = 5;
	public static final int M12 = 9;
	public static final int M13 = 13;
	public static final int M20 = 2;
	public static final int M21 = 6;
	public static final int M22 = 10;
	public static final int M23 = 14;
	public static final int M30 = 3;
	public static final int M31 = 7;
	public static final int M32 = 11;
	public static final int M33 = 15;
	
	public float[] mMatrix;
	public float[] mInverted;
	protected float[] mMatrixBack;
	protected float[] mTempMat1,mTempMat2;

	public static float TO_RAD_FACTOR = (float) Math.PI / 180;
	public static float TO_DEG_FACTOR = 180 / (float) Math.PI;
	
	public YangMatrix() {
		mMatrix = new float[16];
		mMatrixBack = new float[16];
		mTempMat1 = new float[16];
		mTempMat2 = new float[16];
		mInverted = null;
	}

	public void loadIdentity() {
		MatrixOps.setIdentity(mMatrix);
	}

	public void translate(float x, float y, float z) {
		mMatrix[12] += mMatrix[0]*x + mMatrix[4]*y + mMatrix[8]*z;
		mMatrix[13] += mMatrix[1]*x + mMatrix[5]*y + mMatrix[9]*z;
		mMatrix[14] += mMatrix[2]*x + mMatrix[6]*y + mMatrix[10]*z;
		mMatrix[15] += mMatrix[3]*x + mMatrix[7]*y + mMatrix[11]*z;
	}

	public void scale(float x, float y, float z) {
		for(int i=0;i<4;i++) {
			mMatrix[i] *= x;
			mMatrix[i+4] *= y;
			mMatrix[i+8] *= z;
		}
	}
	
	public void scale(float x, float y) {
		scale(x, y, 1);
	}

	public void scale(float s) {
		scale(s, s, s);
	}

	public void rotateX(float angle) {
		MatrixOps.setRotationX(mTempMat1,angle);
		MatrixOps.multiply(mMatrixBack,mMatrix,mTempMat1);
		float[] swap = mMatrix;
		mMatrix = mMatrixBack;
		mMatrixBack = swap;
	}

	public void rotateY(float angle) {
		MatrixOps.setRotationY(mTempMat1,angle);
		MatrixOps.multiply(mMatrixBack,mMatrix,mTempMat1);
		float[] swap = mMatrix;
		mMatrix = mMatrixBack;
		mMatrixBack = swap;
	}

	public void rotateZ(float angle) {
		MatrixOps.setRotationZ(mTempMat1,angle);
		MatrixOps.multiply(mMatrixBack,mMatrix,mTempMat1);
		float[] swap = mMatrix;
		mMatrix = mMatrixBack;
		mMatrixBack = swap;
	}

	public void multiplyRight(float[] rhsMatrix) {
		
		MatrixOps.multiply(mMatrixBack,mMatrix,rhsMatrix);
		
		float[] swap = mMatrix;
		mMatrix = mMatrixBack;
		mMatrixBack = swap;
	}

	public void multiplyLeft(float[] lhsMatrix) {

		MatrixOps.multiply(mMatrixBack,lhsMatrix,mMatrix);
		
		float[] swap = mMatrix;
		mMatrix = mMatrixBack;
		mMatrixBack = swap;
	}

	public void multiply(float[] lhsMatrix,float[] rhsMatrix) {
		MatrixOps.multiply(mMatrix,lhsMatrix,rhsMatrix);
	}
	
	public void multiply(YangMatrix lhsMatrix,YangMatrix rhsMatrix) {
		MatrixOps.multiply(mMatrix,lhsMatrix.mMatrix,rhsMatrix.mMatrix);
	}
	
	public void set(YangMatrix src) {
		System.arraycopy(src.mMatrix,0, this.mMatrix,0, 16);
	}
	
	public boolean asInverted(float[] target) {
		return MatrixOps.invert(target,mMatrix,mTempMat1,mTempMat2);
	}
	
	public boolean refreshInverted() {
		if(mInverted==null)
			mInverted = new float[16];
		return MatrixOps.invert(mInverted,mMatrix,mTempMat1,mTempMat2);
	}
	
	public void invert() {
		MatrixOps.invert(mMatrixBack,mMatrix,mTempMat1,mTempMat2);
		
		float[] swap = mMatrix;
		mMatrix = mMatrixBack;
		mMatrixBack = swap;
	}

	public float get(int index) {
		return mMatrix[index];
	}
	
	public float get(int row, int column) {
		return mMatrix[column*4+row];
	}

	public void set(int row, int column, float value) {
		mMatrix[column*4+row] = value;
	}

	public void setColumn(int i, float x, float y, float z) {
		mMatrix[i*4] = x;
		mMatrix[i*4+1] = y;
		mMatrix[i*4+2] = z;
	}
	
	public void setColumn(int i, float x, float y, float z, float w) {
		mMatrix[i*4] = x;
		mMatrix[i*4+1] = y;
		mMatrix[i*4+2] = z;
		mMatrix[i*4+3] = w;
	}
	
	public void setRow(int i, float x, float y, float z) {
		mMatrix[i] = x;
		mMatrix[4+i] = y;
		mMatrix[8+i] = z;
	}
	
	public void setRow(int i, float x, float y, float z, float w) {
		mMatrix[i] = x;
		mMatrix[4+i] = y;
		mMatrix[8+i] = z;
		mMatrix[12+i] = w;
	}

	public void setRowMajor(double[][] matrix) {
		mMatrix[0] = (float)matrix[0][0];
		mMatrix[1] = (float)matrix[1][0];
		mMatrix[2] = (float)matrix[2][0];
		mMatrix[3] = (float)matrix[3][0];
		mMatrix[4] = (float)matrix[0][1];
		mMatrix[5] = (float)matrix[1][1];
		mMatrix[6] = (float)matrix[2][1];
		mMatrix[7] = (float)matrix[3][1];
		mMatrix[8] = (float)matrix[0][2];
		mMatrix[9] = (float)matrix[1][2];
		mMatrix[10] = (float)matrix[2][2];
		mMatrix[11] = (float)matrix[3][2];
		mMatrix[12] = (float)matrix[0][3];
		mMatrix[13] = (float)matrix[1][3];
		mMatrix[14] = (float)matrix[2][3];
		mMatrix[15] = (float)matrix[3][3];
	}

	public void setColumnMajor(double[][] matrix) {
		mMatrix[0] = (float)matrix[0][0];
		mMatrix[1] = (float)matrix[0][1];
		mMatrix[2] = (float)matrix[0][2];
		mMatrix[3] = (float)matrix[0][3];
		mMatrix[4] = (float)matrix[1][0];
		mMatrix[5] = (float)matrix[1][1];
		mMatrix[6] = (float)matrix[1][2];
		mMatrix[7] = (float)matrix[1][3];
		mMatrix[8] = (float)matrix[2][0];
		mMatrix[9] = (float)matrix[2][1];
		mMatrix[10] = (float)matrix[2][2];
		mMatrix[11] = (float)matrix[2][3];
		mMatrix[12] = (float)matrix[3][0];
		mMatrix[13] = (float)matrix[3][1];
		mMatrix[14] = (float)matrix[3][2];
		mMatrix[15] = (float)matrix[3][3];
	}
	
	public void setOrthogonalProjection(float left, float right, float top, float bottom, float near, float far) {
		float dx = 1/(right - left);
		float dy = 1/(top - bottom);
		float dz = 1/(far - near);

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
	
	public void setPerspectiveProjectionFovy(float fovy,float ratio,float near, float far) {
		float tan = (float)Math.tan(fovy);
		setPerspectiveProjection(tan*near*ratio,tan*near,near,far);
	}

	public void multiplyRight(YangMatrix rhsMatrix) {
		multiplyRight(rhsMatrix.mMatrix);
	}

	public void multiplyLeft(YangMatrix lhsMatrix) {
		multiplyLeft(lhsMatrix.mMatrix);
	}

	public void translate(float x, float y) {
		translate(x, y, 0);
	}

	public float[] asFloatArrayDeep() {
		return MatrixOps.createMatrixCopy(mMatrix);
	}
	
	public float apply2DX(float x, float y) {
		return MatrixOps.applyFloatMatrixX2D(mMatrix,x,y);
	}

	public float apply2DY(float x, float y) {
		return MatrixOps.applyFloatMatrixY2D(mMatrix,x,y);
	}

	public void apply2D(float x, float y, float[] target, int targetOffset) {
		MatrixOps.applyFloatMatrix2D(mMatrix,x,y,target,targetOffset);
	}
	
	public void apply3D(float x, float y, float z, float[] target, int targetOffset) {
		MatrixOps.applyFloatMatrix3D(mMatrix,x,y,z,target,targetOffset);
	}
	
	public void apply3DNormalized(float x, float y, float z, float[] target, int targetOffset) {
		MatrixOps.applyFloatMatrix3DNormalized(mMatrix,x,y,z,target,targetOffset);
	}
	
	private Vector3f mVec0 = null;
	private Vector3f mVec1;
	private Vector3f mVec2;
	private Vector3f mVec3;
	private Vector3f mVec4;
	
	protected void setColumn(int col, Vector3f values) {
		setColumn(col,values.x,values.y,values.z);
	}
	
	protected void setRow(int row, Vector3f values) {
		setRow(row,values.x,values.y,values.z);
	}

	public void setLookAt(float eyeX, float eyeY, float eyeZ, float lookAtX, float lookAtY, float lookAtZ, float upX, float upY, float upZ) {
		if(mVec0==null) {
			mVec0 = new Vector3f();
			mVec1 = new Vector3f();
			mVec2 = new Vector3f();
			mVec3 = new Vector3f();
			mVec4 = new Vector3f();
		}
		mVec0.set(eyeX,eyeY,eyeZ);
		mVec3.set(eyeX-lookAtX,eyeY-lookAtY,eyeZ-lookAtZ);
		mVec4.set(upX,upY,upZ);

		float dist = mVec3.length();
		if(dist==0) {
			mVec3.z = 1;
			dist = 1;
		}
		mVec3.scale(1/dist);
		mVec1.cross(mVec3, mVec4);
		float rightDist = mVec1.length();
		if(rightDist == 0) {
			mVec1.x = 1;
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

	public void scaleX(float value) {
		scale(value,1,1);
	}
	
	public void scaleY(float value) {
		scale(1,value,1);
	}
	
	public void scaleZ(float value) {
		scale(1,1,value);
	}

	public void setTranslationOnly() {
		setRow(0,1,0,0);
		setRow(1,0,1,0);
		setRow(2,0,0,1);
	}

	public void applyToArray(float[] source, int vertexCount, boolean zComponent, float preShiftX, float preShiftY, float postShiftX, float postShiftY,float[] target, int targetOffset) {
		int sourceOffset = 0;
		float[] matrix = this.mMatrix;
		if(!zComponent) {
			for(int i=0;i<vertexCount;i++) {
				float x = source[sourceOffset++]+preShiftX;
				float y = source[sourceOffset++]+preShiftY;
				target[targetOffset++] = matrix[0]*x+matrix[4]*y+matrix[12]+postShiftX;
				target[targetOffset++] = matrix[1]*x+matrix[5]*y+matrix[13]+postShiftY;
			}
		}else{
			for(int i=0;i<vertexCount;i++) {
				float x = source[sourceOffset++]+preShiftX;
				float y = source[sourceOffset++]+preShiftY;
				float z = source[sourceOffset++];
				target[targetOffset++] = matrix[0]*x+matrix[4]*y+matrix[8]*z+matrix[12]+postShiftX;
				target[targetOffset++] = matrix[1]*x+matrix[5]*y+matrix[9]*z+matrix[13]+postShiftY;
				target[targetOffset++] = matrix[2]*x+matrix[6]*y+matrix[10]*z+matrix[14];
			}
		}
	}
	
	
	@Override
	public String toString() {
		return MatrixOps.matToString(mMatrix);
	}
	
}
