package yang.graphics.particles;

import yang.graphics.defaults.Default3DGraphics;
import yang.graphics.defaults.meshcreators.BillboardsCreator;

public abstract class ParticleRingBuffer3D<ParticleType extends Particle>  extends AbstractParticleRingBuffer<Default3DGraphics,ParticleType> {

	public BillboardsCreator mBillboardsCreator;

	@Override
	public ParticleRingBuffer3D<ParticleType> init(Default3DGraphics graphics,int maxParticleCount) {
		super.init(graphics,maxParticleCount);
		mBillboardsCreator = new BillboardsCreator(graphics);
		return this;
	}

	@Override
	protected abstract ParticleType createParticle();

	@Override
	protected void drawParticles() {
		mGraphics.setBlack();

		//mBillboardsCreator.begin();
		for(final ParticleType particle:mParticles) {
			if(particle.mExists) {
				if(mAlphaSpeed==0) {
					mGraphics.setColor(particle.mColor);
				}else{
					if(mAlphaLookUp!=null) {
						mGraphics.setColor(particle.mColor[0],particle.mColor[1],particle.mColor[2],particle.mColor[3]*mAlphaLookUp.get(particle.mNormLifeTime*mAlphaSpeed));
					}else{
						mGraphics.setColor(particle.mColor[0],particle.mColor[1],particle.mColor[2],particle.mColor[3]*(1-particle.mNormLifeTime*mAlphaSpeed));
					}
				}

				float uScale;
				if(mScaleSpeed==0) {
					uScale = 1;
				}else{
					if(mScaleLookUp!=null)
						uScale = mScaleLookUp.get(particle.mNormLifeTime*mScaleSpeed);
					else{
						if(mScaleSpeed<0)
							uScale = (-particle.mNormLifeTime*mScaleSpeed);
						else
							uScale = (1-particle.mNormLifeTime*mScaleSpeed);
					}
				}
				mBillboardsCreator.putBillboardPositionsUniScale(particle.mPosX, particle.mPosY, particle.mPosZ, uScale*particle.mScaleX, particle.mRotation);//TODO include scale Y
				mBillboardsCreator.putTextureCoords(particle.mTextureCoordinates);
				mGraphics.putColorRect(mGraphics.mCurColor);
			}
		}

		mBillboardsCreator.finish();
	}

}
