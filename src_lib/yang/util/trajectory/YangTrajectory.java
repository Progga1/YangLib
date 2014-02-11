package yang.util.trajectory;

public abstract class YangTrajectory {

	public static final float PI = (float)Math.PI;
	public float mGravity = 9.81f;
	public float mMaxVel = Float.MAX_VALUE;
	public float mMinVel = 0;

	protected float mResultTime = 0;
	protected float mResultVelX = 0, mResultVelY = 0;
	protected boolean mReachable = false;

	public abstract void calculate(float targetX,float targetY);

	public YangTrajectory(float gravity) {
		mGravity = gravity;
	}

	public boolean calculate(float targetX,float targetY,float maxSpeed) {
		mMaxVel = maxSpeed;
		calculate(targetX,targetY);
		return mReachable;
	}

	protected void clampVelocity() {
		if(mMaxVel==Float.MAX_VALUE)
			return;

		float absVel = mResultVelX*mResultVelX + mResultVelY*mResultVelY;
		mReachable = absVel>mMaxVel*mMaxVel;
		if(!mReachable) {
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

	public boolean isReachable() {
		return mReachable;
	}

	public void setVelBounds(float minVelocity,float maxVelocity) {
		mMinVel = minVelocity;
		mMaxVel = maxVelocity;
	}

	public void setFixedVel(float velocity) {
		setVelBounds(velocity,velocity);
	}

}
