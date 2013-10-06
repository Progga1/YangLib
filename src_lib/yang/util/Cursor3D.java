package yang.util;

import yang.math.objects.Vector3f;

public class Cursor3D extends Vector3f{

	public float mDelay = 0.5f;
	public float mDelayedX,mDelayedY,mDelayedZ;
	
	public void step(float deltaTime) {
		mDelayedX += (mX-mDelayedX)*mDelay;
		mDelayedY += (mY-mDelayedY)*mDelay;
		mDelayedZ += (mZ-mDelayedZ)*mDelay;
	}

	public void jump(float x, float y, float z) {
		set(x,y,z);
		setDelayedPos(x,y,z);
	}
	
	public void setDelayedPos(float x, float y, float z) {
		mDelayedX = x;
		mDelayedY = y;
		mDelayedZ = z;
	}
	
}
