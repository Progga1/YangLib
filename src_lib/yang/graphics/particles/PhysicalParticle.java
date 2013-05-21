package yang.graphics.particles;

import yang.graphics.YangSurface;
import yang.math.Geometry;
import yang.math.MathConst;
import yang.math.MathFunc;

public class PhysicalParticle extends Particle {

	public boolean mRotationByVelo = false;
	public float mFriction;
	public float mVelX,mVelY,mVelZ;
	public float mAccelerationX,mAccelerationY,mAccelerationZ;
	
	public PhysicalParticle() {
		mAccelerationX = 0;
		mAccelerationY = 0;
		mAccelerationZ = 0;
		mFriction = 1;
	}
	
	@Override
	public void derivedStep() {
		if(mRotationByVelo) {
			mRotation = Geometry.getAngle(mVelX,mVelY) + MathConst.PI*0.5f;
		}
		mPosX += mVelX;
	    mPosY += mVelY;
	    mPosZ += mVelZ;
	    mVelX += mAccelerationX;
	    mVelY += mAccelerationY;
	    mVelZ += mAccelerationZ;
	    if(mFriction!=1) {
		    mVelX *= mFriction;
		    mVelY *= mFriction;
		    mVelZ *= mFriction;
	    }
	}
	
	public void setVelocity(float velX, float velY, float velZ) {
		mVelX = velX*YangSurface.deltaTimeSeconds;
		mVelY = velY*YangSurface.deltaTimeSeconds;
		mVelZ = velZ*YangSurface.deltaTimeSeconds;
	}
	
	public void setVelocity(float velX, float velY) {
		mVelX = velX*YangSurface.deltaTimeSeconds;;
		mVelY = velY*YangSurface.deltaTimeSeconds;;
	}
	
	public void setAcceleration(float accX, float accY,float accZ) {
		mAccelerationX = accX*YangSurface.deltaTimeSeconds;
		mAccelerationY = accY*YangSurface.deltaTimeSeconds;
		mAccelerationZ = accZ*YangSurface.deltaTimeSeconds;
	}
	
	public void setAcceleration(float accX, float accY) {
		mAccelerationX = accX*YangSurface.deltaTimeSeconds;;
		mAccelerationY = accY*YangSurface.deltaTimeSeconds;;
	}
	
	public float setSpeedRange2D(float minSpeed, float maxSpeed, float minAngle, float maxAngle) {
		float a = MathFunc.random(minAngle, maxAngle);
		float v = MathFunc.random(minSpeed, maxSpeed);
		mVelX = (float)(Math.cos(a)*v)*YangSurface.deltaTimeSeconds;;
		mVelY = (float)(Math.sin(a)*v)*YangSurface.deltaTimeSeconds;;
		return a;
	}
	
	public float setSpeedRangeSpread2D(float minSpeed, float maxSpeed, float direction, float spreadAngle) {
		return setSpeedRange2D(minSpeed,maxSpeed, direction-spreadAngle*0.5f, direction+spreadAngle*0.5f);
	}
	
	public float setSpeedRange2D(float minSpeed, float maxSpeed) {
		return setSpeedRange2D(minSpeed,maxSpeed,0,2*MathConst.PI);
	}
	
}
