package yang.math;

import javax.vecmath.Vector3f;


public abstract class TransformationMatrix {

	public static float DEFAULT_NEAR = 1;
	public static float DEFAULT_FAR = -1;
	
	public static final float[] FLOAT_IDENTITY = 
		{
			1,0,0,0,
			0,1,0,0,
			0,0,1,0,
			0,0,0,1
		};
	
	protected float[] mIntermediate;
	protected float[] inverted;
	public static float TO_RAD_FACTOR = (float) Math.PI / 180;
	public static float TO_DEG_FACTOR = 180 / (float) Math.PI;
	
	public static final void copyMatrix(float[] dest, float[] src) {
		System.arraycopy(src, 0, dest, 0, 16);
	}

	public static final float[] copyMatrix(float[] src) {
		float[] result = new float[16];
		System.arraycopy(src, 0, result, 0, 16);
		return result;
	}

	/**
	 * Does not have to be a deep copy
	 */
	public abstract float[] asFloatArraySwallow();
	
	public abstract void set(TransformationMatrix src);

	public abstract boolean asInverted(float[] target);

	public abstract void loadIdentity();

	public abstract void translate(float x, float y, float z);

	public abstract void scale(float x, float y, float z);

	public abstract void rotate(float angle, float weightX, float weightY, float weightZ);

	public abstract void multiplyRight(float[] rhsMatrix);

	public abstract void multiplyLeft(float[] lhsMatrix);

	public abstract void multiply(float[] lhsMatrix,float[] rhsMatrix);
	
	public abstract void multiply(TransformationMatrix lhsMatrix, TransformationMatrix rhsMatrix);
	
	public abstract float get(int row, int column);
	
	public abstract float get(int index);
	
	public abstract void set(int row,int column, float value);
	
	public abstract void setRowMajor(double[][] matrix);
	
	public abstract void setColumnMajor(double[][] matrix);
	
	public abstract void setColumn(int col, float x,float y,float z,float w);

	public abstract void setRow(int row, float x,float y,float z,float w);
	
	public TransformationMatrix() {
		mIntermediate = new float[16];
		inverted = new float[16];
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
	
	public void setColumn(int col, float x,float y,float z) {
		setColumn(col,x,y,z,get(3,col));
	}
	
	public void setRow(int row, float x,float y,float z) {
		setRow(row,x,y,z,get(row,3));
	}

	public void multiplyRight(TransformationMatrix rhsMatrix) {
		multiplyRight(rhsMatrix.asFloatArraySwallow());
	}

	public void multiplyLeft(TransformationMatrix lhsMatrix) {
		multiplyLeft(lhsMatrix.asFloatArraySwallow());
	}

	public void translate(float x, float y) {
		translate(x, y, 0);
	}

	public float[] asFloatArrayDeep() {
		return copyMatrix(asFloatArraySwallow());
	}

	public void rotateX(float angle) {
		rotate(angle, 1, 0, 0);
	}

	public void rotateY(float angle) {
		rotate(angle, 0, 1, 0);
	}

	public void rotateZ(float angle) {
		rotate(angle, 0, 0, 1);
	}

	public void scale(float x, float y) {
		scale(x, y, 1);
	}

	public void scale(float s) {
		scale(s, s, s);
	}
	
	/**
	 * Slow! Call once in initialization!
	 */
	public void setRectBias(float x1, float y1, float x2, float y2, float z,float biasX, float biasY) {
		loadIdentity();
		translate(x1-biasX, y1-biasY, z);
		scale((x2+biasX - x1), (y2+biasY - y1));
	}
	
	/**
	 * Slow! Call once in initialization!
	 */
	public void setRect(float x1, float y1, float x2, float y2, float z) {
		setRectBias(x1,y1,x2,y2, z, 0,0);
	}
	
	/**
	 * Slow! Call once in initialization!
	 */
	public void setRectBias(float x1, float y1, float x2, float y2,float biasX,float biasY) {
		setRectBias(x1,y1,x2,y2,0, biasX,biasY);
	}
	
	/**
	 * Slow! Call once in initialization!
	 */
	public void setRect(float x1, float y1, float x2, float y2) {
		setRect(x1,y1,x2,y2,0);
	}

	/**
	 * Slow! Call once in initialization!
	 */
	public void setCenteredRectBias(float centerX, float centerY, float scaleX, float scaleY, float angle, float biasX, float biasY) {
		loadIdentity();
		translate(centerX, centerY);
		if (angle != 0)
			rotateZ(angle);
		scale(scaleX, scaleY);
		biasX += 1;
		biasY += 1;
		translate(-0.5f*biasX, -0.5f*biasY);
	}
	
	public void setCenteredRect(float centerX, float centerY, float scaleX, float scaleY, float angle) {
		setCenteredRectBias(centerX,centerY,scaleX,scaleY,angle, 0,0);
	}

	public void setLine(float fromX, float fromY, float toX, float toY, float width) {
		loadIdentity();
		float angle;
		float dx = toX - fromX;
		float dy = toY - fromY;
		float r = (float) Math.sqrt(dx * dx + dy * dy);
		if (r == 0) {
			scale(0);
			return;
		}
		if (dy < 0)
			angle = -(float) Math.acos(dx / r);
		else
			angle = (float) Math.acos(dx / r);
		angle += (float) Math.PI * 0.5f;

		translate(fromX, fromY);
		rotateZ(angle);
		scale(width, r);
		translate(-0.5f, -1);
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

	public static final void applyFloatMatrix2D(float[] matrix, float x, float y, float[] target, int targetOffset) {
		target[targetOffset] = applyFloatMatrixX2D(matrix,x,y);
		target[targetOffset+1] = applyFloatMatrixY2D(matrix,x,y);
	}
	
	public static final void applyFloatMatrix3D(float[] matrix, float x, float y, float z, float[] target, int targetOffset) {
		target[targetOffset] = matrix[0] * x + matrix[4] * y + matrix[8] * z + matrix[12];
		target[targetOffset+1] = matrix[1] * x + matrix[5] * y + matrix[9] * z + matrix[13];
		target[targetOffset+2] = matrix[2] * x + matrix[6] * y + matrix[10] * z + matrix[14];
		if(target.length>3) {
			target[targetOffset+3] = applyFloatMatrixW3D(matrix,x,y,z);
		}
	}
	
	public static final void applyFloatMatrix3DNormalized(float[] matrix, float x, float y, float z, float[] target, int targetOffset) {
		float w = 1f/applyFloatMatrixW3D(matrix,x,y,z);
		target[targetOffset] = (matrix[0] * x + matrix[4] * y + matrix[8] * z + matrix[12])*w;
		target[targetOffset+1] = (matrix[1] * x + matrix[5] * y + matrix[9] * z + matrix[13])*w;
		target[targetOffset+2] = (matrix[2] * x + matrix[6] * y + matrix[10] * z + matrix[14])*w;	
	}

	public float apply2DX(float x, float y) {
		return applyFloatMatrixX2D(asFloatArraySwallow(),x,y);
	}

	public float apply2DY(float x, float y) {
		return applyFloatMatrixY2D(asFloatArraySwallow(),x,y);
	}

	public void apply2D(float x, float y, float[] target, int targetOffset) {
		applyFloatMatrix2D(asFloatArraySwallow(),x,y,target,targetOffset);
	}
	
	public void apply3D(float x, float y, float z, float[] target, int targetOffset) {
		applyFloatMatrix3D(asFloatArraySwallow(),x,y,z,target,targetOffset);
	}
	
	public void apply3DNormalized(float x, float y, float z, float[] target, int targetOffset) {
		applyFloatMatrix3DNormalized(asFloatArraySwallow(),x,y,z,target,targetOffset);
	}

	@Override
	public String toString() {
		return matToString(asFloatArraySwallow());
	}
	
	public void applyToRect2D(float[] target) {
		float[] matrix = asFloatArraySwallow();
		applyFloatMatrix2D(matrix,0,0,target,0);
		applyFloatMatrix2D(matrix,1,0,target,2);
		applyFloatMatrix2D(matrix,0,1,target,4);
		applyFloatMatrix2D(matrix,1,1,target,6);
	}
	
	public void applyToRect2DInvertY(float[] target) {
		float[] matrix = asFloatArraySwallow();
		applyFloatMatrix2D(matrix,0,1,target,0);
		applyFloatMatrix2D(matrix,1,1,target,2);
		applyFloatMatrix2D(matrix,0,0,target,4);
		applyFloatMatrix2D(matrix,1,0,target,6);
	}
	
	public void applyToRect3D(float[] target) {
		float[] matrix = asFloatArraySwallow();
		applyFloatMatrix3D(matrix,0,0,0,target,0);
		applyFloatMatrix3D(matrix,1,0,0,target,3);
		applyFloatMatrix3D(matrix,0,1,0,target,6);
		applyFloatMatrix3D(matrix,1,1,0,target,9);
	}
	
	private Vector3f mVec0 = new Vector3f();
	private Vector3f mVec1 = new Vector3f();
	private Vector3f mVec2 = new Vector3f();
	private Vector3f mVec3 = new Vector3f();
	private Vector3f mVec4 = new Vector3f();
	
	protected void setColumn(int col, Vector3f values) {
		setColumn(col,values.x,values.y,values.z);
	}
	
	protected void setRow(int row, Vector3f values) {
		setRow(row,values.x,values.y,values.z);
	}

	public void setLookAt(float eyeX, float eyeY, float eyeZ, float lookAtX, float lookAtY, float lookAtZ, float upX, float upY, float upZ) {
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
		float[] matrix = this.asFloatArraySwallow();
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
	
}
