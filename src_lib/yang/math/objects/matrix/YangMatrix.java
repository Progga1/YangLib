package yang.math.objects.matrix;

import yang.math.MatrixOps;
import yang.math.objects.Point3f;
import yang.math.objects.Quaternion;
import yang.math.objects.Vector3f;
import yang.util.Util;

//TODO normal inversion only 3x3, autocreate inversed mat, save orthogonal etc information

public class YangMatrix {

	public static final YangMatrix IDENTITY = new YangMatrix();

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

	public float[] mValues;
	public float[] mInverted;
	protected float[] mBackMatrix;
	protected float[] mTempMat1,mTempMat2;
	protected float[][] mStack;
	public int mStackPointer;	//pre increment

	public static float TO_RAD_FACTOR = (float) Math.PI / 180;
	public static float TO_DEG_FACTOR = 180 / (float) Math.PI;

	public static void identity4f(float[] matrix) {
		matrix[0] = 1;
		matrix[1] = 0;
		matrix[2] = 0;
		matrix[3] = 0;
		matrix[4] = 0;
		matrix[5] = 1;
		matrix[6] = 0;
		matrix[7] = 0;
		matrix[8] = 0;
		matrix[9] = 0;
		matrix[10] = 1;
		matrix[11] = 0;
		matrix[12] = 0;
		matrix[13] = 0;
		matrix[14] = 0;
		matrix[15] = 1;
	}

	public static void identity3f(float[] matrix) {
		matrix[0] = 1;
		matrix[1] = 0;
		matrix[2] = 0;
		matrix[3] = 0;
		matrix[4] = 1;
		matrix[5] = 0;
		matrix[6] = 0;
		matrix[7] = 0;
		matrix[8] = 1;
	}

	public YangMatrix() {
		mValues = new float[16];
		mBackMatrix = new float[16];
		mTempMat1 = new float[16];
		mTempMat2 = new float[16];
		mInverted = null;
		mStack = null;
		mStackPointer = -1;
		loadIdentity();
	}

	public void initStack(int capacity) {
		mStack = new float[capacity][16];
		mStackPointer = -1;
		//MatrixOps.setIdentity(mStack[0]);
	}

	public void stackClear() {
		mStackPointer = -1;
	}

	public void stackGoto(int absoluteIndex) {
		mStackPointer = absoluteIndex;
	}

	public void stackDecPointer(int dec) {
		mStackPointer -= dec;
	}

	public void stackIncPointer(int dec) {
		mStackPointer += dec;
	}

	public void stackPush() {
		if(mStack==null)
			initStack(8);
		else if(mStackPointer>=mStack.length)
			mStack = Util.resizeArray(mStack,new float[mStackPointer+2][16]);
		System.arraycopy(mValues,0,mStack[++mStackPointer],0,16);
	}

	public void stackPop() {
		System.arraycopy(mStack[mStackPointer--],0,mValues,0,16);
	}

	public void stackGetTop() {
		System.arraycopy(mStack[mStackPointer],0,mValues,0,16);
	}

	public void stackRefreshTop() {
		System.arraycopy(mValues,0,mStack[mStackPointer],0,16);
	}

	public void stackGet(int index) {
		System.arraycopy(mStack[mStackPointer-index],0,mValues,0,16);
	}

	public void stackSet(int index) {
		System.arraycopy(mValues,0,mStack[mStackPointer-index],0,16);
	}

	public void loadIdentity() {
		System.arraycopy(MatrixOps.IDENTITY, 0, mValues, 0, 16);
	}

	public final void translate(float x, float y, float z) {
		mValues[12] += mValues[0]*x + mValues[4]*y + mValues[8]*z;
		mValues[13] += mValues[1]*x + mValues[5]*y + mValues[9]*z;
		mValues[14] += mValues[2]*x + mValues[6]*y + mValues[10]*z;
		mValues[15] += mValues[3]*x + mValues[7]*y + mValues[11]*z;
	}

	public final void translate(float x, float y) {
		translate(x, y, 0);
	}

	public final void translate(Point3f translationVector) {
		translate(translationVector.mX,translationVector.mY,translationVector.mZ);
	}

	public final void postTranslate(float x,float y,float z) {
		mValues[12] += x;
		mValues[13] += y;
		mValues[14] += z;
	}

	public final void postTranslate(float x,float y) {
		mValues[12] += x;
		mValues[13] += y;
	}

	public void postTranslate(Point3f vector) {
		mValues[12] += vector.mX;
		mValues[13] += vector.mY;
		mValues[14] += vector.mZ;
	}

	public void setTranslation(float x,float y,float z) {
		System.arraycopy(MatrixOps.IDENTITY, 0, mValues, 0, 16);
		mValues[12] = x;
		mValues[13] = y;
		mValues[14] = z;
	}

	public void setTranslation(float x,float y) {
		System.arraycopy(MatrixOps.IDENTITY, 0, mValues, 0, 16);
		mValues[12] = x;
		mValues[13] = y;
	}

	public void setTranslation(Point3f translation) {
		setTranslation(translation.mX,translation.mY,translation.mZ);
	}

	public void setScale(float x, float y, float z) {
		loadIdentity();
		mValues[0] = x;
		mValues[5] = y;
		mValues[10] = z;
	}

	public void setScale(float s) {
		loadIdentity();
		mValues[0] = s;
		mValues[5] = s;
		mValues[10] = s;
	}

	public void scale(float x, float y, float z) {
		for(int i=0;i<4;i++) {
			mValues[i] *= x;
			mValues[i+4] *= y;
			mValues[i+8] *= z;
		}
	}

	public void postScale(float x, float y, float z) {
		for(int i=0;i<4;i++) {
			mValues[i*4] *= x;
			mValues[i*4+1] *= y;
			mValues[i*4+2] *= z;
		}
	}

	public void scale(float x, float y) {
		scale(x, y, 1);
	}

	public void scale(float s) {
		scale(s,s,s);
	}

	public void scale(Vector3f scale) {
		scale(scale.mX, scale.mY, scale.mZ);
	}

	public void rotateX(float angle) {
		MatrixOps.setRotationX(mTempMat1,angle);
		MatrixOps.multiply(mBackMatrix,mValues,mTempMat1);
		final float[] swap = mValues;
		mValues = mBackMatrix;
		mBackMatrix = swap;
	}

	public void rotateY(float angle) {
		MatrixOps.setRotationY(mTempMat1,angle);
		MatrixOps.multiply(mBackMatrix,mValues,mTempMat1);
		final float[] swap = mValues;
		mValues = mBackMatrix;
		mBackMatrix = swap;
	}

	public void rotateZ(float angle) {
		MatrixOps.setRotationZ(mTempMat1,angle);
		MatrixOps.multiply(mBackMatrix,mValues,mTempMat1);
		final float[] swap = mValues;
		mValues = mBackMatrix;
		mBackMatrix = swap;
	}

	public void rotateZAround(float angle,float anchorX,float anchorY) {
		translate(-anchorX, -anchorY);
		rotateZ(angle);
		translate(anchorX,anchorY);
	}

	public void rotateAround(float rotVecX,float rotVecY,float rotVecZ, float angle) {
		MatrixOps.createDirectionTrafo(mTempMat2, rotVecX,rotVecY,rotVecZ);
		multiplyRight(mTempMat2);
		rotateY(angle);
		multiplyRightTransposed(mTempMat2);

		//Not working (Game physics engine development p. 169)
//		float c = (float)Math.cos(angle);
//		float s = (float)Math.sin(angle);
//		float t = 1.0f-c;
//		mMatrix[0] = t*x*x + c;
//		mMatrix[1] = t*x*y - s*z;
//		mMatrix[2] = t*x*z + s*y;
//		mMatrix[3] = 0;
//		mMatrix[4] = t*x*y + s*z;
//		mMatrix[5] = t*y*y + c;
//		mMatrix[6] = t*y*z - s*x;
//		mMatrix[7] = 0;
//		mMatrix[8] = t*x*z - s*y;
//		mMatrix[9] = t*y*z + s*x;
//		mMatrix[10] = t*z*z + x;
//		mMatrix[11] = 0;
//		mMatrix[12] = 0;
//		mMatrix[13] = 0;
//		mMatrix[14] = 0;
//		mMatrix[15] = 1;
	}

	public void mirrorAtPlane(float nx,float ny,float nz, Vector3f offset) {
		MatrixOps.createDirectionTrafo(mTempMat1, nx,ny,nz);
		if(offset!=null)
			translate(offset);
		multiplyRight(mTempMat1);
		scale(1,-1,1);
		multiplyRightTransposed(mTempMat1);
		if(offset!=null)
			translate(-offset.mX,-offset.mY,-offset.mZ);
	}

	public void rotateAround(Vector3f rotationVector, float angle) {
		rotateAround(rotationVector.mX,rotationVector.mY,rotationVector.mZ,angle);
	}

	public void multiplyRight(float[] rhsMatrix) {
		MatrixOps.multiply(mBackMatrix,mValues,rhsMatrix);
		final float[] swap = mValues;
		mValues = mBackMatrix;
		mBackMatrix = swap;
	}

	public void multiplyRightTransposed(float[] rhsMatrix) {
		MatrixOps.multiplyRightTransposed(mBackMatrix,mValues,rhsMatrix);
		final float[] swap = mValues;
		mValues = mBackMatrix;
		mBackMatrix = swap;
	}

	public void multiplyLeft(float[] lhsMatrix) {

		MatrixOps.multiply(mBackMatrix,lhsMatrix,mValues);

		final float[] swap = mValues;
		mValues = mBackMatrix;
		mBackMatrix = swap;
	}

	public void multiply(float[] lhsMatrix,float[] rhsMatrix) {
		MatrixOps.multiply(mValues,lhsMatrix,rhsMatrix);
	}

	public void multiply(YangMatrix lhsMatrix,YangMatrix rhsMatrix) {
		MatrixOps.multiply(mValues,lhsMatrix.mValues,rhsMatrix.mValues);
	}

	public void set(YangMatrix src) {
		System.arraycopy(src.mValues,0, this.mValues,0, 16);
	}

	public void set(float[] src) {
		System.arraycopy(src,0, this.mValues,0, 16);
	}

	public boolean asInverted(float[] target) {
		return MatrixOps.invert(target,mValues,mTempMat1,mTempMat2);
	}

	//TODO very inefficient!
	public boolean asNormalTransform4f(float[] target) {
		if(!refreshInverted())
			return false;
		target[0] = mInverted[0];
		target[1] = mInverted[4];
		target[2] = mInverted[8];
		target[3] = 0;
		target[4] = mInverted[1];
		target[5] = mInverted[5];
		target[6] = mInverted[9];
		target[7] = 0;
		target[8] = mInverted[2];
		target[9] = mInverted[6];
		target[10] = mInverted[10];
		target[11] = 0;
		target[12] = 0;
		target[13] = 0;
		target[14] = 0;
		target[15] = 1;
		return true;
	}

	public boolean asNormalTransform3f(float[] target) {
		if(!refreshInverted())
			return false;
		target[0] = mInverted[0];
		target[1] = mInverted[4];
		target[2] = mInverted[8];
		target[3] = mInverted[1];
		target[4] = mInverted[5];
		target[5] = mInverted[9];
		target[6] = mInverted[2];
		target[7] = mInverted[6];
		target[8] = mInverted[10];
		return true;
	}

	public boolean refreshInverted() {
		if(mInverted==null)
			mInverted = new float[16];
		return MatrixOps.invert(mInverted,mValues,mTempMat1,mTempMat2);
	}

	public void invert() {
		MatrixOps.invert(mBackMatrix,mValues,mTempMat1,mTempMat2);

		final float[] swap = mValues;
		mValues = mBackMatrix;
		mBackMatrix = swap;
	}

	public float get(int index) {
		return mValues[index];
	}

	public float get(int row, int column) {
		return mValues[column*4+row];
	}

	public void set(int row, int column, float value) {
		mValues[column*4+row] = value;
	}

	public void setColumn(int i, float x, float y, float z) {
		mValues[i*4] = x;
		mValues[i*4+1] = y;
		mValues[i*4+2] = z;
	}

	public void setColumn(int i, float x, float y, float z, float w) {
		mValues[i*4] = x;
		mValues[i*4+1] = y;
		mValues[i*4+2] = z;
		mValues[i*4+3] = w;
	}

	public void setRow(int i, float x, float y, float z) {
		mValues[i] = x;
		mValues[4+i] = y;
		mValues[8+i] = z;
	}

	public void setRow(int i, float x, float y, float z, float w) {
		mValues[i] = x;
		mValues[4+i] = y;
		mValues[8+i] = z;
		mValues[12+i] = w;
	}

	public void setRowMajor(double[][] matrix) {
		mValues[0] = (float)matrix[0][0];
		mValues[1] = (float)matrix[1][0];
		mValues[2] = (float)matrix[2][0];
		mValues[3] = (float)matrix[3][0];
		mValues[4] = (float)matrix[0][1];
		mValues[5] = (float)matrix[1][1];
		mValues[6] = (float)matrix[2][1];
		mValues[7] = (float)matrix[3][1];
		mValues[8] = (float)matrix[0][2];
		mValues[9] = (float)matrix[1][2];
		mValues[10] = (float)matrix[2][2];
		mValues[11] = (float)matrix[3][2];
		mValues[12] = (float)matrix[0][3];
		mValues[13] = (float)matrix[1][3];
		mValues[14] = (float)matrix[2][3];
		mValues[15] = (float)matrix[3][3];
	}

	public void setColumnMajor(double[][] matrix) {
		mValues[0] = (float)matrix[0][0];
		mValues[1] = (float)matrix[0][1];
		mValues[2] = (float)matrix[0][2];
		mValues[3] = (float)matrix[0][3];
		mValues[4] = (float)matrix[1][0];
		mValues[5] = (float)matrix[1][1];
		mValues[6] = (float)matrix[1][2];
		mValues[7] = (float)matrix[1][3];
		mValues[8] = (float)matrix[2][0];
		mValues[9] = (float)matrix[2][1];
		mValues[10] = (float)matrix[2][2];
		mValues[11] = (float)matrix[2][3];
		mValues[12] = (float)matrix[3][0];
		mValues[13] = (float)matrix[3][1];
		mValues[14] = (float)matrix[3][2];
		mValues[15] = (float)matrix[3][3];
	}

	public void multiplyQuaternionRight(Quaternion quaternion) {
		quaternion.toRotationMatrix(mTempMat1);
		multiplyRight(mTempMat1);
	}

	public void multiplyQuaternionLeft(Quaternion quaternion) {
		quaternion.toRotationMatrix(mTempMat1);
		multiplyLeft(mTempMat1);
	}

	public void setFromQuaternion(Quaternion quaternion) {
		quaternion.toRotationMatrix(mValues);
	}

	public void setFromQuaternion(float quatX,float quatY,float quatZ,float quatW) {
		Quaternion.toRotationMatrix(quatX, quatY, quatZ, quatW, mValues);
	}

	public void setFromQuaternion(float quatX,float quatY,float quatZ) {
		Quaternion.toRotationMatrix(quatX,quatY,quatZ,mValues);
	}

	public void multiplyRight(YangMatrix rhsMatrix) {
		multiplyRight(rhsMatrix.mValues);
	}

	public void multiplyLeft(YangMatrix lhsMatrix) {
		multiplyLeft(lhsMatrix.mValues);
	}

	public float[] asFloatArrayDeep() {
		return MatrixOps.createMatrixCopy(mValues);
	}

	public float apply2DX(float x, float y) {
		return MatrixOps.applyFloatMatrixX2D(mValues,x,y);
	}

	public float apply2DY(float x, float y) {
		return MatrixOps.applyFloatMatrixY2D(mValues,x,y);
	}

	public void apply2D(float x, float y, float[] target, int targetOffset) {
		MatrixOps.applyFloatMatrix2D(mValues,x,y,target,targetOffset);
	}

	public void apply3D(float x, float y, float z, float[] target, int targetOffset) {
		MatrixOps.applyFloatMatrix3D(mValues,x,y,z,target,targetOffset);
	}

	public void apply3D(float x, float y, float z,Point3f target) {
		MatrixOps.applyFloatMatrix3D(mValues, x, y, z, target);
	}

	public void apply3D(Point3f point,Point3f target) {
		MatrixOps.applyFloatMatrix3D(mValues, point.mX,point.mY,point.mZ, target);
	}

	public void apply3DNormalized(float x, float y, float z, float[] target, int targetOffset) {
		MatrixOps.applyFloatMatrix3DNormalized(mValues,x,y,z,target,targetOffset);
	}

	protected void setColumn(int col, Vector3f values) {
		setColumn(col,values.mX,values.mY,values.mZ);
	}

	protected void setRow(int row, Vector3f values) {
		setRow(row,values.mX,values.mY,values.mZ);
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
		final float[] matrix = this.mValues;
		if(!zComponent) {
			for(int i=0;i<vertexCount;i++) {
				final float x = source[sourceOffset++]+preShiftX;
				final float y = source[sourceOffset++]+preShiftY;
				target[targetOffset++] = matrix[0]*x+matrix[4]*y+matrix[12]+postShiftX;
				target[targetOffset++] = matrix[1]*x+matrix[5]*y+matrix[13]+postShiftY;
			}
		}else{
			for(int i=0;i<vertexCount;i++) {
				final float x = source[sourceOffset++]+preShiftX;
				final float y = source[sourceOffset++]+preShiftY;
				final float z = source[sourceOffset++];
				target[targetOffset++] = matrix[0]*x+matrix[4]*y+matrix[8]*z+matrix[12]+postShiftX;
				target[targetOffset++] = matrix[1]*x+matrix[5]*y+matrix[9]*z+matrix[13]+postShiftY;
				target[targetOffset++] = matrix[2]*x+matrix[6]*y+matrix[10]*z+matrix[14];
			}
		}
	}

	@Override
	public String toString() {
		return MatrixOps.matToString(mValues);
	}

	public static void setBase4f(float[] target,Vector3f vec1, Vector3f vec2, Vector3f vec3) {
		target[0] = vec1.mX;
		target[1] = vec1.mY;
		target[2] = vec1.mZ;
		target[3] = 0;
		target[4] = vec2.mX;
		target[5] = vec2.mY;
		target[6] = vec2.mZ;
		target[7] = 0;
		target[8] = vec3.mX;
		target[9] = vec3.mY;
		target[10] = vec3.mZ;
		target[11] = 0;
		target[12] = 0;
		target[13] = 0;
		target[14] = 0;
		target[15] = 1;
	}

	public void setPointToDirection(Vector3f direction) {
		MatrixOps.createDirectionTrafo(mValues, direction.mX,direction.mY,direction.mZ);
	}

	public void setPointToDirection(float dirX,float dirY,float dirZ) {
		MatrixOps.createDirectionTrafo(mValues, dirX,dirY,dirZ);
	}

	public void pointToDirection(Vector3f direction) {
		MatrixOps.createDirectionTrafo(mTempMat1, direction.mX,direction.mY,direction.mZ);
		multiplyRight(mTempMat1);
	}

	public void pointToDirection(float dirX,float dirY,float dirZ) {
		MatrixOps.createDirectionTrafo(mTempMat1, dirX,dirY,dirZ);
		multiplyRight(mTempMat1);
	}

	public void setPointToDirection(float dirX,float dirY,float dirZ, float upX,float upY,float upZ) {
		MatrixOps.createDirectionTrafo(mValues, dirX,dirY,dirZ, upX,upY,upZ);
	}

	public void setPointFromTo(Point3f fromPoint, Point3f toPoint, Vector3f upVector) {
		float dx = toPoint.mX - fromPoint.mX;
		float dy = toPoint.mY - fromPoint.mY;
		float dz = toPoint.mZ - fromPoint.mZ;
		final float rMagn = 1/(float)Math.sqrt(dx*dx + dy*dy + dz*dz);
		dx *= rMagn;
		dy *= rMagn;
		dz *= rMagn;
		MatrixOps.createDirectionTrafo(mValues, dx,dy,dz, upVector.mX,upVector.mY,upVector.mZ);
	}

	public void setBase(Vector3f vec1, Vector3f vec2, Vector3f vec3) {
		setBase4f(mValues,vec1,vec2,vec3);
	}

	public void multiplyBaseVectorsRight(Vector3f vec1, Vector3f vec2, Vector3f vec3) {
		setBase4f(mTempMat1,vec1,vec2,vec3);
		multiplyRight(mTempMat1);
	}

	public void multiplyBaseVectorsLeft(Vector3f vec1, Vector3f vec2, Vector3f vec3) {
		setBase4f(mTempMat1,vec1,vec2,vec3);
		multiplyLeft(mTempMat1);
	}

	public void setFromEulerAngles(float yaw,float pitch,float roll) {
		//TODO properly
		loadIdentity();
		rotateY(yaw);
		rotateX(pitch);
		rotateZ(roll);
		if(true)
			return;
		final float s1 = (float)Math.sin(roll);
		final float c1 = (float)Math.cos(roll);
		final float s2 = (float)Math.sin(pitch);
		final float c2 = (float)Math.cos(pitch);
		final float s3 = (float)Math.sin(yaw);
		final float c3 = (float)Math.cos(yaw);
//		mMatrix[0] = c2*c3;
//		mMatrix[1] = c1*s3 + c3*s1*s2;
//		mMatrix[2] = s1*s3-c1*c3*s2;
//		mMatrix[3] = 0;
//		mMatrix[4] = -c2*s3;
//		mMatrix[5] = c1*c3-s1*s2*s3;
//		mMatrix[6] = c3*s1+c1*s2*s3;
//		mMatrix[7] = 0;
//		mMatrix[8] = s2;
//		mMatrix[9] = -c2*s1;
//		mMatrix[10] = c1*c2;
//		mMatrix[11] = 0;
//		mMatrix[12] = 0;
//		mMatrix[13] = 0;
//		mMatrix[14] = 0;
//		mMatrix[15] = 1;

//		mMatrix[0] = c1*c2;
//		mMatrix[1] = c2*s1;
//		mMatrix[2] = -s2;
//		mMatrix[3] = 0;
//		mMatrix[4] = c1*s2*s3-c3*s1;
//		mMatrix[5] = c1*c3+s1*s2*s3;
//		mMatrix[6] = c2*s3;
//		mMatrix[7] = 0;
//		mMatrix[8] = s1*s3+c1*c3*s2;
//		mMatrix[9] = c3*s1*s2-c1*s3;
//		mMatrix[10] = c2*c3;
//		mMatrix[11] = 0;
//		mMatrix[12] = 0;
//		mMatrix[13] = 0;
//		mMatrix[14] = 0;
//		mMatrix[15] = 1;

		mValues[0] = c1*c3 + s1*s2*s3;
		mValues[1] = c2*s3;
		mValues[2] = c1*s2*s3-c3*s1;
		mValues[3] = 0;
		mValues[4] = c1*s1*s2-c1*s3;
		mValues[5] = c2*c3;
		mValues[6] = s1*s3+c1*c3*s2;
		mValues[7] = 0;
		mValues[8] = c2*s1;
		mValues[9] = -s2;
		mValues[10] = c1*c2;
		mValues[11] = 0;
		mValues[12] = 0;
		mValues[13] = 0;
		mValues[14] = 0;
		mValues[15] = 1;
	}

	public void getTranslation(Point3f target) {
		target.mX = mValues[12];
		target.mY = mValues[13];
		target.mZ = mValues[14];
	}

	@Override
	public YangMatrix clone() {
		YangMatrix result = new YangMatrix();
		result.set(this.mValues);
		return result;
	}

}
