package yang.gdx.graphics;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import yang.model.TransformationMatrix;


public class PCTransformationMatrix extends TransformationMatrix{

	private Matrix4f mMatrix;
	private Matrix4f mInvertedMatrix;
	private Matrix4f mInterMatrix;
	private Matrix4f mInterMatrix2;
	private Vector3f mInterVector;
	private AxisAngle4f mAxisAngle;
	
	public PCTransformationMatrix() {
		mMatrix = new Matrix4f();
		mInvertedMatrix = new Matrix4f();
		mInterMatrix = new Matrix4f();
		mInterMatrix2 = new Matrix4f();
		mInterVector = new Vector3f();
		mAxisAngle = new AxisAngle4f();
		loadIdentity();
	}
	
	private void refreshTransform() {
		mMatrix.mul(mMatrix, mInterMatrix);
	}
	
	@Override
	public float[] asFloatArraySwallow() {
		for(int i=0;i<16;i++) {
			mIntermediate[i] = mMatrix.getElement(i%4, i/4);
		}
		return mIntermediate;
	}

	@Override
	public void loadIdentity() {
		mMatrix.setIdentity();
	}
	
	private final Vector3f getVector(float x, float y, float z) {
		mInterVector.set(x, y, z);
		return mInterVector;
	}
	
	@Override
	public void translate(float x, float y, float z) {
		mInterMatrix.setIdentity();
		mInterMatrix.setTranslation(getVector(x,y,z));
		
		refreshTransform();
	}

	@Override
	public void scale(float x, float y, float z) {
		mInterMatrix.setIdentity();
		mInterMatrix.setElement(0, 0, x);
		mInterMatrix.setElement(1, 1, y);
		mInterMatrix.setElement(2, 2, z);
		refreshTransform();
	}

	@Override
	public void rotate(float angle, float weightX, float weightY, float weightZ) {
		mAxisAngle.set(weightX, weightY, weightZ, angle);
		mInterMatrix.setIdentity();
		mInterMatrix.setRotation(mAxisAngle);
		mMatrix.mul(mMatrix, mInterMatrix);
	}

	@Override
	public void multiplyRight(float[] rhsMatrix) {
		floatToMatrix(mInterMatrix,rhsMatrix);
		mMatrix.mul(mMatrix, mInterMatrix);
	}
	
	@Override
	public void multiplyRight(TransformationMatrix rhsMatrix) {
		mMatrix.mul(mMatrix, ((PCTransformationMatrix)rhsMatrix).mMatrix);
	}
	
	@Override
	public void multiplyLeft(float[] lhsMatrix) {
		floatToMatrix(mInterMatrix,lhsMatrix);
		mMatrix.mul(mInterMatrix, mMatrix);
	}
	
	@Override
	public void multiply(float[] lhsMatrix, float[] rhsMatrix) {
		floatToMatrix(mInterMatrix,lhsMatrix);
		floatToMatrix(mInterMatrix2,rhsMatrix);
		mMatrix.mul(mInterMatrix, mInterMatrix2);
	}

	@Override
	public void multiply(TransformationMatrix lhsMatrix, TransformationMatrix rhsMatrix) {
		multiply(lhsMatrix.asFloatArraySwallow(), rhsMatrix.asFloatArraySwallow());
	}
	
	private void floatToMatrix(Matrix4f dest, float[] matrix) {
		for(int i=0;i<16;i++) {
			dest.setElement(i%4, i/4, matrix[i]);
		}
	}

	@Override
	public void setSwallow(float[] matrix) {
		floatToMatrix(mMatrix,matrix);
	}
	
	@Override
	public void copyFrom(TransformationMatrix src) {
		mMatrix.set(((PCTransformationMatrix)src).mMatrix);
	}

	@Override
	public void setOrthogonalProjection(float left, float right, float top, float bottom, float near, float far) {
		float dx = 1/(right - left);
		float dy = 1/(top - bottom);
		float dz = 1/(far - near);

		mMatrix.setRow(0, 2*dx,    0,    0, -(right+left)*dx);
		mMatrix.setRow(1, 0,    2*dy,    0, -(top+bottom)*dy);
		mMatrix.setRow(2, 0,    0, -2*dz, -(far+near)*dz);
		mMatrix.setRow(3, 0,    0,    0,    1);
	}

	@Override
	public void asInverted(float[] target) {
		for(int x=0;x<4;x++) 
			for(int y=0;y<4;y++)
				mInvertedMatrix.setElement(x,y, mMatrix.getElement(x,y));
		
		mInvertedMatrix.invert();
		for(int i=0;i<16;i++) {
			target[i] = mInvertedMatrix.getElement(i%4, i/4);
		}
	}
	
	@Override
	public float get(int row, int column) {
		return mMatrix.getElement(row, column);
	}

	@Override
	public float get(int index) {
		return mMatrix.getElement(index%4,index/4);
	}
	
	@Override
	public void set(int row, int column, float value) {
		mMatrix.setElement(row, column, value);
	}
	
	@Override
	public void setColumn(int col, float x,float y,float z,float w) {
		mMatrix.setColumn(col, x,y,z,w);
	}

	@Override
	public void setRow(int row, float x,float y,float z,float w) {
		mMatrix.setRow(row, x,y,z,w);
	}

	@Override
	public void setRowMajor(double[][] matrix) {
		mMatrix.m00 = (float)matrix[0][0];
		mMatrix.m01 = (float)matrix[0][1];
		mMatrix.m02 = (float)matrix[0][2];
		mMatrix.m03 = (float)matrix[0][3];
		mMatrix.m10 = (float)matrix[1][0];
		mMatrix.m11 = (float)matrix[1][1];
		mMatrix.m12 = (float)matrix[1][2];
		mMatrix.m13 = (float)matrix[1][3];
		mMatrix.m20 = (float)matrix[2][0];
		mMatrix.m21 = (float)matrix[2][1];
		mMatrix.m22 = (float)matrix[2][2];
		mMatrix.m23 = (float)matrix[2][3];
		mMatrix.m30 = (float)matrix[3][0];
		mMatrix.m31 = (float)matrix[3][1];
		mMatrix.m32 = (float)matrix[3][2];
		mMatrix.m33 = (float)matrix[3][3];
	}

	@Override
	public void setColumnMajor(double[][] matrix) {
		mMatrix.m00 = (float)matrix[0][0];
		mMatrix.m01 = (float)matrix[1][0];
		mMatrix.m02 = (float)matrix[2][0];
		mMatrix.m03 = (float)matrix[3][0];
		mMatrix.m10 = (float)matrix[0][1];
		mMatrix.m11 = (float)matrix[1][1];
		mMatrix.m12 = (float)matrix[2][1];
		mMatrix.m13 = (float)matrix[3][1];
		mMatrix.m20 = (float)matrix[0][2];
		mMatrix.m21 = (float)matrix[1][2];
		mMatrix.m22 = (float)matrix[2][2];
		mMatrix.m23 = (float)matrix[3][2];
		mMatrix.m30 = (float)matrix[0][3];
		mMatrix.m31 = (float)matrix[1][3];
		mMatrix.m32 = (float)matrix[2][3];
		mMatrix.m33 = (float)matrix[3][3];
	}
	
}
