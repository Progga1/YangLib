package yang.math.objects;

public class Vector3f extends Point3f{

	public final static Vector3f RIGHT = new Vector3f(1,0,0);
	public final static Vector3f LEFT = new Vector3f(-1,0,0);
	public final static Vector3f UP = new Vector3f(0,1,0);
	public final static Vector3f DOWN = new Vector3f(0,-1,0);
	public final static Vector3f FORWARD = new Vector3f(0,0,1);
	public final static Vector3f BACKWARD = new Vector3f(0,0,-1);
	
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
	
	public void setNormalized(float x,float y,float z) {
		float dist = (float)Math.sqrt(x*x+y*y+z*z);
		if(dist==0) {
			mX = 0;
			mY = 0;
			mZ = 0;
		}else{
			dist = 1/dist;
			mX = x*dist;
			mY = y*dist;
			mZ = z*dist;
		}
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

	@Override
	public void setAlphaBeta(float alpha, float beta, float distance) {
		mX = (float)(Math.sin(alpha)*Math.cos(beta)) * distance;
		mY = (float)(Math.sin(beta)) * distance;
		mZ = (float)(Math.cos(alpha)*Math.cos(beta)) * distance;
	}

	public void createOrthoVec(Vector3f ortho) {
		if(ortho.mX<=ortho.mY && ortho.mX<=ortho.mZ) {
			setNormalized(0,-ortho.mZ,ortho.mY);
		}else if(ortho.mY<=ortho.mX && ortho.mY<=ortho.mZ) {
			setNormalized(-ortho.mZ,0,ortho.mX);
		}else{
			setNormalized(-ortho.mY,ortho.mX,0);
		}
	}
	
}
