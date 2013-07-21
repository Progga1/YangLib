package yang.math.objects;

public class Vector3f {

	public final static Vector3f ZERO = new Vector3f(0,0,0);
	public final static Vector3f RIGHT = new Vector3f(1,0,0);
	public final static Vector3f LEFT = new Vector3f(-1,0,0);
	public final static Vector3f UP = new Vector3f(0,1,0);
	public final static Vector3f DOWN = new Vector3f(0,-1,0);
	public final static Vector3f FORWARD = new Vector3f(0,0,1);
	public final static Vector3f BACKWARD = new Vector3f(0,0,-1);
	public final static Vector3f ONE = new Vector3f(1,1,1);
	
	public float mX,mY,mZ;
	
	public Vector3f() {
		set(0,0,0);
	}
	
	public Vector3f(float x,float y,float z) {
		set(x,y,z);
	}
	
	public float magn() {
		return (float)Math.sqrt(mX*mX + mY*mY + mZ*mZ);
	}
	
	public float sqrMagn() {
		return mX*mX + mY*mY + mZ*mZ;
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
	
	public void scale(float scalar) {
		mX *= scalar;
		mY *= scalar;
		mZ *= scalar;
	}
	
	public void normalize() {
		float d = (float)(Math.sqrt(mX*mX+mY*mY+mZ*mZ));
		if(d!=0) {
			mX /= d;
			mY /= d;
			mZ /= d;
		}
	}
	
	public float dot(Vector3f vector) {
		return mX*vector.mX + mY*vector.mY + mZ*vector.mZ;
	}
	
	public float dot(float x,float y,float z) {
		return mX*x + mY*y + mZ*z;
	}
	
	public void cross(Vector3f lhsVector,Vector3f rhsVector) {
		mX = lhsVector.mY*rhsVector.mZ - lhsVector.mZ*rhsVector.mY;
		mY = lhsVector.mZ*rhsVector.mX - lhsVector.mX*rhsVector.mZ;
		mZ = lhsVector.mX*rhsVector.mY - lhsVector.mY*rhsVector.mX;
	}
	
	public void cross(float lhsX,float lhsY,float lhsZ, float rhsX,float rhsY,float rhsZ) {
		mX = lhsY*rhsZ - lhsZ*rhsY;
		mY = lhsZ*rhsX - lhsX*rhsZ;
		mZ = lhsX*rhsY - lhsY*rhsX;
	}
	
	@Override
	public Vector3f clone() {
		return new Vector3f(mX,mY,mZ);
	}
	
}
