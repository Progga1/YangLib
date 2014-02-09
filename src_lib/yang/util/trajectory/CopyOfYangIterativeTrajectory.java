package yang.util.trajectory;

public class CopyOfYangIterativeTrajectory extends YangTrajectory {

	public float mMinVelX = 0;

	@Override
	public void calculate(float targetX, float targetY) {

		float velX = 0.5f;

		float t = targetX/velX;
		float velY = targetY/t - mGravity/2*t;

		mResultVelX = velX;
		mResultVelY = velY;
		mResultTime = t;

	}

}
