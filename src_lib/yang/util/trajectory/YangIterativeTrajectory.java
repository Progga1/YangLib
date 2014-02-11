package yang.util.trajectory;

@Deprecated
public class YangIterativeTrajectory extends YangTrajectory {

	public YangIterativeTrajectory(float gravity) {
		super(gravity);
	}

	public float mMinVelX = 0;
	public float mMaxIterations = 500;

	@Override
	public void calculate(float targetX, float targetY) {

		float t;
		float velX;
		float velY;
		float velS = 0;

		float vel = Float.MAX_VALUE;
		float lstVel = 0;
		int c = 0;

		float vY;
		velY = (float)Math.sqrt(-2*mGravity*targetY);
		t = (velS-velY)/mGravity;
		do{
			lstVel = vel;
			velX = targetX/t;
			vel = velX*velX + velY*velY;

			t += 0.01f;
			velY = targetY/t - mGravity/2*t;

			c++;
		}while(c<mMaxIterations && vel>mMaxVel && lstVel>=vel);

		mResultVelX = velX;
		mResultVelY = velY;
		mResultTime = t;

		clampVelocity();
	}

}
