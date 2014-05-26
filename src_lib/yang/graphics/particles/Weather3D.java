package yang.graphics.particles;

import yang.graphics.defaults.Default3DGraphics;
import yang.graphics.translator.GraphicsTranslator;
import yang.model.Boundaries3D;

public class Weather3D<RingBufferType extends ParticleRingBuffer3D<? extends EffectParticle>> {

	public Default3DGraphics mGraphics3D;
	public GraphicsTranslator mGraphics;
	protected RingBufferType mParticleBuffer;
	public EffectParticleProperties mParticleProperties;
	public boolean mUseWind = false;
	public float mWindForceX,mWindForceY,mWindForceZ;
	public float mWindVelX,mWindVelY,mWindVelZ;
	public float mWindFriction = 0.99f;

	public Boundaries3D mBoundaries;

	public Weather3D(Boundaries3D boundaries) {
		mBoundaries = boundaries;
	}

	public Weather3D() {
		this(new Boundaries3D(1,1,1));
	}

	public Weather3D<RingBufferType> init(RingBufferType ringBuffer,EffectParticleProperties particleProperties) {
		mGraphics3D = ringBuffer.mGraphics;
		mGraphics = mGraphics3D.mTranslator;
		mParticleBuffer = ringBuffer;
		mParticleProperties = particleProperties;
		return this;
	}

	public void createRandomParticles(int amount) {
		for(int i=0;i<amount;i++) {
			EffectParticle particle = mParticleProperties.spawnParticle(mParticleBuffer,mBoundaries.getRandomX(), mBoundaries.getRandomY(), mBoundaries.getRandomZ());
		}
		mParticleBuffer.refreshParticleCount();
	}

	public void step(float deltaTime) {
		if(mUseWind) {
			mWindVelX += mWindForceX;
			mWindVelY += mWindForceY;
			mWindVelZ += mWindForceZ;
			mWindVelX *= mWindFriction;
			mWindVelY *= mWindFriction;
			mWindVelZ *= mWindFriction;
			for(final Object particleObj:mParticleBuffer.mParticles) {
				Particle particle = (Particle)particleObj;
				particle.mPosX += mWindVelX * deltaTime;
				particle.mPosY += mWindVelY * deltaTime;
				particle.mPosZ += mWindVelZ * deltaTime;
			}
		}

		mParticleBuffer.step();
		for(final Object particleObj:mParticleBuffer.mParticles) {
			Particle particle = (Particle)particleObj;

			if(particle.mPosX<mBoundaries.mMinX) {
				float delta = particle.mPosX-mBoundaries.mMinX;
				particle.mPosX = mBoundaries.mMaxX+delta;
				particle.respawn();
			}
			if(particle.mPosX>mBoundaries.mMaxX) {
				float delta = particle.mPosX-mBoundaries.mMaxX;
				particle.mPosX = mBoundaries.mMinX+delta;
				particle.respawn();
			}
			if(particle.mPosY<mBoundaries.mMinY) {
				float delta = particle.mPosY-mBoundaries.mMinY;
				particle.mPosY = mBoundaries.mMaxY+delta;
				particle.respawn();
			}
			if(particle.mPosY>mBoundaries.mMaxY) {
				float delta = particle.mPosY-mBoundaries.mMaxY;
				particle.mPosY = mBoundaries.mMinY+delta;
				particle.respawn();
			}
			if(particle.mPosZ<mBoundaries.mMinZ) {
				float delta = particle.mPosZ-mBoundaries.mMinZ;
				particle.mPosZ = mBoundaries.mMaxZ+delta;
				particle.respawn();
			}
			if(particle.mPosZ>mBoundaries.mMaxZ) {
				float delta = particle.mPosZ-mBoundaries.mMaxZ;
				particle.mPosZ = mBoundaries.mMinZ+delta;
				particle.respawn();
			}
		}
	}

	public void draw() {
		mParticleBuffer.draw();
	}

	public void setWind(float windForceX,float windForceY,float windForceZ) {
		mWindForceX = windForceX;
		mWindForceY = windForceY;
		mWindForceZ = windForceZ;
	}

	public void removeParticles(int maxAmount) {
		mParticleBuffer.removeParticles(maxAmount);
	}

	public void clear() {
		mParticleBuffer.clear();
	}

	public int getParticleCount() {
		return mParticleBuffer.mParticleCount;
	}

}
