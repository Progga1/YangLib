package yang.graphics.particles;

import yang.graphics.textures.TextureCoordinatesQuad;
import yang.math.MathFunc;

public class Particle {

	//Properties
	public TextureCoordinatesQuad mTextureCoordinates;
	
	//State
	public boolean mExists;
	public float mPosX,mPosY,mPosZ;
	public float mLifeTime;
	public float mLifeTimeStep;
	public float[] mColor;

	public float mRotation;
	public float mScale;
	
	
	public Particle() {
		mExists = false;
		mColor = new float[4];
		setColor(1,1,1,1);
		mRotation = 0;
		mScale = 1;
		mTextureCoordinates = TextureCoordinatesQuad.FULL_TEXTURE;
		mLifeTimeStep = 0;
	}
	
	public void setPosition(float x, float y, float z) {
		mPosX = x;
		mPosY = y;
		mPosZ = z;
	}
	
	public void setPosition(float x, float y) {
		mPosX = x;
		mPosY = y;
	}
	
	public void setColor(float r,float g,float b,float a) {
		mColor[0] = r;
		mColor[1] = g;
		mColor[2] = b;
		mColor[3] = a;
	}
	
	public void setColor(float[] color) {
		this.mColor = color;
	}
	
	public void derivedStep() { };
	
	public void step() {
		if(!mExists)
			return;
	    mLifeTime+=mLifeTimeStep;
	    
	    derivedStep();
	    
	    if(mLifeTime>1)
	    	mExists = false;
	}
	
	public void setStartScale(float minScale, float maxScale) {
		mScale = MathFunc.random(minScale, maxScale);
	}
	
	public void setLifeSteps(int steps) {
		mLifeTime = 0;
		if(steps<=0)
			mLifeTimeStep = 0;
		else
			mLifeTimeStep = 1f/steps;
	}
	
	public void setLifeSteps(int minSteps,int maxSteps) {
		setLifeSteps(MathFunc.random(minSteps, maxSteps));
	}
	
	public float shiftPosition2D(float minRadius, float maxRadius, float minAngle, float maxAngle) {
		float a = MathFunc.random(minAngle, maxAngle); 
		float r = MathFunc.random(minRadius, maxRadius);
		mPosX += (float)(Math.cos(a)*r);
		mPosY += (float)(Math.sin(a)*r);
		return a;
	}
	
	public float shiftPositionSpread2D(float minRadius, float maxRadius, float direction, float spreadAngle) {
		return shiftPosition2D(minRadius,maxRadius, direction-spreadAngle*0.5f, direction+spreadAngle*0.5f);
	}
	
	public void spawn() {
		mLifeTime = 0;
		mExists = true;
	}
	
	public void kill() {
		mExists = false;
	}

	public void respawn() {
		
	}
	
}
