package yang.math.objects;

public class Quaternion {

	float mX,mY,mZ,mW;
	
	public Quaternion() {
		
	}
	
	public Quaternion(float x,float y,float z,float w) {
		this();
		set(x,y,z,w);
	}
	
	public Quaternion(Vector3f axis,float angle) {
		this();
		set(axis,angle);
	}
	
	public void set(float x,float y,float z,float w) {
		mX = x;
		mY = y;
		mZ = z;
		mW = w;
	}
	
	public void set(Vector3f axis,float angle) {
		float sinA = (float)Math.sin(angle*0.5f);
		float cosA = (float)Math.cos(angle*0.5f);
		mX = axis.mX * sinA;
		mY = axis.mY * sinA;
		mZ = axis.mZ * sinA;
		mW = cosA;
	}
	
	public void normalize() {
		float magn = 1/(float)Math.sqrt(mX*mX + mY*mY + mZ*mZ + mW*mW);
		mX *= magn;
		mY *= magn;
		mZ *= magn;
		mW *= magn;
	}
	
	public float calcMagnitude() {
		return (float)Math.sqrt(mX*mX + mY*mY + mZ*mZ + mW*mW);
	}
	
	public void conjugate() {
		mX = -mX;
		mY = -mY;
		mZ = -mZ;
	}
	
	public void invert() {
		float magn = calcMagnitude();
		magn = 1/(magn*magn);
		mX *= -magn;
		mY *= -magn;
		mZ *= -magn;
		mW *= magn;
	}
	
	public void scale(float scalar) {
		mX *= scalar;
		mY *= scalar;
		mZ *= scalar;
		mW *= scalar;
	}
	
	public void apply(Vector3f axis) {
		
	}
	
	public void multiplyRight(Quaternion lhq,Quaternion rhq) {
		
	}
	
	public void toRotationMatrix(float[] target) {
		target[0] = 1 - 2*mY*mY - 2*mZ*mZ;
		target[1] = 2*mX*mY - 2*mZ*mW;
		target[2] = 2*mX*mZ + 2*mY*mW;
		target[3] = 0;
		target[4] = 2*mX*mY+ 2*mZ*mW;
		target[5] = 1 - 2*mX*mX - 2*mZ*mZ;
		target[6] = 2*mY*mZ - 2*mX*mW;
		target[7] = 0;
		target[8] = 2*mX*mZ - 2*mY*mW;
		target[9] = 2*mY*mZ + 2*mX*mW;
		target[10] = 1 - 2*mX*mX - 2*mY*mY;
		target[11] = 0;
		target[12] = 0;
		target[13] = 0;
		target[14] = 0;
		target[15] = 1;
	}
	
	@Override
	public Quaternion clone() {
		return new Quaternion(mX,mY,mZ,mW);
	}
	
}
