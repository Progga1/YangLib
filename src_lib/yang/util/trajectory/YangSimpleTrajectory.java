package yang.util.trajectory;

public class YangSimpleTrajectory extends YangTrajectory {

	public YangSimpleTrajectory(float gravity) {
		super(gravity);
	}

	@Override
	public void calculate(float targetX,float targetY) {
		float t = (float)Math.sqrt(Math.abs(targetY/mGravity*2));
		mResultVelX = targetX/t;
		mResultVelY = -mGravity*t;
		mResultTime = t;

		clampVelocity();
	}

}
