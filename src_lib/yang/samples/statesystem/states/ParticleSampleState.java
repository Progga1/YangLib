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

public class ParticleSampleState extends SampleState {

	private Particles2D<EffectParticle> mParticles;
	private Texture mParticleTexture;
	private TextureCoordinatesQuad[] mTexCoords;
	private float mCurPntX=0,mCurPntY=0;
	private float mSmokeScale = 1;
	private float mParticleSpeed = 0.5f;
	
	@Override
	protected void initGraphics() {
		mParticles = new Particles2D<EffectParticle>(EffectParticle.class).init(mGraphics2D, 320);
		mParticleTexture = mGFXLoader.getImage("particles",TextureFilter.LINEAR_MIP_LINEAR);
		mParticles.mTexture = mParticleTexture;
		mParticles.mCelShading = 1.1f;
		mTexCoords = TextureCoordinateSet.createTexCoordSequencePixels(mParticleTexture,0, 0, 64, 4);
	}
	
	@Override
	protected void step(float deltaTime) {		
		EffectParticle particle = mParticles.spawnParticle(mCurPntX, mCurPntY, mTexCoords[MathFunc.random(4)]);
		particle.mFriction = 0.995f;
		particle.setSpeedRangeSpread2D(0.04f*mParticleSpeed,0.05f*mParticleSpeed, 1.2f, 0.5f);
		particle.setLifeSteps(170,190);
		particle.mRotation = 0;
		particle.setStartScale(1*mSmokeScale, 1.2f*mSmokeScale);
		particle.shiftPosition2D(0.1f, 0.12f, 0, 2*MathConst.PI);
		particle.setRotationSpeedRange(0.01f, 0.02f, true);
		particle.setAcceleration(-0.00001f*mParticleSpeed, 0.000005f*mParticleSpeed);
		particle.setScaleSpeedRange(-0.025f*mParticleSpeed, -0.038f*mParticleSpeed);
		
		mParticles.step();
	}

	@Override
	protected void draw() {
		mGraphics.clear(0.5f, 0.5f, 0.6f);
		mParticles.draw();
	}

	@Override
	public void pointerDragged(float x,float y,YangPointerEvent event) {
		mCurPntX = x;
		mCurPntY = y;
	}
	
}
