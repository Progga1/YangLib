package yang.util.trajectory;

public abstract class YangTrajectory {

	public float mGravity = -9.81f;
	public float mMaxVel = Float.MAX_VALUE;

	protected float mResultTime = 0;
	protected float mResultVelX = 0, mResultVelY = 0;

	public abstract void calculate(float targetX,float targetY);

	protected void clampVelocity() {
		if(mMaxVel==Float.MAX_VALUE)
			return;
		float absVel = mResultVelX*mResultVelX + mResultVelY*mResultVelY;
		if(absVel>mMaxVel*mMaxVel) {
			absVel = 1/(float)Math.sqrt(absVel);
			mResultVelX *= absVel;
			mResultVelY *= absVel;
		}
	}

	public float getResultTime() {
		return mResultTime;
	}

	public float getResultVelX() {
		return mResultVelX;
	}

	public float getResultVelY() {
		return mResultVelY;
	}

}
