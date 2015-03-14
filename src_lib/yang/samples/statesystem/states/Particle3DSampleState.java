package yang.samples.statesystem.states;

import yang.events.eventtypes.SurfacePointerEvent;
import yang.events.eventtypes.YangSensorEvent;
import yang.graphics.particles.EffectParticle;
import yang.graphics.particles.EffectParticleProperties;
import yang.graphics.particles.Particles3D;
import yang.graphics.particles.Weather3D;
import yang.graphics.textures.TextureCoordinatesQuad;
import yang.graphics.translator.Texture;
import yang.math.MathConst;
import yang.math.MathFunc;
import yang.samples.statesystem.SampleStateCameraControl;
import yang.systemdependent.YangSensor;
import yang.util.Cursor3D;

public class Particle3DSampleState extends SampleStateCameraControl {

	private static final int PARTICLE_COUNT = 800;
	public static boolean BY_SENSOR = true;
	private static boolean ONLY_Z_MOVEMENT = false;
	private static boolean FLIP_Z = false;

	private Particles3D<EffectParticle> mParticles;
	private final Weather3D<Particles3D<EffectParticle>> mWeather = new Weather3D<Particles3D<EffectParticle>>();
	private Texture mParticleTexture;
	private TextureCoordinatesQuad[] mTexCoords;
	private final Cursor3D mCursor = new Cursor3D();
	private final float mSmokeScale = 0.4f;
	private final float mParticleSpeed = 0.1f;
	private int mFrameCount = 0;
	private float mVelX=0, mVelY=0, mVelZ=0;
	private float mGravityX=0, mGravityY=0, mGravityZ=0;
	private float mCurAccX=0,mCurAccY=0,mCurAccZ=0;
	private double mAccTimeStamp = -1;
	private int c = 0;

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
		final Particles3D<EffectParticle> particles = new Particles3D<EffectParticle>(EffectParticle.class);
		particles.mTexture = mGFXLoader.getAlphaMap("light_alpha");
		mWeather.init(particles.init(mGraphics3D, PARTICLE_COUNT), props);
		mWeather.mBoundaries.set(-6,6, -16,9, -6,6);
		mCamera.mOrthogonalProjection = false;

		refreshAccState();

	}

	protected void refreshAccState() {
		final EffectParticleProperties props = mWeather.mParticleProperties;
		mWeather.clear();
		mWeather.resetWind();
		if(BY_SENSOR) {
			mStateSystem.mSensor.startSensor(YangSensor.TYPE_ACCELEROMETER, YangSensor.SPEED_FASTEST);
			props.setSpeed(0);
			props.setVelocityDirection(0,0,-1, false);
		}else{
			props.setSpeed(1f,2f);
			props.setVelocityDirection(0,1,0, true);
		}
		mWeather.createRandomParticles(PARTICLE_COUNT);
		mAccTimeStamp = -1;
		mCurAccX = 0;
		mCurAccY = 0;
		mCurAccZ = 0;
		mVelX = 0;
		mVelY = 0;
		mVelZ = 0;
		mAccTimeStamp = -1;
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

			if(BY_SENSOR) {
				mVelX += mCurAccX * deltaTime;
				mVelY += mCurAccY * deltaTime;
				mVelZ += mCurAccZ * deltaTime;
				float absVel = (float)Math.sqrt(mVelX*mVelX + mVelY*mVelY + mVelZ*mVelZ);
				if(absVel<1.5f) {
					final float fric = 0.99f;
					mVelX *= fric;
					mVelY *= fric;
					mVelZ *= fric;
					absVel *= fric;
				}
				if(ONLY_Z_MOVEMENT)
					mWeather.setAbsoluteSpeed(0,0,absVel);
				else
					mWeather.setAbsoluteSpeed(-mVelX,-mVelY,FLIP_Z?-mVelZ:mVelZ);
			}
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
	public void sensorChanged(YangSensorEvent ev) {
		if(!BY_SENSOR)
			return;
		if(ev.mType==YangSensor.TYPE_ACCELEROMETER) {
			c++;
			if(c%2==0) {
				double curTime = System.nanoTime()*0.000000001f;
				if(mAccTimeStamp<0) {
					mGravityX = ev.mX;
					mGravityY = ev.mY;
					mGravityZ = ev.mZ;
					mCurAccX = 0;
					mCurAccY = 0;
					mCurAccZ = 0;
				}else{
//					float deltaTime = (float)(curTime-mAccTimeStamp);
					mCurAccX = (ev.mX-mGravityX);
					mCurAccY = (ev.mY-mGravityY);
					mCurAccZ = (ev.mZ-mGravityZ);
//					mVelX -= (ev.mX-mGravityX)*deltaTime;
//					mVelY -= (ev.mY-mGravityY)*deltaTime;
//					mVelZ += (ev.mZ-mGravityZ)*deltaTime;
				}
				mAccTimeStamp = curTime;
			}

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
	public void keyDown(int code) {
		if(code=='l') {
			BY_SENSOR ^= true;
			refreshAccState();
		}
		if(code=='f') {
			FLIP_Z ^= true;
		}
		if(code=='z') {
			ONLY_Z_MOVEMENT ^= true;
		}
		if(code=='r') {
			refreshAccState();
		}
	}

	@Override
	public void zoom(float value) {
		super.zoom(value);
	}

}
