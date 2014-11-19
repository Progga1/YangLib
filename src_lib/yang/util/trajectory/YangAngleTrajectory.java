package yang.util.trajectory;

import yang.math.MathConst;
import yang.math.MathFunc;
import yang.model.DebugYang;

public class YangAngleTrajectory extends YangTrajectory {

	public YangAngleTrajectory(float gravity) {
		super(gravity);
	}

	public YangAngleTrajectory() {
		this(9.81f);
	}

	@Override
	public void calculate(float targetX,float targetY) {

		if(!DebugYang.useAlternativeParabola) {
			float maxVel2 = mMaxVel * mMaxVel;
	
			float y = targetY;
			float t = 0;
			float velX = 0;
			float velY = 0;
			float vel2 = Float.MAX_VALUE;
			boolean reachable = true;
	
			boolean fixedVel = targetY<0 || mMinVel==mMaxVel;
			if(!fixedVel) {
				t = (float)Math.sqrt(targetY/mGravity*2);
				velX = targetX/t;
				velY = mGravity*t;
				vel2 = velX*velX + velY*velY;
				reachable = vel2<=maxVel2;
			}
	
			boolean velLowerBound = vel2<mMinVel*mMinVel;
			if(!reachable || fixedVel || velLowerBound) {
	
				boolean mir = targetX<0;
				float x = mir?-targetX:targetX;
				float g = -mGravity;
				float v = velLowerBound?mMinVel:mMaxVel;
				float v2 = v*v;
	
				double sqrt = v2*v2 - g*(g*x*x - 2*y*v2);
				reachable = sqrt>=0;
				if(!reachable)
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
			mReachable = reachable;
			
		} else {
			boolean jumpDown = targetY < 0; 
			if(jumpDown) {
				targetX = -targetX;
				targetY = -targetY;
			}
			
			boolean flip = targetX < 0;
			
			if(flip)  {
				targetX = -targetX;			
			}
			
			float relAngle = 22.5f;
			
			float ang = (float) Math.atan2(targetY, targetX);		
			float impAng = MathConst.PI - ang + relAngle /180f*MathConst.PI;		
//			impAng = 20.0f / 180*MathConst.PI;
			
			
			float velXint = (-mGravity *0.5f *targetX) / (-targetY/targetX - (float) Math.tan(impAng));
			
			
			
			float velX = MathFunc.sqrt( velXint);
			float velY = (float)-(Math.tan(impAng)*velX - mGravity*targetX/velX);
			
			boolean reachable = true;		
			
			if(flip)  {
				velX = -velX;
				
			} else if(jumpDown)  {
				velX = -velX;			
			}
			
			float t = targetX / velX;
			
			if(jumpDown) {			
				velY = mGravity * t + velY;
				
				velX = -velX;
				velY = -velY;
			}				
	
			if(!flip && jumpDown)  {
				velX = -velX;			
			}
			
			float norm = mMaxVel/MathFunc.sqrt(velX*velX + velY *velY);
			if(norm < 1) {			
				velX *= norm;
				velY *= norm;
				reachable = false;
			}			
					
			mResultVelX = velX;
			mResultVelY = velY;
			mResultTime = t;
			mReachable = reachable;
		}
	}

}
