package yang.android.graphics;

import yang.model.TransformationMatrix;
import android.opengl.Matrix;

public class AndroidTransformationMatrix extends TransformationMatrix{

	private float[] mMatrix;
	private float[] mMatrixBack;
	
	public AndroidTransformationMatrix() {
		super();
		mMatrix = new float[16];
		mMatrixBack = new float[16];
	}
	
	@Override
	public void loadIdentity()
	{
		Matrix.setIdentityM(mMatrix, 0);
	}

	@Override
	public void translate(float x, float y, float z) {
		Matrix.translateM(mMatrix, 0, x, y, z);
	}
	
	@Override
	public void scale(float x, float y, float z) {
		Matrix.scaleM(mMatrix, 0, x, y, z);
	}
	
	@Override
	public void scale(float s) {
		Matrix.scaleM(mMatrix, 0, s, s, s);
	}
	
	@Override
	public void rotate(float angle,float weightX,float weightY,float weightZ) {
		Matrix.rotateM(mMatrix, 0, angle*TransformationMatrix.TO_DEG_FACTOR, weightX, weightY, weightZ);
	}

	@Override
	public void setOrthogonalProjection(float left, float right, float top, float bottom, float near, float far) {
		Matrix.orthoM(mMatrix, 0, left, right, bottom, top, near, far);
	}

	@Override
	public float[] asFloatArraySwallow() {
		return mMatrix;
	}

	@Override
	public void multiplyRight(float[] rhsMatrix) {
		Matrix.multiplyMM(mMatrixBack, 0, mMatrix, 0, rhsMatrix, 0);
		float[] swap = mMatrix;
		mMatrix = mMatrixBack;
		mMatrixBack = swap;
	}

	@Override
	public void multiplyLeft(float[] lhsMatrix) {
		Matrix.multiplyMM(mMatrixBack, 0, lhsMatrix, 0, mMatrix, 0);
		float[] swap = mMatrix;
		mMatrix = mMatrixBack;
		mMatrixBack = swap;
	}
	
	@Override
	public void multiply(float[] lhsMatrix,float[] rhsMatrix) {
		Matrix.multiplyMM(mMatrix, 0, lhsMatrix, 0, rhsMatrix, 0);
	}
	
	@Override
	public void multiply(TransformationMatrix lhsMatrix,TransformationMatrix rhsMatrix) {
		Matrix.multiplyMM(mMatrix, 0, ((AndroidTransformationMatrix)lhsMatrix).mMatrix, 0, ((AndroidTransformationMatrix)rhsMatrix).mMatrix, 0);
	}

	@Override
	public void setSwallow(float[] matrix) {
		this.mMatrix = matrix;
	}
	
	@Override
	public void copyFrom(TransformationMatrix src) {
		System.arraycopy( ((AndroidTransformationMatrix)src).mMatrix,0, this.mMatrix,0, 16);
	}

	@Override
	public void asInverted(float[] target) {
		Matrix.invertM(target, 0, mMatrix, 0);
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
	public void setPerspectiveProjection(float right,float top,float near,float far) {
		Matrix.frustumM(mMatrix, 0, -right, right, -top, top, near, far);
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
