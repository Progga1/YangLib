package yang.graphics.particles;

import yang.graphics.textures.TextureCoordinatesQuad;
import yang.graphics.translator.AbstractGraphics;
import yang.graphics.translator.GraphicsTranslator;
import yang.graphics.translator.Texture;
import yang.util.lookuptable.Function;
import yang.util.lookuptable.LookUpTable;

public abstract class AbstractParticleRingBuffer<GraphicsType extends AbstractGraphics<?>, ParticleType extends Particle> {

	public Texture mTexture;
	protected GraphicsType mGraphics;
	protected GraphicsTranslator mTranslator;
	protected int mCurParticleIndex;
	protected int mMaxParticleCount;
	public float mDefaultScale;
	public float mScaleSpeed;
	public float mAlphaSpeed;
	public LookUpTable mScaleLookUp;
	public LookUpTable mAlphaLookUp;
	public int mParticleCount;
	public Object[] mParticles;
	public float mDefaultFriction = 0.9995f;
	public boolean mDebug = false;

	protected abstract ParticleType createParticle();
	protected abstract void drawParticles();

	public AbstractParticleRingBuffer() {
		mTexture = null;
		mDefaultScale = 1;
		mScaleSpeed = 1;
		mAlphaSpeed = 0;
		mScaleLookUp = null;
		mAlphaLookUp = null;
		mParticleCount = 0;
	}

	public AbstractParticleRingBuffer<GraphicsType,ParticleType> init(GraphicsType graphics,int maxParticleCount) {
		mGraphics = graphics;
		mTranslator = graphics.mTranslator;
		mMaxParticleCount = maxParticleCount;
		mParticles = new Object[maxParticleCount];
		for(int i=0;i<maxParticleCount;i++)
			mParticles[i] = createParticle();
		return this;
	}


	public void refreshParticleCount() {
		int particleCount = 0;
		for(final Object particle:mParticles) {
			if(((ParticleType)particle).mExists)
				particleCount++;
		}
		mParticleCount = particleCount;
	}

	public void step() {
		int particleCount = 0;
		for(final Object particleObj:mParticles) {
			ParticleType particle = (ParticleType)particleObj;
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

	protected ParticleType getNextParticle() {
		final ParticleType particle = (ParticleType)mParticles[mCurParticleIndex];
		mCurParticleIndex++;
		if(mCurParticleIndex>=mMaxParticleCount)
			mCurParticleIndex = 0;
		return particle;
	}

	protected ParticleType spawnParticle(float posX,float posY, float posZ, TextureCoordinatesQuad texCoords) {
		final ParticleType particle = getNextParticle();
		particle.spawn(posX, posY, posZ);
		particle.mTextureCoordinates = texCoords;
		particle.mScaleX = mDefaultScale;
		particle.mScaleY = mDefaultScale;

		return particle;
	}

	protected ParticleType spawnParticle(float posX,float posY, TextureCoordinatesQuad texCoords) {
		return spawnParticle(posX,posY,0,texCoords);
	}

	public void removeParticles(int maxAmount) {
		for(final Object particleObj:mParticles) {
			ParticleType particle = (ParticleType)particleObj;
			particle.mExists = false;
			maxAmount--;
			if(maxAmount<=0)
				return;
		}
	}

	public void clear() {
		for(final Object particleObj:mParticles) {
			ParticleType particle = (ParticleType)particleObj;
			particle.mExists = false;
		}
		mCurParticleIndex = 0;
	}

	public void setScaleFunction(Function function,float stepSize) {
		mScaleLookUp = new LookUpTable(0,1,stepSize,function);
	}

	public void setScaleFunction(Function function) {
		setScaleFunction(function,0.001f);
	}

	public void setAlphaFunction(Function function,float stepSize) {
		mAlphaLookUp = new LookUpTable(0,1,stepSize,function);
	}

	public void setAlphaFunction(Function function) {
		setAlphaFunction(function,0.001f);
	}

}
