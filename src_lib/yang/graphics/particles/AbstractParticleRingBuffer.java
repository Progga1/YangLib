package yang.graphics.particles;

import yang.graphics.textures.TextureCoordinatesQuad;
import yang.graphics.translator.AbstractGraphics;
import yang.graphics.translator.GraphicsTranslator;
import yang.graphics.translator.Texture;
import yang.util.NonConcurrentList;
import yang.util.lookuptable.Function;
import yang.util.lookuptable.LookUpTable;

public abstract class AbstractParticleRingBuffer<GraphicsType extends AbstractGraphics<?>, ParticleType extends Particle> {

	public Texture mTexture;
	protected GraphicsType mGraphics;
	protected GraphicsTranslator mTranslator;
	protected int mCurParticleIndex;
	protected int mMaxParticleCount;
	public float mDefaultScale;
	public LookUpTable mScaleLookUp;
	public int mParticleCount;
	public NonConcurrentList<ParticleType> mParticles;
	public float mDefaultFriction = 0.9995f;
	
	protected abstract ParticleType createParticle();
	protected abstract void drawParticles();
	
	public AbstractParticleRingBuffer() {
		mTexture = null;
		mDefaultScale = 1;
		mScaleLookUp = null;
		mParticleCount = 0;
	}
	
	public AbstractParticleRingBuffer<GraphicsType,ParticleType> init(GraphicsType graphics,int maxParticleCount) {
		mGraphics = graphics;
		mTranslator = graphics.mTranslator;
		mMaxParticleCount = maxParticleCount;
		mParticles = new NonConcurrentList<ParticleType>();
		for(int i=0;i<maxParticleCount;i++)
			mParticles.add(createParticle());
		return this;
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
				particleCount++;
			}
		}
		mParticleCount = particleCount;
	}
	
	public final void draw() {
		if(mParticleCount<=0)
			return;
		mTranslator.bindTexture(mTexture);

		drawParticles();
	}
	
	public ParticleType spawnParticle(float posX,float posY, float posZ, TextureCoordinatesQuad texCoords) {
		ParticleType particle = mParticles.get(mCurParticleIndex);
		particle.mExists = true;
		particle.mLifeTime = 0;
		particle.mTextureCoordinates = texCoords;
		particle.mScale = mDefaultScale;
		particle.setPosition(posX, posY, posZ);
		mCurParticleIndex++;
		if(mCurParticleIndex>=mMaxParticleCount)
			mCurParticleIndex = 0;
		return particle;
	}
	
	public ParticleType spawnParticle(float posX,float posY, TextureCoordinatesQuad texCoords) {
		return spawnParticle(posX,posY,0,texCoords);
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
	
	public void setFunction(Function function,float stepSize) {
		mScaleLookUp = new LookUpTable(0,1,stepSize,function);
	}
	
	public void setScaleFunction(Function function) {
		setFunction(function,0.001f);
	}
	
}
