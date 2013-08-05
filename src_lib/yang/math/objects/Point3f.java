package yang.math.objects;

public class Point3f {

	public final static Point3f ZERO = new Point3f(0,0,0);
	public final static Point3f ONE = new Point3f(1,1,1);
	
	public float mX,mY,mZ;
	
	public Point3f() {
		set(0,0,0);
	}
	
	public Point3f(float x,float y,float z) {
		set(x,y,z);
	}
	
	public void set(float x,float y,float z) {
		mX = x;
		mY = y;
		mZ = z;
	}
	
	public void set(float[] array) {
		mX = array[0];
		mY = array[1];
		mZ = array[2];
	}
	
	public void set(Point3f point) {
		mX = point.mX;
		mY = point.mY;
		mZ = point.mZ;
	}
	
	public void add(float x,float y,float z) {
		mX += x;
		mY += y;
		mZ += z;
	}
	
	public void add(Vector3f vector) {
		mX += vector.mX;
		mY += vector.mY;
		mZ += vector.mZ;
	}
	
	public void sub(float x,float y,float z) {
		mX -= x;
		mY -= y;
		mZ -= z;
	}
	
	public void sub(Vector3f vector) {
		mX -= vector.mX;
		mY -= vector.mY;
		mZ -= vector.mZ;
	}
	
	@Override
	public Point3f clone() {
		return new Point3f(mX,mY,mZ);
	}

	public void setAlphaBeta(float alpha, float beta, float distance, float focusX,float focusY,float focusZ) {
		mX = focusX+(float)(Math.sin(alpha)*Math.cos(beta)) * distance;
		mY = focusY+(float)(Math.sin(beta)) * distance;
		mZ = focusZ+(float)(Math.cos(alpha)*Math.cos(beta)) * distance;
	}
	
	public void setAlphaBeta(float alpha, float beta, float distance) {
		setAlphaBeta(alpha,beta,distance,0,0,0);
	}
	
	public void setAlphaBeta(float alpha, float beta) {
		setAlphaBeta(alpha,beta,1,0,0,0);
	}
	
	@Override
	public String toString() {
		return "("+mX+","+mY+","+mZ+")";
	}

	public float getDistance(float[] coordinates) {
		float dX = mX-coordinates[0];
		float dY = mY-coordinates[1];
		float dZ = mZ-coordinates[2];
		return (float)Math.sqrt(dX*dX+dY*dY+dZ*dZ);
	}

	public float getDistance(Point3f point) {
		float dX = mX-point.mX;
		float dY = mY-point.mY;
		float dZ = mZ-point.mZ;
		return (float)Math.sqrt(dX*dX+dY*dY+dZ*dZ);
	}

	public void lerp(Point3f target, float weight) {
		mX += (target.mX-mX)*weight;
		mY += (target.mY-mY)*weight;
		mZ += (target.mZ-mZ)*weight;
	}
	
}
