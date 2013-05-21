package yang.graphics.particles;

import yang.graphics.defaults.Default3DGraphics;
import yang.graphics.defaults.meshcreators.BillboardsCreator;

public abstract class ParticleRingBuffer3D<ParticleType extends Particle>  extends AbstractParticleRingBuffer<Default3DGraphics,ParticleType> {
	
	public BillboardsCreator mBillboardsCreator;
	
	public ParticleRingBuffer3D<ParticleType> init(Default3DGraphics graphics,int maxParticleCount) {
		super.init(graphics,maxParticleCount);
		mBillboardsCreator = new BillboardsCreator(graphics);
		return this;
	}
	
	protected abstract ParticleType createParticle();
	
	protected void drawParticles() {
		mGraphics.setBlack();
		
		mBillboardsCreator.begin();
		for(ParticleType particle:mParticles) {
			if(particle.mExists) {
				mGraphics.setColor(particle.mColor);
				float uScale;
				if(mScaleLookUp!=null)
					uScale = mScaleLookUp.get(particle.mNormLifeTime) * particle.mScale;
				else
					uScale = particle.mScale;
				mBillboardsCreator.putBillboardPositionsUniScale(particle.mPosX, particle.mPosY, particle.mPosZ, particle.mScale, particle.mRotation);
				mBillboardsCreator.putTextureCoords(particle.mTextureCoordinates);
			}
		}
		
		mBillboardsCreator.finish();
	}
	
}
