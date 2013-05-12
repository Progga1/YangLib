package yang.samples.statesystem.states;

import yang.events.eventtypes.YangPointerEvent;
import yang.graphics.particles.EffectParticle;
import yang.graphics.particles.Particles2D;
import yang.graphics.textures.TextureCoordinateSet;
import yang.graphics.textures.TextureCoordinatesQuad;
import yang.graphics.textures.enums.TextureFilter;
import yang.graphics.translator.Texture;
import yang.math.MathFunc;
import yang.samples.statesystem.SampleState;

public class ParticleSampleState extends SampleState {

	private Particles2D<EffectParticle> mParticles;
	private Texture mParticleTexture;
	private TextureCoordinatesQuad[] mTexCoords;
	private float mCurPntX,mCurPntY;
	
	@Override
	protected void initGraphics() {
		mParticles = new Particles2D<EffectParticle>(EffectParticle.class).init(mGraphics2D, 320);
		mParticleTexture = mGFXLoader.getImage("particles",TextureFilter.LINEAR_MIP_LINEAR);
		mTexCoords = TextureCoordinateSet.createTexCoordSequencePixels(mParticleTexture,0, 0, 64, 4);
	}
	
	@Override
	protected void step(float deltaTime) {
		EffectParticle particle = mParticles.spawnParticle(mCurPntX, mCurPntY, mTexCoords[MathFunc.random(4)]);
		particle.setColor(1, 1, 1, 1);
		particle.setScaleSpeedRange(0.01f, 0.02f);
		particle.mRotationByVelo = true;
	}

	@Override
	protected void draw() {
		mParticles.draw();
	}

	@Override
	public void pointerDragged(float x,float y,YangPointerEvent event) {
		mCurPntX = x;
		mCurPntY = y;
	}
	
}
