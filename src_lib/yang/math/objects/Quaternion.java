package yang.math.objects;

import yang.math.MathConst;
import yang.math.objects.matrix.YangMatrix;

public class Quaternion {

	public static final Quaternion IDENTITY = new Quaternion(0,0,0,1);

	public float mX,mY,mZ,mW;

	public static void toRotationMatrix(float quatX,float quatY,float quatZ,float quatW,float[] target) {
		target[0] = 1 - 2*quatY*quatY - 2*quatZ*quatZ;
		target[1] = 2*quatX*quatY - 2*quatZ*quatW;
		target[2] = 2*quatX*quatZ + 2*quatY*quatW;
		target[3] = 0;
		target[4] = 2*quatX*quatY+ 2*quatZ*quatW;
		target[5] = 1 - 2*quatX*quatX - 2*quatZ*quatZ;
		target[6] = 2*quatY*quatZ - 2*quatX*quatW;
		target[7] = 0;
		target[8] = 2*quatX*quatZ - 2*quatY*quatW;
		target[9] = 2*quatY*quatZ + 2*quatX*quatW;
		target[10] = 1 - 2*quatX*quatX - 2*quatY*quatY;
		target[11] = 0;
		target[12] = 0;
		target[13] = 0;
		target[14] = 0;
		target[15] = 1;
	}

	public static void toRotationMatrix(float quatX,float quatY,float quatZ,float[] target) {
		toRotationMatrix(quatX,quatY,quatZ,(float)Math.sqrt(1-quatX*quatX-quatY*quatY-quatZ*quatZ),target);
	}

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

	public void setIdentity() {
		mX = 0;
		mY = 0;
		mZ = 0;
		mW = 1;
	}

	public void setFromEuler(float yaw,float pitch,float roll) {
		final float s1 = (float)Math.sin(yaw);
		final float c1 = (float)Math.cos(yaw);
		final float s2 = (float)Math.sin(roll);
		final float c2 = (float)Math.cos(roll);
		final float s3 = (float)Math.sin(pitch);
		final float c3 = (float)Math.cos(pitch);
		mW = c1*c2*c3 - s1*s2*s3;
		mX = s1*s2*c3 + c1*c2*s3;
		mY = s1*c2*c3 + c1*s2*s3;
		mZ = c1*s2*c3 - s1*c2*s3;
	}

	public void setFromAxis(Vector3f axis,float angle) {
		final float sinA = (float)Math.sin(angle*0.5f);
		final float cosA = (float)Math.cos(angle*0.5f);
		mX = axis.mX * sinA;
		mY = axis.mY * sinA;
		mZ = axis.mZ * sinA;
		mW = cosA;
	}

	public void setFromAxis(float x,float y,float z,float angle) {
		final float sinA = (float)Math.sin(angle*0.5f);
		final float cosA = (float)Math.cos(angle*0.5f);
		mX = x * sinA;
		mY = y * sinA;
		mZ = z * sinA;
		mW = cosA;
	}

	/**
	 * Matrix must be orthogonal with no flipping
	 */
	public void setFromTransform(float m00,float m10,float m20, float m01,float m11,float m21, float m02,float m12,float m22) {

		final float diag = m00 + m11 + m22;

		if (diag > 0) {
			mW = (float)Math.sqrt(1.0f + diag) * 0.5f;
			final float w4 = 1/(4.0f * mW);
			mX = (m21 - m12) * w4;
			mY = (m02 - m20) * w4;
			mZ = (m10 - m01) * w4;
		} else if ((m00 > m11)&(m00 > m22)) {
		  final float S = (float)Math.sqrt(1.0 + m00 - m11 - m22) * 2; // S=4*qx
		  mW = (m21 - m12) / S;
		  mX = 0.25f * S;
		  mY = (m01 + m10) / S;
		  mZ = (m02 + m20) / S;
		} else if (m11 > m22) {
		  final float S = (float)Math.sqrt(1.0 + m11 - m00 - m22) * 2; // S=4*qy
		  mW = (m02 - m20) / S;
		  mX = (m01 + m10) / S;
		  mY = 0.25f * S;
		  mZ = (m12 + m21) / S;
		} else {
		  final float S = (float)Math.sqrt(1.0 + m22 - m00 - m11) * 2; // S=4*qz
		  mW = (m10 - m01) / S;
		  mX = (m02 + m20) / S;
		  mY = (m12 + m21) / S;
		  mZ = 0.25f * S;
		}
	}

	/**
	 * Axes must form orthogonal basis with no flipping
	 */
	public void setFromAxes(Vector3f right,Vector3f up,Vector3f forward) {
		setFromTransform(right.mX,right.mY,right.mZ, up.mX,up.mY,up.mZ, forward.mX,forward.mY,forward.mZ);
	}

	/**
	 * Matrix must be orthogonal with no flipping
	 */
	public void setFromMatrix(float[] matrix) {
		setFromTransform(matrix[YangMatrix.M00],matrix[YangMatrix.M10],matrix[YangMatrix.M20], matrix[YangMatrix.M01],matrix[YangMatrix.M11],matrix[YangMatrix.M21], matrix[YangMatrix.M02],matrix[YangMatrix.M12],matrix[YangMatrix.M22]);
	}

	public void normalize() {
		final float magn = 1/(float)Math.sqrt(mX*mX + mY*mY + mZ*mZ + mW*mW);
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
		final float x = mW*vector.mX + mY*vector.mZ - mZ*vector.mY;
		final float y = mW*vector.mY - mX*vector.mZ + mZ*vector.mX;
		final float z = mW*vector.mZ + mX*vector.mY - mY*vector.mX;
		final float w = -mX*vector.mX - mY*vector.mY - mZ*vector.mZ;
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

	public void multRight(Quaternion rhq) {
		final float x = mX;
		final float y = mY;
		final float z = mZ;
		mX = mW*rhq.mX + x*rhq.mW + y*rhq.mZ - z*rhq.mY;
		mY = mW*rhq.mY - x*rhq.mZ + y*rhq.mW + z*rhq.mX;
		mZ = mW*rhq.mZ + x*rhq.mY - y*rhq.mX + z*rhq.mW;
		mW = mW*rhq.mW - x*rhq.mX - y*rhq.mY - z*rhq.mZ;
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

	public void setLerp(Quaternion quat1,Quaternion quat2,float alpha) {
		mX = quat1.mX*(1-alpha) + quat2.mX*alpha;
		mY = quat1.mY*(1-alpha) + quat2.mY*alpha;
		mZ = quat1.mZ*(1-alpha) + quat2.mZ*alpha;
		mW = quat1.mW*(1-alpha) + quat2.mW*alpha;
		float magn = (float)Math.sqrt(mX*mX + mY*mY + mZ*mZ + mW*mW);
		if(magn>0) {
			magn = 1/magn;
			mX *= magn;
			mY *= magn;
			mZ *= magn;
			mW *= magn;
		}
	}

	public void lerp(Quaternion otherQuaternion,float alpha) {
		setLerp(this,otherQuaternion,alpha);
	}

	public void setSlerp(Quaternion quat1,Quaternion quat2,float alpha) {
		final float dot = quat1.mX*quat2.mX + quat1.mY*quat2.mY + quat1.mZ*quat2.mZ + quat1.mW*quat2.mW;
		if(dot>=0.9999999f)
			return;
		float theta = (float)Math.acos(dot);
		if(theta>=MathConst.PI-0.00001f) {
			theta = MathConst.PI-0.00001f;
		}
		final float dSin = 1/(float)Math.sin(theta);
		final float w1 = (float)Math.sin((1-alpha)*theta)*dSin;
		final float w2 = (float)Math.sin(alpha*theta)*dSin;
		mX = w1*quat1.mX + w2*quat2.mX;
		mY = w1*quat1.mY + w2*quat2.mY;
		mZ = w1*quat1.mZ + w2*quat2.mZ;
		mW = w1*quat1.mW + w2*quat2.mW;
	}

	public void slerp(Quaternion otherQuaternion,float alpha) {
		setSlerp(this,otherQuaternion,alpha);
	}

	/**
	 * Vectors must be normalized
	 */
	public void setFromToRotation(Vector3f fromVector,Vector3f toVector) {
		final float dot = fromVector.mX*toVector.mX + fromVector.mY*toVector.mY + fromVector.mZ*toVector.mZ;
		if(dot<=-0.99999) {
			mX = 1;
			mY = 0;
			mZ = 0;
			mW = 0;
		}else{
			final float crossX = toVector.mY*fromVector.mZ - toVector.mZ*fromVector.mY;
			final float crossY = toVector.mZ*fromVector.mX - toVector.mX*fromVector.mZ;
			final float crossZ = toVector.mX*fromVector.mY - toVector.mY*fromVector.mX;
			mX = crossX * 0.5f;
			mY = crossY * 0.5f;
			mZ = crossZ * 0.5f;
			mW = dot * 0.5f + 0.5f;
			float magn = (float)Math.sqrt(mX*mX + mY*mY + mZ*mZ + mW*mW);
			if(magn!=0) {
				magn = 1/magn;
				mX *= magn;
				mY *= magn;
				mZ *= magn;
				mW *= magn;
			}
		}
	}

	public void setFromToRotation(float fromX,float fromY,float fromZ, float toX,float toY,float toZ) {
		final float dot = fromX*toX + fromY*toY + fromZ*toZ;
		if(dot<=-0.99999) {
			mX = 1;
			mY = 0;
			mZ = 0;
			mW = 0;
		}else{
			final float crossX = toY*fromZ - toZ*fromY;
			final float crossY = toZ*fromX - toX*fromZ;
			final float crossZ = toX*fromY - toY*fromX;
			mX = crossX * 0.5f;
			mY = crossY * 0.5f;
			mZ = crossZ * 0.5f;
			mW = dot * 0.5f + 0.5f;
			float magn = (float)Math.sqrt(mX*mX + mY*mY + mZ*mZ + mW*mW);
			if(magn!=0) {
				magn = 1/magn;
				mX *= magn;
				mY *= magn;
				mZ *= magn;
				mW *= magn;
			}
		}
	}

	@Override
	public Quaternion clone() {
		return new Quaternion(mX,mY,mZ,mW);
	}

	@Override
	public String toString() {
		return "("+mX+","+mY+","+mZ+", "+mW+")";
	}

	public void setZero() {
		mX = 0;
		mY = 0;
		mZ = 0;
		mW = 1;
	}

}
