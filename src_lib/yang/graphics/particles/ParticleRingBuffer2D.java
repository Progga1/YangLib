package yang.graphics.particles;

import java.util.ListIterator;

import yang.graphics.buffers.IndexedVertexBuffer;
import yang.graphics.defaults.Default2DGraphics;
import yang.graphics.defaults.DefaultGraphics;
import yang.graphics.programs.BasicProgram;
import yang.model.DebugYang;

public abstract class ParticleRingBuffer2D<ParticleType extends Particle> extends AbstractParticleRingBuffer<Default2DGraphics,ParticleType> {

	public BasicProgram mProgram;
	public float mCelShading;
	int d = 200;
	
	public ParticleRingBuffer2D<ParticleType> init(Default2DGraphics graphics,int maxParticleCount) {
		super.init(graphics,maxParticleCount);
		mProgram = graphics.getDefaultProgram();
		return this;
	}
	
	protected abstract ParticleType createParticle();
	
	protected void drawParticles() {
		IndexedVertexBuffer vertexBuffer = mGraphics.getCurrentVertexBuffer();
		mGraphics.setShaderProgram(mProgram);
		mGraphics.setBlack();
		if(mCelShading!=1) {
			for(ParticleType particle:mParticles) {
				if(particle.mExists) {
					float uScale;
					if(mScaleSpeed==0) {
						uScale = mCelShading;
					}else{
						if(mScaleLookUp!=null) {
							uScale = mScaleLookUp.get(particle.mNormLifeTime*mScaleSpeed)*mCelShading;
						}else{
							uScale = (1-particle.mNormLifeTime*mScaleSpeed)*mCelShading;
						}
					}
//					mGraphics.drawRectCentered(particle.mPosX, particle.mPosY, uScale*particle.mScaleX, uScale*mCelShading*particle.mScaleY, particle.mRotation, particle.mTextureCoordinates);
					vertexBuffer.beginQuad(false);
					vertexBuffer.putRotatedRect3D(DefaultGraphics.ID_POSITIONS, uScale*particle.mScaleX, uScale*mCelShading*particle.mScaleY, particle.mPosX, particle.mPosY, 0, particle.mRotation);
					vertexBuffer.putArray(DefaultGraphics.ID_TEXTURES, particle.mTextureCoordinates.mAppliedCoordinates);
					vertexBuffer.putArray(DefaultGraphics.ID_COLORS, DefaultGraphics.RECT_BLACK);
					vertexBuffer.putArray(DefaultGraphics.ID_SUPPDATA, DefaultGraphics.RECT_BLACK);
				}
			}
		}
		
		ListIterator<ParticleType> iter = mParticles.listIteratorLast();
		ParticleType particle;
		while((particle=iter.previous())!=null) {
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
						uScale = (1-particle.mNormLifeTime*mScaleSpeed);
					}
				}
//				mGraphics.drawRectCentered(particle.mPosX, particle.mPosY, uScale*particle.mScaleX, uScale*particle.mScaleY, particle.mRotation, particle.mTextureCoordinates);
				vertexBuffer.beginQuad(false);
				vertexBuffer.putRotatedRect3D(DefaultGraphics.ID_POSITIONS, uScale*particle.mScaleX, uScale*mCelShading*particle.mScaleY, particle.mPosX, particle.mPosY, 0, particle.mRotation);
				vertexBuffer.putArray(DefaultGraphics.ID_TEXTURES, particle.mTextureCoordinates.mAppliedCoordinates);
				vertexBuffer.putArrayMultiple(DefaultGraphics.ID_COLORS, particle.mColor, 4);
				vertexBuffer.putArray(DefaultGraphics.ID_SUPPDATA, DefaultGraphics.RECT_BLACK);
			}
		}
		d --;
	}
	
}
