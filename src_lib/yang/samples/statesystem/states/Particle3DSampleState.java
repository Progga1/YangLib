package yang.samples.statesystem.states;

import yang.events.eventtypes.YangPointerEvent;
import yang.graphics.particles.EffectParticle;
import yang.graphics.particles.EffectParticleProperties;
import yang.graphics.particles.Particles3D;
import yang.graphics.particles.Weather3D;
import yang.graphics.textures.TextureCoordinateSet;
import yang.graphics.textures.TextureCoordinatesQuad;
import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.translator.Texture;
import yang.math.MathConst;
import yang.math.MathFunc;
import yang.samples.statesystem.SampleState;
import yang.util.Cursor3D;
import yang.util.DefaultFunctions;

public class Particle3DSampleState extends SampleState {

	private static final int PARTICLES_PER_FRAME = 1;
	
	private Particles3D<EffectParticle> mParticles;
	private Weather3D<Particles3D<EffectParticle>> mWeather = new Weather3D<Particles3D<EffectParticle>>();
	private Texture mParticleTexture;
	private TextureCoordinatesQuad[] mTexCoords;
	private Cursor3D mCursor = new Cursor3D();
	private float mSmokeScale = 0.4f;
	private float mParticleSpeed = 0.4f;
	private float mCamAlpha=0,mCamBeta=0.5f;
	private float mZoom = 2;
	
	
	
	@Override
	protected void initGraphics() {
		mParticles = new Particles3D<EffectParticle>(EffectParticle.class).init(mGraphics3D, 320);
		mParticles.setScaleFunction(DefaultFunctions.EXP_GROWING_SQR_SHRINKING);
		mParticleTexture = mGFXLoader.getImage("cube",TextureFilter.LINEAR_MIP_LINEAR);
		mParticles.mTexture = mParticleTexture;
		mTexCoords = TextureCoordinateSet.createTexCoordSequencePixelsBias(mParticleTexture,0, 0, 64,64, 4, 2,2);
		EffectParticleProperties props = new EffectParticleProperties();
		props.setScale(0.1f);
		props.setSpeed(1f,2f);
		props.setVelocityDirection(0, 1, 0, true);
		mWeather.init(new Particles3D<EffectParticle>(EffectParticle.class).init(mGraphics3D, 1000), props);
		mWeather.mBoundaries.set(-6,6, -16,9, -6,6);
		mWeather.createRandomParticles(1000);
	}
	
	@Override
	protected void step(float deltaTime) {
		for(int i=0;i<PARTICLES_PER_FRAME;i++) {
			
			EffectParticle particle = mParticles.spawnParticle(mCursor.mX,mCursor.mY,mCursor.mZ, mTexCoords[MathFunc.random(4)]);
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
			mWeather.step(deltaTime);
		}
	}

	@Override
	protected void draw() {
		mGraphics.clear(0.3f, 0.3f, 0.6f);
		mGraphics3D.activate();
		mGraphics3D.setPerspectiveProjection(10);
		mGraphics3D.setCameraAlphaBeta(0,0,0, mCamAlpha,mCamBeta, mZoom);

		synchronized(mParticles) {
			mParticles.draw();
			mWeather.draw();
		}
	}

	@Override
	public void pointerDown(float x,float y,YangPointerEvent event) {
		mCursor.jump(x,y,0);
	}
	
	@Override
	public void pointerDragged(float x,float y,YangPointerEvent event) {
		if(event.mButton==YangPointerEvent.BUTTON_MIDDLE || event.mId>0) {
			mCamAlpha -= event.mDeltaX;
			mCamBeta -= event.mDeltaY;
			if(mCamBeta>PI/2*0.99f)
				mCamBeta = PI/2*0.99f;
			if(mCamBeta<-PI/2*0.99f)
				mCamBeta = -PI/2*0.99f;
		}else
			mCursor.set(x, 0, y);
	}
	
	@Override
	public void pointerMoved(float x,float y,YangPointerEvent event) {
		//mCursor.set(x, 0, y);
	}
	
	@Override
	public void zoom(float value) {
		mZoom += value;
	}
	
}
