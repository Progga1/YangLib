package yang.math.objects;

import yang.graphics.model.TransformationData;
import yang.math.MatrixOps;

//TODO normal inversion only 3x3, autocreate inversed mat, save orthogonal etc information

public class YangMatrix {

	public static final YangMatrix IDENTITY = new YangMatrix();

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
	public float[][] mStack;
	public int mStackPointer;	//pre increment

	public static float TO_RAD_FACTOR = (float) Math.PI / 180;
	public static float TO_DEG_FACTOR = 180 / (float) Math.PI;

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

	public static void setBase4fNormalized(float[] target,Vector3f vec1, Vector3f vec2, Vector3f vec3) {
		float dX = 1/vec1.magn();
		float dY = 1/vec2.magn();
		float dZ = 1/vec3.magn();
		target[0] = vec1.mX*dX;
		target[1] = vec1.mY*dX;
		target[2] = vec1.mZ*dX;
		target[3] = 0;
		target[4] = vec2.mX*dY;
		target[5] = vec2.mY*dY;
		target[6] = vec2.mZ*dY;
		target[7] = 0;
		target[8] = vec3.mX*dZ;
		target[9] = vec3.mY*dZ;
		target[10] = vec3.mZ*dZ;
		target[11] = 0;
		target[12] = 0;
		target[13] = 0;
		target[14] = 0;
		target[15] = 1;
	}

	public static void setBase4f(float[] target,Vector3f vec1, Vector3f vec2, Vector3f vec3, Point3f position) {
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
		target[12] = position.mX;
		target[13] = position.mY;
		target[14] = position.mZ;
		target[15] = 1;
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

	public YangMatrix(int stackCapacity) {
		this();
		initStack(stackCapacity);
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
		assert mStack!=null;
//		if(mStack==null)
//			throw new RuntimeException();
//		else if(mStackPointer>=mStack.length)
//			mStack = Util.resizeArray(mStack,new float[mStackPointer+2][16]);
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

	public final void translateNegative(Point3f translationVector) {
		translate(-translationVector.mX,-translationVector.mY,-translationVector.mZ);
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

	public void setTranslationNegative(Point3f translation) {
		setTranslation(-translation.mX,-translation.mY,-translation.mZ);
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

	public void postScale(float s) {
		postScale(s,s,s);
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

	public void rotate(EulerAngles eulerAngles) {
		rotateY(eulerAngles.mYaw);
		rotateX(eulerAngles.mPitch);
		rotateZ(eulerAngles.mRoll);
	}
	
	public void rotate(Quaternion quaternion) {
		multiplyQuaternionRight(quaternion);
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

	public void mirrorAtPlane(float nx,float ny,float nz, Point3f base) {
		MatrixOps.createDirectionTrafo(mTempMat1, nx,ny,nz);
		if(base!=null)
			translate(base);
		multiplyRight(mTempMat1);
		scale(1,-1,1);
		multiplyRightTransposed(mTempMat1);
		if(base!=null)
			translateNegative(base);
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
	
	public void multiplyRight(TransformationData transform) {
		translate(transform.mPosition);
		rotate(transform.mOrientation);
		scale(transform.mScale);
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

	public void asTransposed(float[] target) {
		target[0] = mValues[0];
		target[1] = mValues[4];
		target[2] = mValues[8];
		target[3] = mValues[12];
		target[4] = mValues[1];
		target[5] = mValues[5];
		target[6] = mValues[9];
		target[7] = mValues[13];
		target[8] = mValues[2];
		target[9] = mValues[6];
		target[10] = mValues[10];
		target[11] = mValues[11];
		target[12] = mValues[3];
		target[13] = mValues[7];
		target[14] = mValues[11];
		target[15] = mValues[15];
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

	public void setColumn(int col, float x, float y, float z) {
		mValues[col*4] = x;
		mValues[col*4+1] = y;
		mValues[col*4+2] = z;
	}

	public void setColumn(int col, float x, float y, float z, float w) {
		mValues[col*4] = x;
		mValues[col*4+1] = y;
		mValues[col*4+2] = z;
		mValues[col*4+3] = w;
	}

	public void setRow(int row, float x, float y, float z) {
		mValues[row] = x;
		mValues[4+row] = y;
		mValues[8+row] = z;
	}

	public void setRow(int row, float x, float y, float z, float w) {
		mValues[row] = x;
		mValues[4+row] = y;
		mValues[8+row] = z;
		mValues[12+row] = w;
	}

	public void setRow(int row, YangMatrix sourceMatrix,int sourceRow) {
		float[] sourceVals = sourceMatrix.mValues;
		mValues[row] = sourceVals[sourceRow];
		mValues[4+row] = sourceVals[4+sourceRow];
		mValues[8+row] = sourceVals[8+sourceRow];
		mValues[12+row] = sourceVals[12+sourceRow];
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

	public void setRowMajor3x3(double[][] matrix) {
		mValues[0] = (float)matrix[0][0];
		mValues[1] = (float)matrix[1][0];
		mValues[2] = (float)matrix[2][0];
		mValues[3] = 0;
		mValues[4] = (float)matrix[0][1];
		mValues[5] = (float)matrix[1][1];
		mValues[6] = (float)matrix[2][1];
		mValues[7] = 0;
		mValues[8] = (float)matrix[0][2];
		mValues[9] = (float)matrix[1][2];
		mValues[10] = (float)matrix[2][2];
		mValues[11] = 0;
		mValues[12] = 0;
		mValues[13] = 0;
		mValues[14] = 0;
		mValues[15] = 1;
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

	public void apply3DTransposed(Point3f point, Point3f target) {
		MatrixOps.applyFloatMatrix3DTransposed(mValues, point.mX,point.mY,point.mZ, target);
	}

	public void apply3D(Point3f target) {
		MatrixOps.applyFloatMatrix3D(mValues, target.mX,target.mY,target.mZ, target);
	}

	public void apply3DNormalized(float x, float y, float z, float[] target, int targetOffset) {
		MatrixOps.applyFloatMatrix3DNormalized(mValues,x,y,z,target,targetOffset);
	}

	public void apply3DNormalized(float x, float y, float z, Point3f target) {
		MatrixOps.applyFloatMatrix3DNormalized(mValues,x,y,z,target);
	}

	public void apply3DNormalized(Point3f source, Point3f target) {
		MatrixOps.applyFloatMatrix3DNormalized(mValues,source.mX,source.mY,source.mZ,target);
	}

	public void setColumn(int col, Vector3f values) {
		setColumn(col,values.mX,values.mY,values.mZ);
	}

	public void setRow(int row, Vector3f values) {
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

	public void setPointToDirection(Vector3f direction,Vector3f up) {
		MatrixOps.createDirectionTrafo(mValues, direction.mX,direction.mY,direction.mZ, up.mX,up.mY,up.mZ);
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

	public void setFromAxis(Vector3f vec1, Vector3f vec2, Vector3f vec3) {
		setBase4f(mValues,vec1,vec2,vec3);
	}

	public void setFromAxisNormalized(Vector3f vec1, Vector3f vec2, Vector3f vec3) {
		setBase4fNormalized(mValues,vec1,vec2,vec3);
	}

	public void setFromAxisAndPosition(Vector3f right,Vector3f up, Vector3f forward, Point3f position) {
		setBase4f(mValues,right,up,forward,position);
	}

	public void setFromAxis(Point3f basePoint, Point3f rightPoint, Point3f topPoint, Point3f frontPoint,boolean normalize) {
		float dx = rightPoint.mX-basePoint.mX;
		float dy = rightPoint.mY-basePoint.mY;
		float dz = rightPoint.mZ-basePoint.mZ;
		if(normalize) {
			float dist = (float)Math.sqrt(dx*dx+dy*dy+dz*dz);
			if(dist!=1) {
				dx /= dist;
				dy /= dist;
				dz /= dist;
			}
		}
		mValues[0] = dx;
		mValues[1] = dy;
		mValues[2] = dz;
		mValues[3] = 0;
		dx = topPoint.mX-basePoint.mX;
		dy = topPoint.mY-basePoint.mY;
		dz = topPoint.mZ-basePoint.mZ;
		if(normalize) {
			float dist = (float)Math.sqrt(dx*dx+dy*dy+dz*dz);
			if(dist!=1) {
				dx /= dist;
				dy /= dist;
				dz /= dist;
			}
		}
		mValues[4] = dx;
		mValues[5] = dy;
		mValues[6] = dz;
		mValues[7] = 0;
		dx = frontPoint.mX-basePoint.mX;
		dy = frontPoint.mY-basePoint.mY;
		dz = frontPoint.mZ-basePoint.mZ;
		if(normalize) {
			float dist = (float)Math.sqrt(dx*dx+dy*dy+dz*dz);
			if(dist!=1) {
				dx /= dist;
				dy /= dist;
				dz /= dist;
			}
		}
		mValues[8] = dx;
		mValues[9] = dy;
		mValues[10] = dz;
		mValues[11] = 0;

		mValues[12] = basePoint.mX;
		mValues[13] = basePoint.mY;
		mValues[14] = basePoint.mZ;
		mValues[15] = 1;
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

	public void setFromEulerAngles(EulerAngles angles) {
		setFromEulerAngles(angles.mYaw,angles.mPitch,angles.mRoll);
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

	public void swapLines(int line1, int line2) {
		int l1 = line1*4;
		int l2 = line2*4;
		float h = mValues[l1];
		mValues[l1] = mValues[l2];
		mValues[l2] = h;
		h = mValues[l1+1];
		mValues[l1+1] = mValues[l2+1];
		mValues[l2+1] = h;
		h = mValues[l1+2];
		mValues[l1+2] = mValues[l2+2];
		mValues[l2+2] = h;
		h = mValues[l1+3];
		mValues[l1+3] = mValues[l2+3];
		mValues[l2+3] = h;
	}

	public void getRightVector(Vector3f target) {
		target.mX = mValues[0];
		target.mY = mValues[1];
		target.mZ = mValues[2];
	}

	public void getUpVector(Vector3f target) {
		target.mX = mValues[4];
		target.mY = mValues[5];
		target.mZ = mValues[6];
	}

	public void getForwardVector(Vector3f target) {
		target.mX = mValues[8];
		target.mY = mValues[9];
		target.mZ = mValues[10];
	}

	public void applyInverted(Point3f position, Point3f target) {
		asInverted(mBackMatrix);
		MatrixOps.applyFloatMatrix3D(mBackMatrix, position, target);
	}

	public void clearTranslation() {
		mValues[12] = 0;
		mValues[13] = 0;
		mValues[14] = 0;
	}

	public void setNormalized(YangMatrix matrix) {
		float[] tempValues = matrix.mValues;
		float magnX = 1/(float)Math.sqrt(tempValues[0]*tempValues[0] + tempValues[1]*tempValues[1] + tempValues[2]*tempValues[2]);
		float magnY = 1/(float)Math.sqrt(tempValues[4]*tempValues[4] + tempValues[5]*tempValues[5] + tempValues[6]*tempValues[6]);
		float magnZ = 1/(float)Math.sqrt(tempValues[8]*tempValues[8] + tempValues[9]*tempValues[9] + tempValues[10]*tempValues[10]);
		this.mValues[0] = tempValues[0]*magnX;
		this.mValues[1] = tempValues[1]*magnX;
		this.mValues[2] = tempValues[2]*magnX;
		this.mValues[3] = 0;
		this.mValues[4] = tempValues[4]*magnY;
		this.mValues[5] = tempValues[5]*magnY;
		this.mValues[6] = tempValues[6]*magnY;
		this.mValues[7] = 0;
		this.mValues[8] = tempValues[8]*magnZ;
		this.mValues[9] = tempValues[9]*magnZ;
		this.mValues[10] = tempValues[10]*magnZ;
		this.mValues[11] = 0;
		this.mValues[12] = 0;
		this.mValues[13] = 0;
		this.mValues[14] = 0;
		this.mValues[15] = 1;

//		float[] values = matrix.mValues;
//		float magnX = 1/(float)Math.sqrt(values[0]*values[0] + values[4]*values[4] + values[8]*values[8]);
//		float magnY = 1/(float)Math.sqrt(values[1]*values[1] + values[5]*values[5] + values[9]*values[9]);
//		float magnZ = 1/(float)Math.sqrt(values[2]*values[2] + values[6]*values[6] + values[10]*values[10]);
//		this.mValues[0] = values[0]*magnX;
//		this.mValues[1] = values[1]*magnY;
//		this.mValues[2] = values[2]*magnZ;
//		this.mValues[3] = 0;
//		this.mValues[4] = values[4]*magnX;
//		this.mValues[5] = values[5]*magnY;
//		this.mValues[6] = values[6]*magnZ;
//		this.mValues[7] = 0;
//		this.mValues[8] = values[8]*magnX;
//		this.mValues[9] = values[9]*magnY;
//		this.mValues[10] = values[10]*magnZ;
//		this.mValues[11] = 0;
//		this.mValues[12] = 0;
//		this.mValues[13] = 0;
//		this.mValues[14] = 0;
//		this.mValues[15] = 1;
	}

	public void applyToPlane(float x,float y,float z, float nx,float ny,float nz, Point3f basePoint, Vector3f planeNormal) {
//		float bx = MatrixOps.applyFloatMatrixX3D(mValues, x,y,z);
//		float by = MatrixOps.applyFloatMatrixY3D(mValues, x,y,z);
//		float bz = MatrixOps.applyFloatMatrixZ3D(mValues, x,y,z);
//		float bnx = MatrixOps.applyFloatMatrixX3D(mValues, x,y,z);
//		float bny = MatrixOps.applyFloatMatrixY3D(mValues, x,y,z);
//		float bnz = MatrixOps.applyFloatMatrixZ3D(mValues, x,y,z);
		this.apply3D(x,y,z, basePoint);
		this.apply3D(x+nx, y+ny, y+nz, planeNormal);
		planeNormal.sub(basePoint);
	}

	public void normalizeScale() {
		float d = (float)Math.sqrt(mValues[0]*mValues[0] + mValues[1]*mValues[1] + mValues[2]*mValues[2]);
		if(d!=0) {
			mValues[0] /= d;
			mValues[1] /= d;
			mValues[2] /= d;
		}
		d = (float)Math.sqrt(mValues[4]*mValues[4] + mValues[5]*mValues[5] + mValues[6]*mValues[6]);
		if(d!=0) {
			mValues[4] /= d;
			mValues[5] /= d;
			mValues[6] /= d;
		}
		d = (float)Math.sqrt(mValues[8]*mValues[8] + mValues[9]*mValues[9] + mValues[10]*mValues[10]);
		if(d!=0) {
			mValues[8] /= d;
			mValues[9] /= d;
			mValues[10] /= d;
		}
	}

	public void normalizeHomogeneous() {
		float d = mValues[15];
		if(d!=0) {
			d = 1/d;
			for(int i=0;i<15;i++) {
				mValues[i] *= d;
			}
			mValues[15] = 1;
		}
	}

	public void fromToTransform(YangMatrix fromTransform, YangMatrix toTransform) {
		fromTransform.asInverted(mValues);
		this.multiplyLeft(toTransform);
	}

	public float getScaleX() {
		return (float)Math.sqrt(mValues[0]*mValues[0] + mValues[1]*mValues[1] + mValues[2]*mValues[2]);
	}

	public float getScaleY() {
		return (float)Math.sqrt(mValues[4]*mValues[4] + mValues[5]*mValues[5] + mValues[6]*mValues[6]);
	}

	public float getScaleZ() {
		return (float)Math.sqrt(mValues[8]*mValues[8] + mValues[9]*mValues[9] + mValues[10]*mValues[10]);
	}

	public void getScale(Vector3f target) {
		target.set(
				(float)Math.sqrt(mValues[0]*mValues[0] + mValues[1]*mValues[1] + mValues[2]*mValues[2]),
				(float)Math.sqrt(mValues[4]*mValues[4] + mValues[5]*mValues[5] + mValues[6]*mValues[6]),
				(float)Math.sqrt(mValues[8]*mValues[8] + mValues[9]*mValues[9] + mValues[10]*mValues[10])
				);
	}

	public void transpose() {
		asTransposed(mBackMatrix);
		mValues = mBackMatrix;
	}

	public void multiplyScalar(float scalar) {
		for(int i=0;i<16;i++) {
			mValues[i] *= scalar;
		}
	}

	public void clean(float threshold) {
		for(int i=0;i<16;i++) {
			if(Math.abs(mValues[i])<threshold)
				mValues[i] = 0;
			if(Math.abs(mValues[i]-1)<threshold)
				mValues[i] = 1;
		}
	}

	public void clean() {
		clean(0.000001f);
	}

	public void replaceTranslation(float x, float y, float z) {
		mValues[M03] = x;
		mValues[M13] = y;
		mValues[M23] = z;
	}

	public boolean isStackInitialized() {
		return mStack!=null;
	}

}
