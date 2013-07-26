package yang.graphics.particles;

import java.util.ListIterator;

import yang.graphics.defaults.Default2DGraphics;
import yang.graphics.programs.BasicProgram;

public abstract class ParticleRingBuffer2D<ParticleType extends Particle> extends AbstractParticleRingBuffer<Default2DGraphics,ParticleType> {

	public BasicProgram mProgram;
	public float mCelShading;
	
	public ParticleRingBuffer2D<ParticleType> init(Default2DGraphics graphics,int maxParticleCount) {
		super.init(graphics,maxParticleCount);
		mProgram = graphics.getDefaultProgram();
		return this;
	}
	
	protected abstract ParticleType createParticle();
	
	protected void drawParticles() {
		mGraphics.setShaderProgram(mProgram);
		mGraphics.setBlack();
		if(mCelShading!=1) {
			for(ParticleType particle:mParticles) {
				if(particle.mExists) {
					float uScale;
					if(mScaleLookUp!=null) {
						uScale = mScaleLookUp.get(particle.mNormLifeTime);
					}else{
						uScale = (1-particle.mNormLifeTime);
					}
					mGraphics.drawRectCentered(particle.mPosX, particle.mPosY, uScale*mCelShading*particle.mScaleX, uScale*mCelShading*particle.mScaleY, particle.mRotation, particle.mTextureCoordinates);
				}
			}
		}
		
		ListIterator<ParticleType> iter = mParticles.listIteratorLast();
		ParticleType particle;
		while((particle=iter.previous())!=null) {
			if(particle.mExists) {
				mGraphics.setColor(particle.mColor);
				float uScale;
				if(mScaleLookUp!=null)
					uScale = mScaleLookUp.get(particle.mNormLifeTime);
				else
					uScale = (1-particle.mNormLifeTime);
				mGraphics.drawRectCentered(particle.mPosX, particle.mPosY, uScale*particle.mScaleX, uScale*particle.mScaleY, particle.mRotation, particle.mTextureCoordinates);
			}
		}
	}
	
}
