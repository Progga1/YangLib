package yang.graphics.particles;

import yang.graphics.textures.TextureCoordinatesQuad;
import yang.graphics.translator.AbstractGraphics;
import yang.graphics.translator.GraphicsTranslator;
import yang.graphics.translator.Texture;
import yang.util.NonConcurrentList;
import yang.util.Util;
import yang.util.lookuptable.LookUpTable;

public abstract class AbstractParticleRingBuffer<GraphicsType extends AbstractGraphics<?>, ParticleType extends Particle> {

	public Texture mTexture;
	protected GraphicsType mGraphics;
	protected GraphicsTranslator mTranslator;
	protected int mCurParticleIndex;
	protected int mMaxParticleCount;
	protected float mGlobalScale;
	public boolean mRotationByVelo;
	public LookUpTable mScaleLookUp;
	public int mParticleCount;
	
	public NonConcurrentList<ParticleType> mParticles;	//TODO arrayList
	protected abstract ParticleType createParticle();
	protected abstract void drawParticles();
	
	public AbstractParticleRingBuffer() {
		mTexture = null;
		mGlobalScale = 1;
		mRotationByVelo = false;
		mScaleLookUp = null;
		mParticleCount = 0;
	}
	
	public AbstractParticleRingBuffer<GraphicsType,ParticleType> init(GraphicsType graphics,int maxParticleCount) {
		mGraphics = graphics;
		mTranslator = graphics.mTranslator;
		mParticles = new NonConcurrentList<ParticleType>();
		mMaxParticleCount = maxParticleCount;
		for(int i=0;i<maxParticleCount;i++)
			mParticles.add(createParticle());
		return this;
	}
	
	public void draw() {
		mTranslator.bindTexture(mTexture);
		
		drawParticles();
	}
	
	public void refreshParticleCount() {
		int particleCount = 0;
		for(ParticleType particle:mParticles) {
			if(particle.mExists)
				particleCount++;
		}
		mParticleCount = particleCount;
	}
	
	public void step() {
		int particleCount = 0;
		for(ParticleType particle:mParticles) {
			if(particle.mExists) {
				particle.step();
				if(mRotationByVelo) {
					particle.mRotation = Util.getAngle(particle.mVelX,particle.mVelY) + Util.F_PI*0.5f;
				}
				particleCount++;
			}
		}
		mParticleCount = particleCount;
	}
	
	public ParticleType spawnParticle(float posX,float posY, float posZ, TextureCoordinatesQuad texCoords, float friction) {
		ParticleType particle = mParticles.get(mCurParticleIndex);
		particle.mExists = true;
		particle.mTextureCoordinates = texCoords;
		particle.mFriction = friction;
		particle.mScale = 1f;
		particle.setPosition(posX, posY, posZ);
		mCurParticleIndex++;
		if(mCurParticleIndex>=mMaxParticleCount)
			mCurParticleIndex = 0;
		return particle;
	}
	
	public ParticleType spawnParticle(float posX,float posY, TextureCoordinatesQuad texCoords, float friction) {
		return spawnParticle(posX,posY,0,texCoords,friction);
	}
	
	public ParticleType spawnParticle(float posX,float posY, TextureCoordinatesQuad texCoords) {
		return spawnParticle(posX,posY,texCoords,0.9995f);
	}
	
	public void removeParticles(int maxAmount) {
		for(ParticleType particle:mParticles) {
			particle.mExists = false;
			maxAmount--;
			if(maxAmount<=0)
				return;
		}
	}
	
	public void clear() {
		for(ParticleType particle:mParticles) {
			particle.mExists = false;
		}
		mCurParticleIndex = 0;
	}
	
}
