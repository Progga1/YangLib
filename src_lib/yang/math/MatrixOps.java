package yang.math;

import java.util.Arrays;

public class MatrixOps {

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
	
	public static final float[] IDENTITY = 
		{
			1,0,0,0,
			0,1,0,0,
			0,0,1,0,
			0,0,0,1
		};
	
	public static void setIdentity(float[] target) {
		System.arraycopy(IDENTITY, 0, target, 0, 16);
	}
	
	public static void setTranslation(float[] target,float x,float y,float z) {
		System.arraycopy(IDENTITY, 0, target, 0, 16);
		target[M03] = x;
		target[M13] = y;
		target[M23] = z;
	}
	
	public static void setScale(float[] target,float x,float y,float z) {
		Arrays.fill(target,0,16,0);
		target[M00] = x;
		target[M11] = y;
		target[M22] = z;
		target[M33] = 1;
	}
	
	public static void setRotationX(float[] target,float angle) {
		System.arraycopy(IDENTITY, 0, target, 0, 16);
		float sinA = (float)Math.sin(angle);
		float cosA = (float)Math.cos(angle);
		target[M11] = cosA;
		target[M12] = -sinA;
		target[M21] = sinA;
		target[M22] = cosA;
	}
	
	public static void setRotationY(float[] target,float angle) {
		System.arraycopy(IDENTITY, 0, target, 0, 16);
		float sinA = (float)Math.sin(angle);
		float cosA = (float)Math.cos(angle);
		target[M00] = cosA;
		target[M20] = sinA;
		target[M02] = -sinA;
		target[22] = cosA;
	}
	
	public static void setRotationZ(float[] target,float angle) {
		System.arraycopy(IDENTITY, 0, target, 0, 16);
		float sinA = (float)Math.sin(angle);
		float cosA = (float)Math.cos(angle);
		target[M00] = cosA;
		target[M01] = -sinA;
		target[M10] = sinA;
		target[M11] = cosA;
	}
	
	public static void multiply(float[] target,float[] lhsMatrix,float[] rhsMatrix) {
		for(int i=0;i<4;i++) {
			for(int j=0;j<4;j++) {
				target[i+j*4] = 
						 lhsMatrix[i]*rhsMatrix[j*4]
						+lhsMatrix[i+4]*rhsMatrix[1+j*4]
						+lhsMatrix[i+8]*rhsMatrix[2+j*4]
						+lhsMatrix[i+12]*rhsMatrix[3+j*4];
			}
		}
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
	
	/**
	 * From android.opengl.matrix, license: http://www.apache.org/licenses/LICENSE-2.0
	 */
	public static boolean invert(float[] target,float[] matrix, float[] tempMat1, float[] tempMat2) {
        // Invert a 4 x 4 matrix using Cramer's Rule

        // transpose matrix
		float[] transposed = target;
		transpose(transposed, matrix);

        // calculate pairs for first 8 elements (cofactors)
        tempMat1[0] = transposed[10] * transposed[15];
        tempMat1[1] = transposed[11] * transposed[14];
        tempMat1[2] = transposed[9] * transposed[15];
        tempMat1[3] = transposed[11] * transposed[13];
        tempMat1[4] = transposed[9] * transposed[14];
        tempMat1[5] = transposed[10] * transposed[13];
        tempMat1[6] = transposed[8] * transposed[15];
        tempMat1[7] = transposed[11] * transposed[12];
        tempMat1[8] = transposed[8] * transposed[14];
        tempMat1[9] = transposed[10] * transposed[12];
        tempMat1[10] = transposed[8] * transposed[13];
        tempMat1[11] = transposed[9] * transposed[12];

        // calculate first 8 elements (cofactors)
        tempMat2[0] = tempMat1[0] * transposed[5] + tempMat1[3] * transposed[6] + tempMat1[4] * transposed[7];
        tempMat2[0] -= tempMat1[1] * transposed[5] + tempMat1[2] * transposed[6] + tempMat1[5] * transposed[7];
        tempMat2[1] = tempMat1[1] * transposed[4] + tempMat1[6] * transposed[6] + tempMat1[9] * transposed[7];
        tempMat2[1] -= tempMat1[0] * transposed[4] + tempMat1[7] * transposed[6] + tempMat1[8] * transposed[7];
        tempMat2[2] = tempMat1[2] * transposed[4] + tempMat1[7] * transposed[5] + tempMat1[10] * transposed[7];
        tempMat2[2] -= tempMat1[3] * transposed[4] + tempMat1[6] * transposed[5] + tempMat1[11] * transposed[7];
        tempMat2[3] = tempMat1[5] * transposed[4] + tempMat1[8] * transposed[5] + tempMat1[11] * transposed[6];
        tempMat2[3] -= tempMat1[4] * transposed[4] + tempMat1[9] * transposed[5] + tempMat1[10] * transposed[6];
        tempMat2[4] = tempMat1[1] * transposed[1] + tempMat1[2] * transposed[2] + tempMat1[5] * transposed[3];
        tempMat2[4] -= tempMat1[0] * transposed[1] + tempMat1[3] * transposed[2] + tempMat1[4] * transposed[3];
        tempMat2[5] = tempMat1[0] * transposed[0] + tempMat1[7] * transposed[2] + tempMat1[8] * transposed[3];
        tempMat2[5] -= tempMat1[1] * transposed[0] + tempMat1[6] * transposed[2] + tempMat1[9] * transposed[3];
        tempMat2[6] = tempMat1[3] * transposed[0] + tempMat1[6] * transposed[1] + tempMat1[11] * transposed[3];
        tempMat2[6] -= tempMat1[2] * transposed[0] + tempMat1[7] * transposed[1] + tempMat1[10] * transposed[3];
        tempMat2[7] = tempMat1[4] * transposed[0] + tempMat1[9] * transposed[1] + tempMat1[10] * transposed[2];
        tempMat2[7] -= tempMat1[5] * transposed[0] + tempMat1[8] * transposed[1] + tempMat1[11] * transposed[2];

        // calculate pairs for second 8 elements (cofactors)
        tempMat1[0] = transposed[2] * transposed[7];
        tempMat1[1] = transposed[3] * transposed[6];
        tempMat1[2] = transposed[1] * transposed[7];
        tempMat1[3] = transposed[3] * transposed[5];
        tempMat1[4] = transposed[1] * transposed[6];
        tempMat1[5] = transposed[2] * transposed[5];
        tempMat1[6] = transposed[0] * transposed[7];
        tempMat1[7] = transposed[3] * transposed[4];
        tempMat1[8] = transposed[0] * transposed[6];
        tempMat1[9] = transposed[2] * transposed[4];
        tempMat1[10] = transposed[0] * transposed[5];
        tempMat1[11] = transposed[1] * transposed[4];

        // calculate second 8 elements (cofactors)
        tempMat2[8] = tempMat1[0] * transposed[13] + tempMat1[3] * transposed[14] + tempMat1[4] * transposed[15];
        tempMat2[8] -= tempMat1[1] * transposed[13] + tempMat1[2] * transposed[14] + tempMat1[5] * transposed[15];
        tempMat2[9] = tempMat1[1] * transposed[12] + tempMat1[6] * transposed[14] + tempMat1[9] * transposed[15];
        tempMat2[9] -= tempMat1[0] * transposed[12] + tempMat1[7] * transposed[14] + tempMat1[8] * transposed[15];
        tempMat2[10] = tempMat1[2] * transposed[12] + tempMat1[7] * transposed[13] + tempMat1[10] * transposed[15];
        tempMat2[10] -= tempMat1[3] * transposed[12] + tempMat1[6] * transposed[13] + tempMat1[11] * transposed[15];
        tempMat2[11] = tempMat1[5] * transposed[12] + tempMat1[8] * transposed[13] + tempMat1[11] * transposed[14];
        tempMat2[11] -= tempMat1[4] * transposed[12] + tempMat1[9] * transposed[13] + tempMat1[10] * transposed[14];
        tempMat2[12] = tempMat1[2] * transposed[10] + tempMat1[5] * transposed[11] + tempMat1[1] * transposed[9];
        tempMat2[12] -= tempMat1[4] * transposed[11] + tempMat1[0] * transposed[9] + tempMat1[3] * transposed[10];
        tempMat2[13] = tempMat1[8] * transposed[11] + tempMat1[0] * transposed[8] + tempMat1[7] * transposed[10];
        tempMat2[13] -= tempMat1[6] * transposed[10] + tempMat1[9] * transposed[11] + tempMat1[1] * transposed[8];
        tempMat2[14] = tempMat1[6] * transposed[9] + tempMat1[11] * transposed[11] + tempMat1[3] * transposed[8];
        tempMat2[14] -= tempMat1[10] * transposed[11] + tempMat1[2] * transposed[8] + tempMat1[7] * transposed[9];
        tempMat2[15] = tempMat1[10] * transposed[10] + tempMat1[4] * transposed[8] + tempMat1[9] * transposed[9];
        tempMat2[15] -= tempMat1[8] * transposed[9] + tempMat1[11] * transposed[10] + tempMat1[5] * transposed[8];

        // calculate determinant
        float det = transposed[0] * tempMat2[0] + transposed[1] * tempMat2[1] + transposed[2] * tempMat2[2] + transposed[3] * tempMat2[3];

        if (det == 0.0f) {
        	return false;
        }

        // calculate matrix inverse
        det = 1 / det;
        for (int j = 0; j < 16; j++)
            target[j] = tempMat2[j] * det;

        return true;
	}
	
	
	public static final float applyFloatMatrixX2D(float[] matrix, float x, float y) {
		return matrix[0] * x + matrix[4] * y + matrix[12];
	}
	
	public static final float applyFloatMatrixY2D(float[] matrix, float x, float y) {
		return matrix[1] * x + matrix[5] * y + matrix[13];
	}
	
	public static final float applyFloatMatrixX3D(float[] matrix, float x, float y, float z) {
		return matrix[0] * x + matrix[4] * y + matrix[8] * z + matrix[12];
	}
	
	public static final float applyFloatMatrixY3D(float[] matrix, float x, float y, float z) {
		return matrix[1] * x + matrix[5] * y + matrix[9] * z + matrix[13];
	}
	
	public static final float applyFloatMatrixZ3D(float[] matrix, float x, float y, float z) {
		return matrix[2] * x + matrix[6] * y + matrix[10] * z + matrix[14];
	}
	
	public static final float applyFloatMatrixW3D(float[] matrix, float x, float y, float z) {
		return matrix[3] * x + matrix[7] * y + matrix[11] * z + matrix[15];
	}

	public static final void applyFloatMatrix2D(float[] matrix, float x, float y, float[] targetVector, int targetOffset) {
		targetVector[targetOffset] = applyFloatMatrixX2D(matrix,x,y);
		targetVector[targetOffset+1] = applyFloatMatrixY2D(matrix,x,y);
	}
	
	public static final void applyFloatMatrix3D(float[] matrix, float x, float y, float z, float[] targetVector, int targetOffset) {
		targetVector[targetOffset] = matrix[0] * x + matrix[4] * y + matrix[8] * z + matrix[12];
		targetVector[targetOffset+1] = matrix[1] * x + matrix[5] * y + matrix[9] * z + matrix[13];
		targetVector[targetOffset+2] = matrix[2] * x + matrix[6] * y + matrix[10] * z + matrix[14];
		if(targetVector.length>3) {
			targetVector[targetOffset+3] = applyFloatMatrixW3D(matrix,x,y,z);
		}
	}
	
	public static final void applyFloatMatrix3DNormalized(float[] matrix, float x, float y, float z, float[] target, int targetOffset) {
		float w = 1f/applyFloatMatrixW3D(matrix,x,y,z);
		target[targetOffset] = (matrix[0] * x + matrix[4] * y + matrix[8] * z + matrix[12])*w;
		target[targetOffset+1] = (matrix[1] * x + matrix[5] * y + matrix[9] * z + matrix[13])*w;
		target[targetOffset+2] = (matrix[2] * x + matrix[6] * y + matrix[10] * z + matrix[14])*w;	
	}
	
	public static final float[] createMatrixCopy(float[] src) {
		float[] result = new float[16];
		System.arraycopy(src, 0, result, 0, 16);
		return result;
	}
	
	public static final void copyMatrix(float[] dest, float[] src) {
		System.arraycopy(src, 0, dest, 0, 16);
	}
	
	public static final String matToString(float[] matrix) {
		String result = "";
		for(int i=0;i<4;i++){
			for(int j=0;j<4;j++) {
				if(j>0)
					result+=" ";
				result += matrix[j*4+i];
			}
			result += "\r\n";
		}
		return result;
	}
	
	public static final String matToStringLinear(float[] matrix) {
		String result = "";
		for(int i=0;i<16;i++){
			if(i>0)
				result += " ";
			result += matrix[i];
			}
		return result;
	}
	
}
