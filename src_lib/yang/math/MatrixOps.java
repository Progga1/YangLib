package yang.math;

import java.util.Arrays;

import yang.math.objects.Point3f;
import yang.math.objects.YangMatrix;

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

	public static void identity4f(float[] target) {
		System.arraycopy(IDENTITY, 0, target, 0, 16);
	}

	public static void identity3f(float[] target) {
		target[0] = 1;
		target[1] = 0;
		target[2] = 0;
		target[3] = 0;
		target[4] = 1;
		target[5] = 0;
		target[6] = 0;
		target[7] = 0;
		target[8] = 1;
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
		final float sinA = (float)Math.sin(angle);
		final float cosA = (float)Math.cos(angle);
		target[M11] = cosA;
		target[M12] = -sinA;
		target[M21] = sinA;
		target[M22] = cosA;
	}

	public static void setRotationY(float[] target,float angle) {
		System.arraycopy(IDENTITY, 0, target, 0, 16);
		final float sinA = (float)Math.sin(angle);
		final float cosA = (float)Math.cos(angle);
		target[M00] = cosA;
		target[M20] = -sinA;
		target[M02] = sinA;
		target[M22] = cosA;
	}

	public static void setRotationZ(float[] target,float angle) {
		System.arraycopy(IDENTITY, 0, target, 0, 16);
		final float sinA = (float)Math.sin(angle);
		final float cosA = (float)Math.cos(angle);
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

	public static void multiplyRightTransposed(float[] target,float[] lhsMatrix,float[] rhsMatrix) {
		for(int i=0;i<4;i++) {
			for(int j=0;j<4;j++) {
				target[i+j*4] =
						 lhsMatrix[i]*rhsMatrix[j]
						+lhsMatrix[i+4]*rhsMatrix[j+4]
						+lhsMatrix[i+8]*rhsMatrix[j+8]
						+lhsMatrix[i+12]*rhsMatrix[j+12];
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
		final float[] transposed = target;
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

	public static final void createDirectionTrafo(float[] target, float dirX,float dirY,float dirZ) {
		float rX,rY,rZ;
		final float absX = dirX<0?-dirX:dirX;
		final float absY = dirY<0?-dirY:dirY;
		final float absZ = dirZ<0?-dirZ:dirZ;
		if(absX<=absY && absX<=absZ) {
			rX = 0;
			rY = -dirZ;
			rZ = dirY;
		}else if(absY<=absX && absY<=absZ) {
			rX = -dirZ;
			rY = 0;
			rZ = dirX;
		}else{
			rX = -dirY;
			rY = dirX;
			rZ = 0;
		}
		final float rMagn = 1/(float)Math.sqrt(rX*rX + rY*rY + rZ*rZ);
		rX *= rMagn;
		rY *= rMagn;
		rZ *= rMagn;
		final float crossX = dirY*rZ - dirZ*rY;
		final float crossY = dirZ*rX - dirX*rZ;
		final float crossZ = dirX*rY - dirY*rX;
		target[0] = rX;
		target[1] = rY;
		target[2] = rZ;
		target[3] = 0;
		target[4] = dirX;
		target[5] = dirY;
		target[6] = dirZ;
		target[7] = 0;
		target[8] = -crossX;
		target[9] = -crossY;
		target[10] = -crossZ;
		target[11] = 0;
		target[12] = 0;
		target[13] = 0;
		target[14] = 0;
		target[15] = 1;
	}

	public static final void createDirectionTrafo(float[] target, float dirX,float dirY,float dirZ, float upX,float upY,float upZ) {

		float rightX = dirZ*upY - dirY*upZ;
		float rightY = dirX*upZ - dirZ*upX;
		float rightZ = dirY*upX - dirX*upY;
		final float rMagn = 1/(float)Math.sqrt(rightX*rightX + rightY*rightY + rightZ*rightZ);
		rightX *= rMagn;
		rightY *= rMagn;
		rightZ *= rMagn;
		final float rUpX = dirY*rightZ - dirZ*rightY;
		final float rUpY = dirZ*rightX - dirX*rightZ;
		final float rUpZ = dirX*rightY - dirY*rightX;

		target[0] = rightX;
		target[1] = rightY;
		target[2] = rightZ;
		target[3] = 0;
		target[4] = rUpX;
		target[5] = rUpY;
		target[6] = rUpZ;
		target[7] = 0;
		target[8] = dirX;
		target[9] = dirY;
		target[10] = dirZ;
		target[11] = 0;
		target[12] = 0;
		target[13] = 0;
		target[14] = 0;
		target[15] = 1;
	}

	public static void setLine(YangMatrix target, float fromX, float fromY, float toX, float toY, float width) {
		target.loadIdentity();
		float angle;
		float dx = toX - fromX;
		float dy = toY - fromY;
		float r = (float) Math.sqrt(dx * dx + dy * dy);
		if (r == 0) {
			target.scale(0);
			return;
		}
		if (dy < 0)
			angle = -(float) Math.acos(dx / r);
		else
			angle = (float) Math.acos(dx / r);
		angle += (float) Math.PI * 0.5f;

		target.translate(fromX, fromY);
		target.rotateZ(angle);
		target.scale(width, r);
		target.translate(-0.5f, -1);
	}

	public static void setLookDir(float[] target,float dirX,float dirY,float dirZ, float upX, float upY, float upZ) {

		float rightX = -dirY*upZ + dirZ*upY;
		float rightY = -dirZ*upX + dirX*upZ;
		float rightZ = -dirX*upY + dirY*upX;

		float rightDist = (float)Math.sqrt(rightX*rightX + rightY*rightY + rightZ*rightZ);
		if(rightDist == 0) {
			rightX = 1;
			rightDist = 1;
		}
		if(rightDist!=1) {
			float dDist = 1/rightDist;
			rightX *= dDist;
			rightY *= dDist;
			rightZ *= dDist;
		}

		upX = -rightY*dirZ + rightZ*dirY;
		upY = -rightZ*dirX + rightX*dirZ;
		upZ = -rightX*dirY + rightY*dirX;

		target[0] = rightX;
		target[1] = rightY;
		target[2] = rightZ;
		target[3] = 0;

		target[4] = upX;
		target[5] = upY;
		target[6] = upZ;
		target[7] = 0;

		target[8] = dirX;
		target[9] = dirY;
		target[10] = dirZ;
		target[11] = 0;

		target[12] = 0;
		target[13] = 0;
		target[14] = 0;
		target[15] = 1;
	}

	public static void setLookAt(float[] target,float eyeX, float eyeY, float eyeZ, float lookAtX, float lookAtY, float lookAtZ, float upX, float upY, float upZ) {

		float dx = eyeX-lookAtX;
		float dy = eyeY-lookAtY;
		float dz = eyeZ-lookAtZ;

		float dist = (float)Math.sqrt(dx*dx + dy*dy + dz*dz);
		if(dist==0) {
			dz = 1;
			dist = 1;
		}
		if(dist!=1) {
			float dDist = 1/dist;
			dx *= dDist;
			dy *= dDist;
			dz *= dDist;
		}

		float rightX = -dy*upZ + dz*upY;
		float rightY = -dz*upX + dx*upZ;
		float rightZ = -dx*upY + dy*upX;

		float rightDist = (float)Math.sqrt(rightX*rightX + rightY*rightY + rightZ*rightZ);
		if(rightDist == 0) {
			rightX = 1;
			rightDist = 1;
		}
		if(rightDist!=1) {
			float dDist = 1/rightDist;
			rightX *= dDist;
			rightY *= dDist;
			rightZ *= dDist;
		}

		upX = -rightY*dz + rightZ*dy;
		upY = -rightZ*dx + rightX*dz;
		upZ = -rightX*dy + rightY*dx;

		target[0] = rightX;
		target[1] = rightY;
		target[2] = rightZ;
		target[3] = 0;

		target[4] = upX;
		target[5] = upY;
		target[6] = upZ;
		target[7] = 0;

		target[8] = dx;
		target[9] = dy;
		target[10] = dz;
		target[11] = 0;

		target[12] = eyeX;
		target[13] = eyeY;
		target[14] = eyeZ;
		target[15] = 1;
	}

	public static void setLookAt(float[] target,Point3f eye,Point3f lookAt,Point3f up) {
		setLookAt(target,eye.mX,eye.mY,eye.mZ, lookAt.mX,lookAt.mY,lookAt.mZ, up.mX,up.mY,up.mZ);
	}

	public static void setLookAtAlphaBeta(float[] target,float focusX, float focusY, float focusZ, float alpha, float beta,float distance,Point3f outPosition) {
		final float eyeX = focusX+(float)(Math.sin(alpha)*Math.cos(beta))*distance;
		final float eyeY = focusY+(float)Math.sin(beta)*distance;
		final float eyeZ = focusZ+(float)(Math.cos(alpha)*Math.cos(beta))*distance;
		setLookAt(target, eyeX,eyeY,eyeZ, focusX,focusY,focusZ, 0,1,0);
		if(outPosition!=null)
			outPosition.set(eyeX,eyeY,eyeZ);
	}

	public static void setLookAtInverse(float[] target,float eyeX, float eyeY, float eyeZ, float lookAtX, float lookAtY, float lookAtZ, float upX, float upY, float upZ) {

		float dx = eyeX-lookAtX;
		float dy = eyeY-lookAtY;
		float dz = eyeZ-lookAtZ;

		float dist = (float)Math.sqrt(dx*dx + dy*dy + dz*dz);
		if(dist==0) {
			dz = 1;
			dist = 1;
		}
		if(dist!=1) {
			float dDist = 1/dist;
			dx *= dDist;
			dy *= dDist;
			dz *= dDist;
		}

		float rightX = -dy*upZ + dz*upY;
		float rightY = -dz*upX + dx*upZ;
		float rightZ = -dx*upY + dy*upX;

		float rightDist = (float)Math.sqrt(rightX*rightX + rightY*rightY + rightZ*rightZ);
		if(rightDist == 0) {
			rightX = 1;
			rightDist = 1;
		}
		if(rightDist!=1) {
			float dDist = 1/rightDist;
			rightX *= dDist;
			rightY *= dDist;
			rightZ *= dDist;
		}

		upX = -rightY*dz + rightZ*dy;
		upY = -rightZ*dx + rightX*dz;
		upZ = -rightX*dy + rightY*dx;

		target[0] = rightX;
		target[1] = upX;
		target[2] = dx;
		target[3] = 0;

		target[4] = rightY;
		target[5] = upY;
		target[6] = dy;
		target[7] = 0;

		target[8] = rightZ;
		target[9] = upZ;
		target[10] = dz;
		target[11] = 0;

		target[12] = -(eyeX*rightX + eyeY*rightY + eyeZ*rightZ);
		target[13] = -(eyeX*upX + eyeY*upY + eyeZ*upZ);
		target[14] = -(eyeX*dx + eyeY*dy + eyeZ*dz);
		target[15] = 1;
	}

	public static void setLookAtAlphaBetaInverse(float[] target,float focusX, float focusY, float focusZ, float alpha, float beta,float distance,Point3f outPosition) {
		final float eyeX = focusX+(float)(Math.sin(alpha)*Math.cos(beta))*distance;
		final float eyeY = focusY+(float)Math.sin(beta)*distance;
		final float eyeZ = focusZ+(float)(Math.cos(alpha)*Math.cos(beta))*distance;
		setLookAtInverse(target, eyeX,eyeY,eyeZ, focusX,focusY,focusZ, 0,1,0);
		if(outPosition!=null)
			outPosition.set(eyeX,eyeY,eyeZ);
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

	public static final void applyFloatMatrix3D(float[] matrix, float x, float y, float z, Point3f targetVector) {
		targetVector.mX = matrix[0] * x + matrix[4] * y + matrix[8] * z + matrix[12];
		targetVector.mY = matrix[1] * x + matrix[5] * y + matrix[9] * z + matrix[13];
		targetVector.mZ = matrix[2] * x + matrix[6] * y + matrix[10] * z + matrix[14];
		float w = matrix[3] * x + matrix[7] * y + matrix[11] * z + matrix[15];
		if(w!=1 && w!=0) {
			w = 1/w;
			targetVector.mX *= w;
			targetVector.mY *= w;
			targetVector.mZ *= w;
		}
	}

	public static final void applyFloatMatrix3D(float[] matrix, Point3f point, Point3f targetVector) {
		applyFloatMatrix3D(matrix,point.mX,point.mY,point.mZ,targetVector);
	}

	public static final void applyFloatMatrix3DNormalized(float[] matrix, float x, float y, float z, float[] target, int targetOffset) {
		final float w = 1f/applyFloatMatrixW3D(matrix,x,y,z);
		target[targetOffset] = (matrix[0] * x + matrix[4] * y + matrix[8] * z + matrix[12])*w;
		target[targetOffset+1] = (matrix[1] * x + matrix[5] * y + matrix[9] * z + matrix[13])*w;
		target[targetOffset+2] = (matrix[2] * x + matrix[6] * y + matrix[10] * z + matrix[14])*w;
	}

	public static final void applyFloatMatrix3DNormalized(float[] matrix, float x, float y, float z, Point3f target) {
		float w = 1f/applyFloatMatrixW3D(matrix,x,y,z);
		target.mX = (matrix[0] * x + matrix[4] * y + matrix[8] * z + matrix[12])*w;
		target.mY = (matrix[1] * x + matrix[5] * y + matrix[9] * z + matrix[13])*w;
		target.mZ = (matrix[2] * x + matrix[6] * y + matrix[10] * z + matrix[14])*w;
	}

	public static final float[] createMatrixCopy(float[] src) {
		final float[] result = new float[16];
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
			result += "\n";
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
