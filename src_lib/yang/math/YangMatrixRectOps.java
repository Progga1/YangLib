package yang.math;

public class YangMatrixRectOps extends YangMatrix {

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
	
	
	public void applyToRect2D(float[] target) {
		float[] matrix = mMatrix;
		MatrixOps.applyFloatMatrix2D(matrix,0,0,target,0);
		MatrixOps.applyFloatMatrix2D(matrix,1,0,target,2);
		MatrixOps.applyFloatMatrix2D(matrix,0,1,target,4);
		MatrixOps.applyFloatMatrix2D(matrix,1,1,target,6);
	}
	
	public void applyToRect2DInvertY(float[] target) {
		float[] matrix = mMatrix;
		MatrixOps.applyFloatMatrix2D(matrix,0,1,target,0);
		MatrixOps.applyFloatMatrix2D(matrix,1,1,target,2);
		MatrixOps.applyFloatMatrix2D(matrix,0,0,target,4);
		MatrixOps.applyFloatMatrix2D(matrix,1,0,target,6);
	}
	
	public void applyToRect3D(float[] target) {
		float[] matrix = mMatrix;
		MatrixOps.applyFloatMatrix3D(matrix,0,0,0,target,0);
		MatrixOps.applyFloatMatrix3D(matrix,1,0,0,target,3);
		MatrixOps.applyFloatMatrix3D(matrix,0,1,0,target,6);
		MatrixOps.applyFloatMatrix3D(matrix,1,1,0,target,9);
	}
	
}
