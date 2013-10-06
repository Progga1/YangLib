package yang.graphics.particles;

import yang.graphics.YangSurface;
import yang.math.MathFunc;

public class EffectParticle extends PhysicalParticle {

	public float mAlphaV;
	public float mRotV;
	
	public void setEffects(float rotationSpeed,float alphaSpeed) {
		mRotV = rotationSpeed;
		mColor[3] = alphaSpeed;
	}
	
	@Override
	public void derivedStep() {
		super.derivedStep();
		mColor[3] += mAlphaV;
		mRotation += mRotV;

		if(mScaleX<=0 || mScaleY<=0 || mColor[3]<0)
			mExists = false;
	}
	
	public void setRotationSpeedRange(float minSpeed,float maxSpeed,boolean mirror) {
		mRotV = MathFunc.randomF(minSpeed, maxSpeed)*YangSurface.deltaTimeSeconds;
		if(mirror && Math.random()>=0.5f) {
			mRotV = -mRotV;
		}
			
	}
	
}
