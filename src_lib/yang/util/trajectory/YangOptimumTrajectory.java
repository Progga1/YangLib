package yang.util.trajectory;

@Deprecated
public class YangOptimumTrajectory extends YangTrajectory {

	public YangOptimumTrajectory(float gravity) {
		super(gravity);
	}

	public float mMinVelX = 0;

	@Override
	public void calculate(float targetX, float targetY) {

		float velY = (float)Math.sqrt(-2*mGravity*targetY);
		float t = -velY/mGravity;
		float velX = targetX/t;

		mResultVelX = velX;
		mResultVelY = velY;
		mResultTime = t;

		clampVelocity();
	}

}
