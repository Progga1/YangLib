package yang.samples.statesystem.states;

import yang.events.eventtypes.SurfacePointerEvent;
import yang.graphics.particles.EffectParticle;
import yang.graphics.particles.EffectParticleProperties;
import yang.graphics.particles.Particles3D;
import yang.graphics.particles.Weather3D;
import yang.graphics.textures.TextureCoordinatesQuad;
import yang.graphics.translator.Texture;
import yang.math.MathConst;
import yang.math.MathFunc;
import yang.samples.statesystem.SampleStateCameraControl;
import yang.util.Cursor3D;

public class Particle3DSampleState extends SampleStateCameraControl {

	private static final int PARTICLES_PER_FRAME = 1;

	private Particles3D<EffectParticle> mParticles;
	private final Weather3D<Particles3D<EffectParticle>> mWeather = new Weather3D<Particles3D<EffectParticle>>();
	private Texture mParticleTexture;
	private TextureCoordinatesQuad[] mTexCoords;
	private final Cursor3D mCursor = new Cursor3D();
	private final float mSmokeScale = 0.4f;
	private final float mParticleSpeed = 0.1f;
	private int mFrameCount = 0;

	@Override
	protected void initGraphics() {
		super.initGraphics();
		mCursor.set(-1000,0,0);
		mParticles = new Particles3D<EffectParticle>(EffectParticle.class).init(mGraphics3D, 320);
		//mParticles.setScaleFunction(DefaultFunctions.EXP_GROWING_SQR_SHRINKING);
		mParticleTexture = mGFXLoader.getAlphaMap("smoke");
		mParticles.mTexture = mParticleTexture;
		mParticles.mAlphaSpeed = 1;
		mParticles.mScaleSpeed = -1;
		//mTexCoords = TextureCoordinateSet.createTexCoordSequencePixelsBias(mParticleTexture,0, 0, 64,64, 4, 2,2);
		final EffectParticleProperties props = new EffectParticleProperties();
		props.setScale(0.1f);
		props.setSpeed(1f,2f);
		props.setVelocityDirection(0, 1, 0, true);
		final Particles3D<EffectParticle> particles = new Particles3D<EffectParticle>(EffectParticle.class);
		particles.mTexture = mGFXLoader.getAlphaMap("light_alpha");
		mWeather.init(particles.init(mGraphics3D, 1000), props);
		mWeather.mBoundaries.set(-6,6, -16,9, -6,6);
		mWeather.createRandomParticles(1000);
		mCamera.mOrthogonalProjection = false;
	}

	@Override
	protected void step(float deltaTime) {
		super.step(deltaTime);
		if(mFrameCount++ % 1==0) {
			final EffectParticle particle = mParticles.spawnParticle(mCursor.mX,mCursor.mY,mCursor.mZ, TextureCoordinatesQuad.FULL_TEXTURE);
			particle.mFriction = 0.995f;
			particle.setSpeedRangeSpread2D(5.2f*mParticleSpeed,6.0f*mParticleSpeed, 1.2f, 0.5f);
			particle.mVelZ = mParticleSpeed*(MathFunc.randomF(-1, 1))*0.01f;
			particle.setLifeTime(0.2f/mParticleSpeed,0.3f/mParticleSpeed);
			particle.setColor(1, 1, 1, 0.15f);
			particle.setScale(1*mSmokeScale, 1.2f*mSmokeScale);
			particle.shiftPosition2D(0.001f, 0.002f, 0, 2*MathConst.PI);
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
		mGraphics.clear(0.03f, 0.03f, 0.1f);
		mGraphics3D.activate();
		setCamera();

		synchronized(mParticles) {
			mParticles.draw();
			mWeather.draw();
		}
	}

	@Override
	public void pointerDown(float x,float y,SurfacePointerEvent event) {
		super.pointerDown(x,y, event);
	}

	@Override
	public void pointerDragged(float x,float y,SurfacePointerEvent event) {
		super.pointerDragged(x, y, event);
		if(event.mButton==SurfacePointerEvent.BUTTON_LEFT && event.mId==0)
			mCursor.set(x, 0, -y);
	}

	@Override
	public void pointerMoved(float x,float y,SurfacePointerEvent event) {
		super.pointerMoved(x, y, event);
	}

	@Override
	public void zoom(float value) {
		super.zoom(value);
	}

}
