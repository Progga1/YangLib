package yang.graphics.particles;

import yang.util.Util;

public class EffectParticle extends Particle {

	public float mAlphaV;
	public float mRotV;
	public float mScaleV;
	
	public void setEffects(float rotationSpeed,float scaleSpeed,float alphaSpeed) {
		mRotV = rotationSpeed;
		mScaleV = scaleSpeed;
		mColor[3] = alphaSpeed;
	}
	
	@Override
	public void derivedStep() {
		mColor[3] += mAlphaV;
		mScale += mScaleV;
		mRotation += mRotV;
		
		if(mScale<=0 || mColor[3]<0)
			mExists = false;
	}
	
	public void setScaleSpeedRange(float minSpeed,float maxSpeed) {
		mScaleV = Util.random(minSpeed, maxSpeed);
	}
	
	public void setRotationSpeedRange(float minSpeed,float maxSpeed,boolean mirror) {
		mRotV = Util.random(minSpeed, maxSpeed);
		if(mirror && Math.random()>=0.5f) {
			mRotV = -mRotV;
		}
			
	}
	
}
