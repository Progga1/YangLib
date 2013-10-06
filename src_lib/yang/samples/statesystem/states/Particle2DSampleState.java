package yang.samples.statesystem.states;

import yang.events.eventtypes.YangPointerEvent;
import yang.graphics.particles.EffectParticle;
import yang.graphics.particles.Particles2D;
import yang.graphics.textures.TextureCoordinateSet;
import yang.graphics.textures.TextureCoordinatesQuad;
import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.translator.Texture;
import yang.math.MathConst;
import yang.math.MathFunc;
import yang.samples.statesystem.SampleState;
import yang.util.DefaultFunctions;

public class Particle2DSampleState extends SampleState {

	private static final int PARTICLES_PER_FRAME = 1;
	
	private Particles2D<EffectParticle> mParticles;
	private Texture mParticleTexture;
	private TextureCoordinatesQuad[] mTexCoords;
	private float mCurPntX=0,mCurPntY=0,mDelayedPntX=0,mDelayedPntY=0;
	private float mSmokeScale = 0.4f;
	private float mParticleSpeed = 0.4f;
	
	
	
	@Override
	protected void initGraphics() {
		mParticles = new Particles2D<EffectParticle>(EffectParticle.class).init(mGraphics2D, 320);
		mParticles.setScaleFunction(DefaultFunctions.EXP_GROWING_SQR_SHRINKING);
		mParticleTexture = mGFXLoader.getImage("particles",TextureFilter.LINEAR_MIP_LINEAR);
		mParticles.mTexture = mParticleTexture;
		mParticles.mCelShading = 1.14f;
		mTexCoords = TextureCoordinateSet.createTexCoordSequencePixelsBias(mParticleTexture,0, 0, 64,64, 4, 2,2);
	}
	
	@Override
	protected void step(float deltaTime) {
		for(int i=0;i<PARTICLES_PER_FRAME;i++) {
			mDelayedPntX += (mCurPntX-mDelayedPntX)*0.2f;
			mDelayedPntY += (mCurPntY-mDelayedPntY)*0.2f;
			EffectParticle particle = mParticles.spawnParticle(mDelayedPntX, mDelayedPntY, mTexCoords[MathFunc.randomI(4)]);
			particle.mFriction = 0.995f;
			particle.setSpeedRangeSpread2D(5.2f*mParticleSpeed,6.0f*mParticleSpeed, 1.2f, 0.5f);
			particle.setLifeTime(0.7f,0.9f);
			particle.setScale(1*mSmokeScale, 1.2f*mSmokeScale);
			particle.shiftPosition2D(0.05f, 0.08f, 0, 2*MathConst.PI);
			particle.setRotationSpeedRange(1, 1.5f, true);
			particle.setAcceleration(-0.00001f*mParticleSpeed, 0.000005f*mParticleSpeed);
		}
		synchronized(mParticles) {
			mParticles.step();
		}
	}

	@Override
	protected void draw() {
		mGraphics.clear(0.3f, 0.3f, 0.6f);
		synchronized(mParticles) {
			mParticles.draw();
		}
	}

	@Override
	public void pointerDown(float x,float y,YangPointerEvent event) {
		mCurPntX = x;
		mCurPntY = y;
		mDelayedPntX = x;
		mDelayedPntY = y;
	}
	
	@Override
	public void pointerDragged(float x,float y,YangPointerEvent event) {
		mCurPntX = x;
		mCurPntY = y;
	}
	
	@Override
	public void pointerMoved(float x,float y,YangPointerEvent event) {
		mCurPntX = x;
		mCurPntY = y;
	}
	
}
