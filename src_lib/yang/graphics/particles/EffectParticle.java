package yang.graphics.particles;

import yang.math.MathFunc;

public class EffectParticle extends PhysicalParticle {

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
		super.derivedStep();
		mColor[3] += mAlphaV;
		mScale += mScaleV;
		mRotation += mRotV;
		
		if(mScale<=0 || mColor[3]<0)
			mExists = false;
	}
	
	public void setScaleSpeedRange(float minSpeed,float maxSpeed) {
		mScaleV = MathFunc.random(minSpeed, maxSpeed);
	}
	
	public void setRotationSpeedRange(float minSpeed,float maxSpeed,boolean mirror) {
		mRotV = MathFunc.random(minSpeed, maxSpeed);
		if(mirror && Math.random()>=0.5f) {
			mRotV = -mRotV;
		}
			
	}
	
}
