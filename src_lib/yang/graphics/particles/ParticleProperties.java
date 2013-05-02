package yang.graphics.particles;

import yang.graphics.textures.TextureCoordinatesQuad;
import yang.util.Util;

public class ParticleProperties {

	public float mFriction;
	public TextureCoordinatesQuad mTextureCoordinates;
	public float mMinScale,mMaxScale;
	public float mMinSpeed,mMaxSpeed;
	public float mMinRotationV,mMaxRotationV;
	public boolean mMirrorRotation;
	public float mVelDirX,mVelDirY,mVelDirZ;
	
	public ParticleProperties(float minScale,float maxScale,float minSpeed,float maxSpeed,float minRotationSpeed,float maxRotationSpeed) {
		mFriction = 1;
		mTextureCoordinates = TextureCoordinatesQuad.FULL_TEXTURE;
		mMirrorRotation = true;
		setScale(minScale,maxScale);
		setSpeed(minSpeed,maxSpeed);
		setRotationSpeed(minRotationSpeed,maxRotationSpeed);
	}
	
	public ParticleProperties(float scale,float speed,float rotationSpeed) {
		this(scale,scale,speed,speed,rotationSpeed,rotationSpeed);
	}
	
	public ParticleProperties() {
		this(1,0.01f,0);
	}
	
	public void setVelocityDirection(float dirX,float dirY,float dirZ,boolean normalize) {
		if(normalize) {
			float dDist = 1/Util.getDistance(dirX,dirY,dirZ);
			dirX *= dDist;
			dirY *= dDist;
			dirZ *= dDist;
		}
		mVelDirX = dirX;
		mVelDirY = dirY;
		mVelDirZ = dirZ;
	}
	
	public <ParticleType extends EffectParticle, RingBufferType extends AbstractParticleRingBuffer<?,? extends ParticleType>> ParticleType spawnParticle(RingBufferType ringBuffer,float x,float y,float z) {
		ParticleType particle = ringBuffer.spawnParticle(x, y, z, mTextureCoordinates, mFriction);
		particle.setStartScale(mMinScale, mMaxScale);
		particle.setRotationSpeedRange(mMinRotationV, mMaxRotationV, mMirrorRotation && (Math.random()>0.5));
		float vel = mMinSpeed + (float)Math.random()*(mMaxSpeed-mMinSpeed);
		particle.setVelocity(mVelDirX*vel, mVelDirY*vel, mVelDirZ*vel);
		return particle;
	}
	
	public ParticleProperties setScale(float scale) {
		mMinScale = scale;
		mMaxScale = scale;
		return this;
	}
	
	public ParticleProperties setScale(float minScale,float maxScale) {
		mMinScale = minScale;
		mMaxScale = maxScale;
		return this;
	}
	
	public ParticleProperties setSpeed(float speedX) {
		mMinSpeed = speedX;
		mMaxSpeed = speedX;
		return this;
	}
	
	public ParticleProperties setSpeed(float minSpeedX,float maxSpeedY) {
		mMinSpeed = minSpeedX;
		mMaxSpeed = maxSpeedY;
		return this;
	}
	
	public ParticleProperties setRotationSpeed(float speedX) {
		mMinRotationV = speedX;
		mMaxRotationV = speedX;
		return this;
	}
	
	public ParticleProperties setRotationSpeed(float minSpeedX,float maxSpeedY) {
		mMinRotationV = minSpeedX;
		mMaxRotationV = maxSpeedY;
		return this;
	}
	
}
