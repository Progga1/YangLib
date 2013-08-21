package yang.math.objects;

public class Quaternion {

	float mX,mY,mZ,mW;
	
	public Quaternion() {
		set(0,0,0,1);
	}
	
	public Quaternion(float x,float y,float z,float w) {
		set(x,y,z,w);
	}
	
	public Quaternion(Vector3f axis,float angle) {
		setFromAxis(axis,angle);
	}
	
	public void set(float x,float y,float z,float w) {
		mX = x;
		mY = y;
		mZ = z;
		mW = w;
	}
	
	public void set(Quaternion quaternion) {
		mX = quaternion.mX;
		mY = quaternion.mY;
		mZ = quaternion.mZ;
		mW = quaternion.mW;
	}
	
	public void setFromAxis(Vector3f axis,float angle) {
		float sinA = (float)Math.sin(angle*0.5f);
		float cosA = (float)Math.cos(angle*0.5f);
		mX = axis.mX * sinA;
		mY = axis.mY * sinA;
		mZ = axis.mZ * sinA;
		mW = cosA;
	}
	
	public void setFromAxis(float x,float y,float z,float angle) {
		float sinA = (float)Math.sin(angle*0.5f);
		float cosA = (float)Math.cos(angle*0.5f);
		mX = x * sinA;
		mY = y * sinA;
		mZ = z * sinA;
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
	
	public void applyToVector(Vector3f target, Vector3f vector) {
		float x = mW*vector.mX + mY*vector.mZ - mZ*vector.mY;
		float y = mW*vector.mY - mX*vector.mZ + mZ*vector.mX;
		float z = mW*vector.mZ + mX*vector.mY - mY*vector.mX;
		float w = -mX*vector.mX - mY*vector.mY - mZ*vector.mZ;
		target.mX = -w*mX + x*mW - y*mZ + z*mY;
		target.mY = -w*mY + x*mZ + y*mW - z*mX;
		target.mZ = -w*mZ - x*mY + y*mX + z*mW;

//		Quaternion vQuat = new Quaternion(vector.mX,vector.mY,vector.mZ,0);
//		Quaternion quat2 = new Quaternion();
//		Quaternion quat3 = new Quaternion();
//		//quat2.setConcat(this, vQuat);
//		quat2.set(x,y,z,w);
//		vQuat.set(this);
//		vQuat.conjugate();
//		quat3.setConcat(quat2, vQuat);
//		target.set(quat3.mX,quat3.mY,quat3.mZ);
	}
	
	public void setConcat(Quaternion lhq,Quaternion rhq) {
		mX = lhq.mW*rhq.mX + lhq.mX*rhq.mW + lhq.mY*rhq.mZ - lhq.mZ*rhq.mY;
		mY = lhq.mW*rhq.mY - lhq.mX*rhq.mZ + lhq.mY*rhq.mW + lhq.mZ*rhq.mX;
		mZ = lhq.mW*rhq.mZ + lhq.mX*rhq.mY - lhq.mY*rhq.mX + lhq.mZ*rhq.mW;
		mW = lhq.mW*rhq.mW - lhq.mX*rhq.mX - lhq.mY*rhq.mY - lhq.mZ*rhq.mZ;
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
	
	@Override
	public String toString() {
		return "("+mX+","+mY+","+mZ+", "+mW+")";
	}
	
}
