package yang.util.trajectory;

public class YangAngleTrajectory extends YangTrajectory {

	@Override
	public void calculate(float targetX,float targetY) {

		float maxVel2 = mMaxVel * mMaxVel;

		float y = targetY;
		float t = (float)Math.sqrt(Math.abs(targetY/mGravity*2));
		float velX = targetX/t;
		float velY = -mGravity*t;
		float vel2 = velX*velX + velY*velY;

		mReachable = vel2<=maxVel2;

		if(!mReachable || targetY<0) {

			boolean mir = targetX<0;
			float x = mir?-targetX:targetX;
			float g = mGravity;
			float v = mMaxVel;
			float v2 = v*v;

			double sqrt = v2*v2 - g*(g*x*x - 2*y*v2);
			mReachable = sqrt>=0;
			if(!mReachable)
				sqrt = 0;
			double atan = (v2 - Math.sqrt(sqrt))/(g*x);

			float a = -(float)Math.atan(atan);

			velX = (float)Math.cos(a)*v;
			velY = (float)Math.sin(a)*v;
			t = x/velX;
			if(mir) {
				velX = -velX;
			}
		}

		mResultVelX = velX;
		mResultVelY = velY;
		mResultTime = t;
	}

}
