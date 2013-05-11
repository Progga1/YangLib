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
	
	public static void setIdentity(float[] target) {
		Arrays.fill(target,0,16,0);
		target[M00] = 1;
		target[M11] = 1;
		target[M22] = 1;
		target[M33] = 1;
	}
	
	public static void setTranslation(float[] target,float x,float y,float z) {
		setIdentity(target);
		target[M03] = x;
		target[M13] = y;
		target[M23] = z;
	}
	
	public static void setRotationX(float[] target,float angle) {
		setIdentity(target);
		float sinA = (float)Math.sin(angle);
		float cosA = (float)Math.cos(angle);
		target[M11] = cosA;
		target[M12] = -sinA;
		target[M21] = sinA;
		target[M22] = cosA;
	}
	
	public static void setRotationY(float[] target,float angle) {
		setIdentity(target);
		float sinA = (float)Math.sin(angle);
		float cosA = (float)Math.cos(angle);
		target[M00] = cosA;
		target[M20] = sinA;
		target[M02] = -sinA;
		target[22] = cosA;
	}
	
	public static void setRotationZ(float[] target,float angle) {
		setIdentity(target);
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
				float sum = 0;
				for(int k=0;k<4;k++) {
					sum += lhsMatrix[i+k*4]*rhsMatrix[k+j*4];
				}
				target[i+j*4] = sum;
			}
		}
	}
	
	
}
