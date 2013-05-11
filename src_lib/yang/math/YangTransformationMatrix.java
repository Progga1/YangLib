package yang.math;

import java.util.Arrays;


public class YangTransformationMatrix extends TransformationMatrix{

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
	protected float[] mMatrixBack;
	protected float[] mTempMat;
	
	//----INSTANCE-METHODS----
	
	public YangTransformationMatrix() {
		super();
		mMatrix = new float[16];
		mMatrixBack = new float[16];
		mTempMat = new float[16];
		loadIdentity();
	}
	
	@Override
	public void loadIdentity() {
		MatrixOps.setIdentity(mMatrix);
	}

	@Override
	public void translate(float x, float y, float z) {
		mMatrix[12] += mMatrix[0]*x + mMatrix[4]*y + mMatrix[8]*z;
		mMatrix[13] += mMatrix[1]*x + mMatrix[5]*y + mMatrix[9]*z;
		mMatrix[14] += mMatrix[2]*x + mMatrix[6]*y + mMatrix[10]*z;
		mMatrix[15] += mMatrix[3]*x + mMatrix[7]*y + mMatrix[11]*z;
	}
	
	@Override
	public void scale(float x, float y, float z) {
		for(int i=0;i<4;i++) {
			mMatrix[i] *= x;
			mMatrix[i+4] *= y;
			mMatrix[i+8] *= z;
		}
	}
	
	@Override
	public void rotate(float angle,float weightX,float weightY,float weightZ) {
		throw new RuntimeException("unsupported");
	}
	
	@Override
	public void rotateX(float angle) {
		MatrixOps.setRotationX(mTempMat,angle);
		MatrixOps.multiply(mMatrixBack,mMatrix,mTempMat);
		float[] swap = mMatrix;
		mMatrix = mMatrixBack;
		mMatrixBack = swap;
	}

	@Override
	public void rotateY(float angle) {
		MatrixOps.setRotationY(mTempMat,angle);
		MatrixOps.multiply(mMatrixBack,mMatrix,mTempMat);
		float[] swap = mMatrix;
		mMatrix = mMatrixBack;
		mMatrixBack = swap;
	}
	
	@Override
	public void rotateZ(float angle) {
		MatrixOps.setRotationZ(mTempMat,angle);
		MatrixOps.multiply(mMatrixBack,mMatrix,mTempMat);
		float[] swap = mMatrix;
		mMatrix = mMatrixBack;
		mMatrixBack = swap;
	}
	
	@Override
	public float[] asFloatArraySwallow() {
		return mMatrix;
	}

	@Override
	public void multiplyRight(float[] rhsMatrix) {
		
		MatrixOps.multiply(mMatrixBack,mMatrix,rhsMatrix);
		
		float[] swap = mMatrix;
		mMatrix = mMatrixBack;
		mMatrixBack = swap;
	}

	@Override
	public void multiplyLeft(float[] lhsMatrix) {

		MatrixOps.multiply(mMatrixBack,lhsMatrix,mMatrix);
		
		float[] swap = mMatrix;
		mMatrix = mMatrixBack;
		mMatrixBack = swap;
	}
	
	@Override
	public void multiply(float[] lhsMatrix,float[] rhsMatrix) {
		MatrixOps.multiply(mMatrix,lhsMatrix,rhsMatrix);
	}
	
	@Override
	public void multiply(TransformationMatrix lhsMatrix,TransformationMatrix rhsMatrix) {
		MatrixOps.multiply(mMatrix,lhsMatrix.asFloatArraySwallow(),rhsMatrix.asFloatArraySwallow());
	}
	
	@Override
	public void set(TransformationMatrix src) {
		System.arraycopy( ((YangTransformationMatrix)src).mMatrix,0, this.mMatrix,0, 16);
	}
	
	public static void transpose(float[] target,float[] matrix) {
		target[M00] = matrix[M00];
		target[M01] = matrix[M10];
		target[M02] = matrix[M20];
		target[M03] = matrix[M30];
		target[M10] = matrix[M01];
		target[M11] = matrix[M11];
		target[M12] = matrix[M21];
		target[M13] = matrix[M31];
		target[M20] = matrix[M02];
		target[M21] = matrix[M12];
		target[M22] = matrix[M22];
		target[M23] = matrix[M32];
		target[M30] = matrix[M03];
		target[M31] = matrix[M13];
		target[M32] = matrix[M23];
		target[M33] = matrix[M33];
	}
	
	//From android.opengl.matrix
	public static boolean invert(float[] target,float[] matrix, float[] tempMat) {
        // Invert a 4 x 4 matrix using Cramer's Rule

        // transpose matrix
		float[] transposed = target;
		transpose(transposed, matrix);
		
        // temp array for pairs
        

        // calculate pairs for first 8 elements (cofactors)
        tempMat[0] = transposed[10] * transposed[15];
        tempMat[1] = transposed[11] * transposed[14];
        tempMat[2] = transposed[9] * transposed[15];
        tempMat[3] = transposed[11] * transposed[13];
        tempMat[4] = transposed[9] * transposed[14];
        tempMat[5] = transposed[10] * transposed[13];
        tempMat[6] = transposed[8] * transposed[15];
        tempMat[7] = transposed[11] * transposed[12];
        tempMat[8] = transposed[8] * transposed[14];
        tempMat[9] = transposed[10] * transposed[12];
        tempMat[10] = transposed[8] * transposed[13];
        tempMat[11] = transposed[9] * transposed[12];

        // Holds the destination matrix while we're building it up.
        float[] dst = new float[16];

        // calculate first 8 elements (cofactors)
        dst[0] = tempMat[0] * transposed[5] + tempMat[3] * transposed[6] + tempMat[4] * transposed[7];
        dst[0] -= tempMat[1] * transposed[5] + tempMat[2] * transposed[6] + tempMat[5] * transposed[7];
        dst[1] = tempMat[1] * transposed[4] + tempMat[6] * transposed[6] + tempMat[9] * transposed[7];
        dst[1] -= tempMat[0] * transposed[4] + tempMat[7] * transposed[6] + tempMat[8] * transposed[7];
        dst[2] = tempMat[2] * transposed[4] + tempMat[7] * transposed[5] + tempMat[10] * transposed[7];
        dst[2] -= tempMat[3] * transposed[4] + tempMat[6] * transposed[5] + tempMat[11] * transposed[7];
        dst[3] = tempMat[5] * transposed[4] + tempMat[8] * transposed[5] + tempMat[11] * transposed[6];
        dst[3] -= tempMat[4] * transposed[4] + tempMat[9] * transposed[5] + tempMat[10] * transposed[6];
        dst[4] = tempMat[1] * transposed[1] + tempMat[2] * transposed[2] + tempMat[5] * transposed[3];
        dst[4] -= tempMat[0] * transposed[1] + tempMat[3] * transposed[2] + tempMat[4] * transposed[3];
        dst[5] = tempMat[0] * transposed[0] + tempMat[7] * transposed[2] + tempMat[8] * transposed[3];
        dst[5] -= tempMat[1] * transposed[0] + tempMat[6] * transposed[2] + tempMat[9] * transposed[3];
        dst[6] = tempMat[3] * transposed[0] + tempMat[6] * transposed[1] + tempMat[11] * transposed[3];
        dst[6] -= tempMat[2] * transposed[0] + tempMat[7] * transposed[1] + tempMat[10] * transposed[3];
        dst[7] = tempMat[4] * transposed[0] + tempMat[9] * transposed[1] + tempMat[10] * transposed[2];
        dst[7] -= tempMat[5] * transposed[0] + tempMat[8] * transposed[1] + tempMat[11] * transposed[2];

        // calculate pairs for second 8 elements (cofactors)
        tempMat[0] = transposed[2] * transposed[7];
        tempMat[1] = transposed[3] * transposed[6];
        tempMat[2] = transposed[1] * transposed[7];
        tempMat[3] = transposed[3] * transposed[5];
        tempMat[4] = transposed[1] * transposed[6];
        tempMat[5] = transposed[2] * transposed[5];
        tempMat[6] = transposed[0] * transposed[7];
        tempMat[7] = transposed[3] * transposed[4];
        tempMat[8] = transposed[0] * transposed[6];
        tempMat[9] = transposed[2] * transposed[4];
        tempMat[10] = transposed[0] * transposed[5];
        tempMat[11] = transposed[1] * transposed[4];

        // calculate second 8 elements (cofactors)
        dst[8] = tempMat[0] * transposed[13] + tempMat[3] * transposed[14] + tempMat[4] * transposed[15];
        dst[8] -= tempMat[1] * transposed[13] + tempMat[2] * transposed[14] + tempMat[5] * transposed[15];
        dst[9] = tempMat[1] * transposed[12] + tempMat[6] * transposed[14] + tempMat[9] * transposed[15];
        dst[9] -= tempMat[0] * transposed[12] + tempMat[7] * transposed[14] + tempMat[8] * transposed[15];
        dst[10] = tempMat[2] * transposed[12] + tempMat[7] * transposed[13] + tempMat[10] * transposed[15];
        dst[10] -= tempMat[3] * transposed[12] + tempMat[6] * transposed[13] + tempMat[11] * transposed[15];
        dst[11] = tempMat[5] * transposed[12] + tempMat[8] * transposed[13] + tempMat[11] * transposed[14];
        dst[11] -= tempMat[4] * transposed[12] + tempMat[9] * transposed[13] + tempMat[10] * transposed[14];
        dst[12] = tempMat[2] * transposed[10] + tempMat[5] * transposed[11] + tempMat[1] * transposed[9];
        dst[12] -= tempMat[4] * transposed[11] + tempMat[0] * transposed[9] + tempMat[3] * transposed[10];
        dst[13] = tempMat[8] * transposed[11] + tempMat[0] * transposed[8] + tempMat[7] * transposed[10];
        dst[13] -= tempMat[6] * transposed[10] + tempMat[9] * transposed[11] + tempMat[1] * transposed[8];
        dst[14] = tempMat[6] * transposed[9] + tempMat[11] * transposed[11] + tempMat[3] * transposed[8];
        dst[14] -= tempMat[10] * transposed[11] + tempMat[2] * transposed[8] + tempMat[7] * transposed[9];
        dst[15] = tempMat[10] * transposed[10] + tempMat[4] * transposed[8] + tempMat[9] * transposed[9];
        dst[15] -= tempMat[8] * transposed[9] + tempMat[11] * transposed[10] + tempMat[5] * transposed[8];

        // calculate determinant
        float det = transposed[0] * dst[0] + transposed[1] * dst[1] + transposed[2] * dst[2] + transposed[3] * dst[3];

        if (det == 0.0f) {
        	return false;
        }

        // calculate matrix inverse
        det = 1 / det;
        for (int j = 0; j < 16; j++)
            target[j] = dst[j] * det;

        return true;
	}
	
	@Override
	public boolean asInverted(float[] target) {
		return invert(target,mMatrix,mTempMat);
	}
	
	public void invert() {
		invert(mMatrixBack,mMatrix,mTempMat);
		
		float[] swap = mMatrix;
		mMatrix = mMatrixBack;
		mMatrixBack = swap;
	}

	@Override
	public float get(int index) {
		return mMatrix[index];
	}
	
	@Override
	public float get(int row, int column) {
		return mMatrix[column*4+row];
	}

	@Override
	public void set(int row, int column, float value) {
		mMatrix[column*4+row] = value;
	}

	@Override
	public void setColumn(int i, float x, float y, float z) {
		mMatrix[i*4] = x;
		mMatrix[i*4+1] = y;
		mMatrix[i*4+2] = z;
	}
	
	@Override
	public void setColumn(int i, float x, float y, float z, float w) {
		mMatrix[i*4] = x;
		mMatrix[i*4+1] = y;
		mMatrix[i*4+2] = z;
		mMatrix[i*4+3] = w;
	}
	
	@Override
	public void setRow(int i, float x, float y, float z) {
		mMatrix[i] = x;
		mMatrix[4+i] = y;
		mMatrix[8+i] = z;
	}
	
	@Override
	public void setRow(int i, float x, float y, float z, float w) {
		mMatrix[i] = x;
		mMatrix[4+i] = y;
		mMatrix[8+i] = z;
		mMatrix[12+i] = w;
	}

	@Override
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

	@Override
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
	
}
