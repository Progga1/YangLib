package yang.math;

public class FloatMatrix {

	public static float[] applyMatrix(float[] dest, float[][] matrix,float[] vector) {
		int cols = vector.length;
		int rows = matrix.length;
		float[] row;
		for(int i=0;i<rows;i++) {
			row = matrix[i];
			float val = 0;
			for(int k=0;k<cols;k++) {
				val += row[k]*vector[k];
			}
			dest[i] = val;
		}
		return dest;
	}
	
	public static final void multiplyMatrices(float[][] dest, float[][] lhsMat,float[][] rhsMat) {
		int cols = rhsMat.length;
		int rows = lhsMat.length;
		float[] row;
		for(int i=0;i<rows;i++) {
			row = lhsMat[i];
			for(int j=0;j<cols;j++) {
				float val = 0;
				for(int k=0;k<cols;k++) {
					val += row[k]*rhsMat[k][j];
				}
				dest[i][j] = val;
			}
		}
	}
	
	public static void extractMatrixColumn(float[] dest,float[][] matrix,int column) {
		for(int i=0;i<dest.length;i++) {
			dest[i] = matrix[i][column];
		}
	}
	
	public static void setMatrixColumn(float[][] matrix,float[] columnVec,int column) {
		for(int i=0;i<matrix.length;i++) {
			matrix[i][column] = columnVec[i];
		}
	}
	
	public static void create3x3MatrixByColumns(float[][] dest,float[] col1,float[] col2,float[] col3) {
		for(int i=0;i<dest.length;i++) {
			dest[i][0] = col1[i];
			dest[i][1] = col2[i];
			dest[i][2] = col3[i];
		}
	}
	
	public static void zeroVec(float[] vector) {
		for(int i=0;i<vector.length;i++)
			vector[i] = 0;
	}
	
	public static void zeroMat(float[][] matrix) {
		for(int i=0;i<matrix.length;i++) {
			float[] row = matrix[i];
			for(int j=0;j<row.length;j++)
				row[j] = 0;
		}
	}
	
	public static void addVector(float[] dest,float[] vec1,float[] vec2) {
		for(int i=0;i<dest.length;i++) {
			dest[i] = vec1[i] + vec2[i];
		}
	}
	
	public static void subtractVector(float[] dest,float[] vec1,float[] vec2) {
		for(int i=0;i<dest.length;i++) {
			dest[i] = vec1[i] - vec2[i];
		}
	}
	
	public static void copyMat(float[][] dest,float[][] src) {
		for(int i=0;i<dest.length;i++) {
			float[] srcRow = src[i];
			float[] destRow = dest[i];
			for(int j=0;j<destRow.length;j++)
				destRow[j] = srcRow[j];
		}
	}
	
	public static void multiplyMatrix(float[][] dest,float[][] matrix,float factor) {
		for(int i=0;i<dest.length;i++) {
			float[] srcRow = matrix[i];
			float[] destRow = dest[i];
			for(int j=0;j<destRow.length;j++)
				destRow[j] = srcRow[j]*factor;
		}
	}
	
	public static void multiplyMatrix(float[][] dest,float factor) {
		for(int i=0;i<dest.length;i++) {
			float[] destRow = dest[i];
			for(int j=0;j<destRow.length;j++)
				destRow[j] *= factor;
		}
	}
	
	public static float[][] addMatrix(float[][] dest,float[][] lhsMat,float[][] rhsMat) {
		for(int i=0;i<dest.length;i++) {
			float[] lhsRow = lhsMat[i];
			float[] rhsRow = rhsMat[i];
			float[] destRow = dest[i];
			for(int j=0;j<destRow.length;j++)
				destRow[j] = lhsRow[j]+rhsRow[j];
		}
		return dest;
	}
	
	public static float[][] subtractMatrix(float[][] dest,float[][] lhsMat,float[][] rhsMat) {
		for(int i=0;i<dest.length;i++) {
			float[] lhsRow = lhsMat[i];
			float[] rhsRow = rhsMat[i];
			float[] destRow = dest[i];
			for(int j=0;j<destRow.length;j++)
				destRow[j] = lhsRow[j]-rhsRow[j];
		}
		return dest;
	}
	
	public static float[][] transposeMatrix(float[][] dest,float[][] matrix) {
		for(int i=0;i<dest.length;i++) {
			float[] destRow = dest[i];
			for(int j=0;j<destRow.length;j++)
				destRow[j] = matrix[j][i];
		}
		return dest;
	}
	
	public static void scaleVector(float[] vector,float factor) {
		for(int i=0;i<vector.length;i++)
			vector[i] *= factor;
	}
	
	public static void scaleVector(float[] dest,float[] vector,float factor) {
		for(int i=0;i<vector.length;i++)
			dest[i] = vector[i]*factor;
	}
	
}
